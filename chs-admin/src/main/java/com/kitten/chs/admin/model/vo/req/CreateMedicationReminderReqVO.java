package com.kitten.chs.admin.model.vo.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateMedicationReminderReqVO {

    private Long healthRecordId;

    private Long userId;

    private String medicineName;

    private String dosage;

    private Integer frequency;

    private String course;

    private String remark;

}
