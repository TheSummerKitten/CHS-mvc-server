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

import static org.apache.commons.lang3.compare.ComparableUtils.ge;

/**
 * @author kitten
 */
@Slf4j
@Service
public class AdminUserServiceImpl implements AdminUserService {

    // security
    @Autowired
    private PasswordEncoder passwordEncoder;

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
        //1.加密
        String password = passwordEncoder.encode(reqVO.getPassword());
        log.info("encode password: {}", password);
        //2.更新
        int count = userMapper.updateByUsernameAndPassword(reqVO.getUsername(), password);
        if (count != 1) {
            return Response.fail("更新密码失败");
        }
        return Response.success("修改成功");
    }

    /**
     * 获取当前用户信息
     * @return
     */
    @Override
    public Response<FindUserInfoRspVO> findCurrentUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String name = authentication.getName();
        FindUserInfoRspVO rspVO = FindUserInfoRspVO.builder()
                .username(name)
                .build();

        return Response.success(rspVO);
    }

    /**
     * 分页条件查询
     * @param reqVO
     * @return
     */
    @Override
    public PageResponse<FindUserPageConListRespVO> findUserPageConditionList(FindUserPageConListReqVO reqVO) {
        Long current = reqVO.getCurrent();
        Long size = reqVO.getSize();

        // 分页对象(查询第几页、每页多少数据)
        Page<UserDO> page = new Page<>(current, size);

        // 构建查询条件
//        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<>();
//
//        String name = reqVO.getUsername();
//        LocalDate startDate = reqVO.getStartDate();
//        LocalDate endDate = reqVO.getEndDate();
//
//        wrapper.like(StringUtils.isNotBlank(name), UserDO::getUsername, name.trim())
//                .ge(Objects.nonNull(startDate), UserDO::getCreateTime, startDate)
//                .le(Objects.nonNull(endDate), UserDO::getCreateTime, endDate)
//                .orderByDesc(UserDO::getCreateTime);
//
//        Page<UserDO> userDOPage = userMapper.selectPage(page, wrapper);

//        Page<UserDO> userDOPage = userMapper.selectPageExcludeAdmin(
//                page,
//                reqVO.getUsername(),
//                reqVO.getStartDate(),
//                reqVO.getEndDate()
//        );

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
                    .map(userDO -> FindUserPageConListRespVO.builder()
                            .id(userDO.getId())
                            .name(userDO.getUsername())
                            .phone(userDO.getPhone())
                            .createTime(userDO.getCreateTime())
                            .build())
                    .collect(Collectors.toList());
        }

        return PageResponse.success(userDOPage, vos);
    }

    /**
     * 根据id删除用户
     * @param reqVO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Response<?> deleteUser(DeleteUserReqVO reqVO) {
        Integer userId = reqVO.getId();
        UserDO user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException(ResponseCodeEnum.USER_DELETE_ERROR);
        }
        String username = user.getUsername();
        // 2. 删除 user_role 表中 username 对应的记录
        userRoleMapper.delete(new LambdaQueryWrapper<UserRoleDO>()
                .eq(UserRoleDO::getUsername, username));
        // 3.删除用户
        userMapper.deleteById(userId);

        return Response.success();
    }

    // update user info
    @Override
    public Response<?> updateUserInfo(UpdateUserReqVO reqVO) {
        if (reqVO.getId() == null) {
            return Response.fail(ResponseCodeEnum.PARAM_NOT_VALID);
        }
        if (StringUtils.isBlank(reqVO.getUsername()) && StringUtils.isBlank(reqVO.getPhone())) {
            return Response.fail(ResponseCodeEnum.UPDATE_USER_REQ_PARAM_INVALID);
        }

        // 2. 检查用户是否存在
        UserDO existingUser = userMapper.selectById(reqVO.getId());
        if (existingUser == null) {
            return Response.fail(ResponseCodeEnum.USER_NOT_EXIST);
        }

        // 3. 构建更新对象
        UserDO updateUser = UserDO.builder()
                .id(reqVO.getId())
                .username(StringUtils.isNotBlank(reqVO.getUsername()) ? reqVO.getUsername() : existingUser.getUsername())
                .phone(StringUtils.isNotBlank(reqVO.getPhone()) ? reqVO.getPhone() : existingUser.getPhone())
                .updateTime(LocalDateTime.now())
                .build();

        // 4. 执行更新
        int result = userMapper.updateById(updateUser);
        if (result <= 0) {
            return Response.fail(ResponseCodeEnum.SYSTEM_ERROR);
        }

        // 5. 返回成功响应
        return Response.success("更新用户信息成功");
    }

}
