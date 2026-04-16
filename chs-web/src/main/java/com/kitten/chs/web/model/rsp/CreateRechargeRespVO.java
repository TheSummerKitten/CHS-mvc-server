package com.kitten.chs.web.model.rsp;

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
public class CreateRechargeRespVO {

    private Long id;

    private String orderNo;

    private BigDecimal amount;

    private Integer status;

    private LocalDateTime createTime;

    private String payUrl;

}
