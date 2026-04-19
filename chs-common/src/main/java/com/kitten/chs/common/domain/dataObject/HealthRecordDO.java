package com.kitten.chs.common.domain.dataObject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("t_health_record")
public class HealthRecordDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

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

    private Long creatorId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Boolean isDeleted;

}
