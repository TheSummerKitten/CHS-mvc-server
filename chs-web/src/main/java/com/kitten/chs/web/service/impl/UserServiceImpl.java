package com.kitten.chs.web.service.impl;

import com.kitten.chs.common.domain.dataObject.UserDO;
import com.kitten.chs.common.domain.dataObject.UserRoleDO;
import com.kitten.chs.common.domain.mapper.UserMapper;
import com.kitten.chs.common.domain.mapper.UserRoleMapper;
import com.kitten.chs.common.utils.Response;
import com.kitten.chs.web.model.req.RegisterReqVO;
import com.kitten.chs.web.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Objects;

/**
 * @author kitten
 */
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

            try {
                // 新增用户
                int count = userMapper.insert(newUserDO);
                if (count == 1) {
                    // 新增对应角色
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

}
