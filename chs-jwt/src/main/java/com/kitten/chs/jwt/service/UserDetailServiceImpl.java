package com.kitten.chs.jwt.service;

import com.kitten.chs.common.domain.dataObject.UserDO;
import com.kitten.chs.common.domain.dataObject.UserRoleDO;
import com.kitten.chs.common.domain.mapper.UserMapper;
import com.kitten.chs.common.domain.mapper.UserRoleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author kitten
 */
@Slf4j
@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDO userDO = userMapper.findByUsername(username);
        if (Objects.isNull(userDO)) {
            throw new UsernameNotFoundException("该用户不存在");
        }

        List<UserRoleDO> userRoleDOS = userRoleMapper.selectListByUsername(username);
        String[] roleArr = null;
        if (!CollectionUtils.isEmpty(userRoleDOS)) {
            List<String> collect = userRoleDOS.stream().map(p -> p.getRole()).collect(Collectors.toList());
            roleArr = collect.toArray(new String[collect.size()]);
        }

        return User.withUsername(userDO.getUsername())
                .password(userDO.getPassword())
                .authorities(roleArr)
                .build();
    }
}
