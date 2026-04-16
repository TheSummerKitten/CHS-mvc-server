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
public class FindRechargeListRespVO {

    private Long id;

    private String orderNo;

    private BigDecimal amount;

    private Integer status;

    private LocalDateTime payTime;

    private String tradeNo;

    private LocalDateTime createTime;

}
