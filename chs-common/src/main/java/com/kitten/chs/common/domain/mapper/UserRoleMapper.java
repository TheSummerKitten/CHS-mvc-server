package com.kitten.chs.common.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kitten.chs.common.domain.dataObject.UserRoleDO;

import java.util.List;

/**
 * @author kitten
 */
public interface UserRoleMapper extends BaseMapper<UserRoleDO> {

    // 根据用户名查询对应 UserRole列表
    default List<UserRoleDO> selectListByUsername(String username) {
        LambdaQueryWrapper<UserRoleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRoleDO::getUsername, username);

        return selectList(wrapper);
    }

}
