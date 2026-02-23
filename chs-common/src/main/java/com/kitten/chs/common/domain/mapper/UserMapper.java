package com.kitten.chs.common.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kitten.chs.common.domain.dataObject.UserDO;

/**
 * @author kitten
 */
public interface UserMapper extends BaseMapper<UserDO> {

    // 根据用户名查询用户信息
    default UserDO findByUsername(String username) {
        return selectOne(new LambdaQueryWrapper<UserDO>().eq(UserDO::getUsername, username));
    }

}
