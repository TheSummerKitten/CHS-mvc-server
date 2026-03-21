package com.kitten.chs.common.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kitten.chs.common.domain.dataObject.UserDO;

import java.time.LocalDateTime;

/**
 * @author kitten
 */
public interface UserMapper extends BaseMapper<UserDO> {

    // 根据用户名查询用户信息
    default UserDO findByUsername(String username) {
        return selectOne(new LambdaQueryWrapper<UserDO>().eq(UserDO::getUsername, username));
    }

    // 更新密码
    default int updateByUsernameAndPassword(String username, String password) {
        LambdaUpdateWrapper<UserDO> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(UserDO::getPassword, password);
        wrapper.set(UserDO::getUpdateTime, LocalDateTime.now());

        wrapper.eq(UserDO::getUsername, username);
        return update(null, wrapper);
    }

    default UserDO selectByUsername(String username) {
        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDO::getUsername, username);

        return selectOne(wrapper);
    };
}
