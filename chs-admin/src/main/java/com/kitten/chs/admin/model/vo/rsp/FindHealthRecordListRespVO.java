package com.kitten.chs.admin.model.vo.rsp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindHealthRecordListRespVO {

    private Long id;

    private Long userId;

    private String username;

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

    private LocalDateTime createTime;

}
