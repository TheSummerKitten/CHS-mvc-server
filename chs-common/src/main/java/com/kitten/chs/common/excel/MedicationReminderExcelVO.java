package com.kitten.chs.common.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@HeadRowHeight(20)
@ContentRowHeight(18)
@ColumnWidth(15)
public class MedicationReminderExcelVO {

    @ExcelProperty("药品名称")
    @ColumnWidth(20)
    private String medicineName;

    @ExcelProperty("剂量")
    @ColumnWidth(12)
    private String dosage;

    @ExcelProperty("频率")
    @ColumnWidth(15)
    private String frequency;

    @ExcelProperty("疗程")
    @ColumnWidth(12)
    private String course;

    @ExcelProperty("注意事项")
    @ColumnWidth(25)
    private String remark;
}
