package com.kitten.chs.common.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kitten.chs.common.domain.dataObject.UserDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    /**
     * 分页查询用户，排除 ROLE_ADMIN 角色
     * @param page
     * @param username
     * @param startDate
     * @param endDate
     * @return
     */
    Page<UserDO> selectPageExcludeAdmin(Page<UserDO> page,
                                        @Param("username") String username,
                                        @Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);

    @Update("UPDATE t_user SET balance = balance - #{amount}, update_time = NOW() " +
            "WHERE username = #{username} AND balance >= #{amount} AND is_deleted = 0")
    int deductBalance(@Param("username") String username, @Param("amount") BigDecimal amount);

}
