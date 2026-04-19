package com.kitten.chs.common.excel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HealthRecordExportData {

    private String username;

    private LocalDate recordDate;

    private LocalDateTime createTime;

    private String height;

    private String weight;

    private String heartRate;

    private String bloodPressureSystolic;

    private String bloodPressureDiastolic;

    private String symptoms;

    private String medicalHistory;

    private String diagnosis;

    private String remark;

    private List<MedicationReminderExcelVO> medicationList;
}
