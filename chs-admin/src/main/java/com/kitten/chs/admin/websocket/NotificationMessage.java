package com.kitten.chs.admin.websocket;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationMessage {

    public static final String TYPE_NEW_ORDER = "NEW_ORDER";
    public static final String TYPE_ORDER_ACCEPTED = "ORDER_ACCEPTED";
    public static final String TYPE_ORDER_REJECTED = "ORDER_REJECTED";
    public static final String TYPE_ORDER_COMPLETED = "ORDER_COMPLETED";
    public static final String TYPE_ORDER_CANCELLED = "ORDER_CANCELLED";

    private String type;
    private String title;
    private String message;
    private Long orderId;
    private String orderNo;
    private String rejectReason;
    private Long timestamp;

    public static NotificationMessage of(String type, String title, String message, Long orderId, String orderNo) {
        return NotificationMessage.builder()
                .type(type)
                .title(title)
                .message(message)
                .orderId(orderId)
                .orderNo(orderNo)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}