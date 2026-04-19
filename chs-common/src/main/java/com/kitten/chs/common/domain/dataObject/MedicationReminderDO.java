package com.kitten.chs.common.domain.dataObject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_medication_reminder")
public class MedicationReminderDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long healthRecordId;

    private Long userId;

    private String medicineName;

    private String dosage;

    private Integer frequency;

    private String course;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Boolean isDeleted;

}
