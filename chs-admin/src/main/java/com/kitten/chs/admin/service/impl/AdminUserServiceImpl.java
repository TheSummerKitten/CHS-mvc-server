package com.kitten.chs.admin.service.impl;

import com.kitten.chs.admin.model.vo.req.UpdatePasswordReqVO;
import com.kitten.chs.admin.model.vo.rsp.FindUserInfoRspVO;
import com.kitten.chs.admin.service.AdminUserService;
import com.kitten.chs.common.domain.mapper.UserMapper;
import com.kitten.chs.common.domain.mapper.UserRoleMapper;
import com.kitten.chs.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

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

}
