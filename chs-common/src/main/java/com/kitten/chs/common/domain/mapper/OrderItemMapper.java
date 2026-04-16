package com.kitten.chs.common.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kitten.chs.common.domain.dataObject.OrderItemDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderItemMapper extends BaseMapper<OrderItemDO> {

    List<OrderItemDO> selectByOrderIds(@Param("orderIds") List<Long> orderIds);

}
