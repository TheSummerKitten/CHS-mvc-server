package com.kitten.chs.common.domain.dataObject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_order_item")
public class OrderItemDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private Long foodId;

    private String foodName;

    private BigDecimal foodPrice;

    private Integer quantity;

    private BigDecimal subtotal;

    private LocalDateTime createTime;

}
