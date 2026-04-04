package com.kitten.chs.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kitten.chs.admin.model.vo.req.DeleteUserReqVO;
import com.kitten.chs.admin.model.vo.req.FindUserPageConListReqVO;
import com.kitten.chs.admin.model.vo.req.UpdatePasswordReqVO;
import com.kitten.chs.admin.model.vo.req.UpdateUserReqVO;
import com.kitten.chs.admin.model.vo.rsp.FindUserInfoRspVO;
import com.kitten.chs.admin.model.vo.rsp.FindUserPageConListRespVO;
import com.kitten.chs.admin.service.AdminUserService;
import com.kitten.chs.common.config.MinioConfig;
import com.kitten.chs.common.domain.dataObject.UserDO;
import com.kitten.chs.common.domain.dataObject.UserRoleDO;
import com.kitten.chs.common.domain.mapper.UserMapper;
import com.kitten.chs.common.domain.mapper.UserRoleMapper;
import com.kitten.chs.common.enums.ResponseCodeEnum;
import com.kitten.chs.common.exception.BizException;
import com.kitten.chs.common.utils.PageResponse;
import com.kitten.chs.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AdminUserServiceImpl implements AdminUserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MinioConfig minioConfig;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;


    @Override
    public Response<?> updatePassword(UpdatePasswordReqVO reqVO) {
        if (!reqVO.getPassword().equals(reqVO.getConfirmPassword())) {
            return Response.fail("输入密码和确认密码不一致");
        }
        String password = passwordEncoder.encode(reqVO.getPassword());
        log.info("encode password: {}", password);
        int count = userMapper.updateByUsernameAndPassword(reqVO.getUsername(), password);
        if (count != 1) {
            return Response.fail("更新密码失败");
        }
        return Response.success("修改成功");
    }

    @Override
    public Response<FindUserInfoRspVO> findCurrentUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String name = authentication.getName();
        FindUserInfoRspVO rspVO = FindUserInfoRspVO.builder()
                .username(name)
                .build();

        return Response.success(rspVO);
    }

    @Override
    public PageResponse<FindUserPageConListRespVO> findUserPageConditionList(FindUserPageConListReqVO reqVO) {
        Long current = reqVO.getCurrent();
        Long size = reqVO.getSize();

        Page<UserDO> page = new Page<>(current, size);

        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<>();
        String name = reqVO.getUsername();
        LocalDate startDate = reqVO.getStartDate();
        LocalDate endDate = reqVO.getEndDate();
        wrapper.like(StringUtils.isNotBlank(name), UserDO::getUsername, name.trim())
                .ge(Objects.nonNull(startDate), UserDO::getCreateTime, startDate)
                .le(Objects.nonNull(endDate), UserDO::getCreateTime, endDate)
                .orderByDesc(UserDO::getCreateTime);
        wrapper.notExists(" (select 1 from chs.t_user_role ur where ur.username = chs.t_user.username and ur.role = 'ROLE_ADMIN' ) ");
        Page<UserDO> userDOPage = userMapper.selectPage(page, wrapper);

        List<UserDO> userDOS = userDOPage.getRecords();

        List<FindUserPageConListRespVO> vos = null;
        if (!CollectionUtils.isEmpty(userDOS)) {
            vos = userDOS.stream()
                    .map(userDO -> {
                        String avatarUrl = null;
                        if (StringUtils.isNotBlank(userDO.getAvatar())) {
                            avatarUrl = minioConfig.getEndpoint() + "/" + minioConfig.getBucketName() + "/" + userDO.getAvatar();
                        }
                        return FindUserPageConListRespVO.builder()
                                .id(userDO.getId())
                                .name(userDO.getUsername())
                                .phone(userDO.getPhone())
                                .gender(userDO.getGender())
                                .avatar(avatarUrl)
                                .createTime(userDO.getCreateTime())
                                .build();
                    })
                    .collect(Collectors.toList());
        }

        return PageResponse.success(userDOPage, vos);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Response<?> deleteUser(DeleteUserReqVO reqVO) {
        Integer userId = reqVO.getId();
        UserDO user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException(ResponseCodeEnum.USER_DELETE_ERROR);
        }
        String username = user.getUsername();
        userRoleMapper.delete(new LambdaQueryWrapper<UserRoleDO>()
                .eq(UserRoleDO::getUsername, username));
        userMapper.deleteById(userId);

        return Response.success();
    }

    @Override
    public Response<?> updateUserInfo(UpdateUserReqVO reqVO) {
        if (reqVO.getId() == null) {
            return Response.fail(ResponseCodeEnum.PARAM_NOT_VALID);
        }
        if (StringUtils.isBlank(reqVO.getUsername()) && StringUtils.isBlank(reqVO.getPhone())) {
            return Response.fail(ResponseCodeEnum.UPDATE_USER_REQ_PARAM_INVALID);
        }

        UserDO existingUser = userMapper.selectById(reqVO.getId());
        if (existingUser == null) {
            return Response.fail(ResponseCodeEnum.USER_NOT_EXIST);
        }

        UserDO updateUser = UserDO.builder()
                .id(reqVO.getId())
                .username(StringUtils.isNotBlank(reqVO.getUsername()) ? reqVO.getUsername() : existingUser.getUsername())
                .phone(StringUtils.isNotBlank(reqVO.getPhone()) ? reqVO.getPhone() : existingUser.getPhone())
                .gender(reqVO.getGender() != null ? reqVO.getGender() : existingUser.getGender())
                .updateTime(LocalDateTime.now())
                .build();

        int result = userMapper.updateById(updateUser);
        if (result <= 0) {
            return Response.fail(ResponseCodeEnum.SYSTEM_ERROR);
        }

        return Response.success("更新用户信息成功");
    }

}
