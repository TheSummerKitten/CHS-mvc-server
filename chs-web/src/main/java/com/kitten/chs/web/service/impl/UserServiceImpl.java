package com.kitten.chs.web.service.impl;

import com.kitten.chs.common.config.MinioConfig;
import com.kitten.chs.common.domain.dataObject.UserDO;
import com.kitten.chs.common.domain.dataObject.UserRoleDO;
import com.kitten.chs.common.domain.mapper.UserMapper;
import com.kitten.chs.common.domain.mapper.UserRoleMapper;
import com.kitten.chs.common.utils.Response;
import com.kitten.chs.web.model.req.RegisterReqVO;
import com.kitten.chs.web.model.req.UpdateSelfInfoReqVO;
import com.kitten.chs.web.model.rsp.UserInfoRspVO;
import com.kitten.chs.web.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MinioConfig minioConfig;

    @Override
    public Response<?> register(RegisterReqVO reqVO) {
        if (reqVO.getUsername() == null || reqVO.getPassword() == null) {
            return Response.fail("用户名或密码不能为空");
        }
        UserDO userDO = userMapper.selectByUsername(reqVO.getUsername());
        if (!Objects.isNull(userDO)) {
            return Response.fail("用户名已存在");
        }

        UserRoleDO userRoleDO = UserRoleDO.builder()
                .username(reqVO.getUsername())
                .role("ROLE_USER")
                .build();

        Boolean isSuccess = Boolean.TRUE.equals(transactionTemplate.execute(status -> {

            UserDO newUserDO = new UserDO();
            newUserDO.setPassword(passwordEncoder.encode(reqVO.getPassword()));
            newUserDO.setUsername(reqVO.getUsername());
            newUserDO.setPhone(reqVO.getPhone());
            newUserDO.setGender(reqVO.getGender() != null ? reqVO.getGender() : 0);

            try {
                int count = userMapper.insert(newUserDO);
                if (count == 1) {
                    count = userRoleMapper.insert(userRoleDO);
                }
                return true;
            } catch (Exception e) {
                status.setRollbackOnly();
                log.error("新增用户异常: {}", e);
            }
            return false;
        }));

        return Response.success();
    }

    @Override
    public Response<UserInfoRspVO> getCurrentUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        UserDO userDO = userMapper.selectByUsername(username);
        if (userDO == null) {
            return Response.fail("用户不存在");
        }
        
        String avatarUrl = null;
        if (StringUtils.isNotBlank(userDO.getAvatar())) {
            avatarUrl = minioConfig.getEndpoint() + "/" + minioConfig.getBucketName() + "/" + userDO.getAvatar();
        }
        
        UserInfoRspVO rspVO = UserInfoRspVO.builder()
                .id(userDO.getId())
                .username(userDO.getUsername())
                .phone(userDO.getPhone())
                .balance(userDO.getBalance())
                .gender(userDO.getGender())
                .avatar(avatarUrl)
                .build();
        
        return Response.success(rspVO);
    }

    @Override
    public Response<?> updateSelfInfo(UpdateSelfInfoReqVO reqVO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        UserDO existingUser = userMapper.selectByUsername(username);
        if (existingUser == null) {
            return Response.fail("用户不存在");
        }
        
        UserDO updateUser = UserDO.builder()
                .id(existingUser.getId())
                .phone(StringUtils.isNotBlank(reqVO.getPhone()) ? reqVO.getPhone() : existingUser.getPhone())
                .gender(reqVO.getGender() != null ? reqVO.getGender() : existingUser.getGender())
                .avatar(StringUtils.isNotBlank(reqVO.getAvatar()) ? reqVO.getAvatar() : existingUser.getAvatar())
                .updateTime(LocalDateTime.now())
                .build();
        
        int result = userMapper.updateById(updateUser);
        if (result <= 0) {
            return Response.fail("更新失败");
        }
        
        return Response.success("更新成功");
    }

}
