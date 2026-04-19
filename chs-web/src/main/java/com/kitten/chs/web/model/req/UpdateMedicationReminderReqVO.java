package com.kitten.chs.web.model.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateMedicationReminderReqVO {

    private Long id;

    private String medicineName;

    private String dosage;

    private Integer frequency;

    private String course;

    private String remark;

}
