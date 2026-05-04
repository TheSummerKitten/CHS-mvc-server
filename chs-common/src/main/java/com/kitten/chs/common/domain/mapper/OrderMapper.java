package com.kitten.chs.common.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kitten.chs.common.domain.dataObject.OrderDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

public interface OrderMapper extends BaseMapper<OrderDO> {

    @Select("SELECT COALESCE(SUM(total_amount), 0) FROM t_order " +
            "WHERE username = #{username} AND is_deleted = 0 AND status != 3 " +
            "AND YEAR(create_time) = #{year} AND MONTH(create_time) = #{month}")
    BigDecimal sumMonthlyConsumption(@Param("username") String username, 
                                     @Param("year") int year, 
                                     @Param("month") int month);

    @Select("SELECT COUNT(*) FROM t_order " +
            "WHERE username = #{username} AND is_deleted = 0 AND status != 3 " +
            "AND YEAR(create_time) = #{year} AND MONTH(create_time) = #{month}")
    Integer countMonthlyOrders(@Param("username") String username, 
                               @Param("year") int year, 
                               @Param("month") int month);

}
