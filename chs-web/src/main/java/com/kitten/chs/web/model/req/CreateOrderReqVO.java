package com.kitten.chs.web.model.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateOrderReqVO {

    private List<OrderItem> items;

    private String receiverName;

    private String receiverPhone;

    private String receiverAddress;

    private String remark;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class OrderItem {
        private Long foodId;
        private Integer quantity;
    }

}
