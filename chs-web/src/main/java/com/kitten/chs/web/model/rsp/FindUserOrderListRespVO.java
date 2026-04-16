package com.kitten.chs.web.model.rsp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindUserOrderListRespVO {

    private Long id;

    private String orderNo;

    private BigDecimal totalAmount;

    private String receiverName;

    private String receiverPhone;

    private String receiverAddress;

    private Integer status;

    private String remark;

    private String rejectReason;

    private LocalDateTime createTime;

    private List<OrderItemVO> items;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class OrderItemVO {
        private Long id;
        private String foodName;
        private BigDecimal foodPrice;
        private Integer quantity;
        private BigDecimal subtotal;
    }
}
