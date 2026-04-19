package com.kitten.chs.web.model.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateHealthRecordReqVO {

    private Long id;

    private LocalDate recordDate;

    private BigDecimal height;

    private BigDecimal weight;

    private Integer bloodPressureSystolic;

    private Integer bloodPressureDiastolic;

    private Integer heartRate;

    private String symptoms;

    private String medicalHistory;

    private String diagnosis;

    private String remark;

}
