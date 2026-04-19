package com.kitten.chs.admin.model.vo.rsp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindMedicationReminderListRespVO {

    private Long id;

    private Long healthRecordId;

    private String medicineName;

    private String dosage;

    private Integer frequency;

    private String course;

    private String remark;

    private LocalDateTime createTime;

}
