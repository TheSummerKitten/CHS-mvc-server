package com.kitten.chs.common.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HealthRecordExcelUtil {

    private static final String THEME_COLOR = "D31145";
    private static final String HEADER_BG_COLOR = "FFF5F5";

    public static void exportHealthRecord(HealthRecordExportData data, HttpServletResponse response) throws IOException {
        String fileName = URLEncoder.encode("健康记录详情_" + data.getRecordDate(), "UTF-8").replaceAll("\\+", "%20");
        
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        
        ExcelWriter excelWriter = EasyExcel.write(byteArrayOutputStream)
                .inMemory(true)
                .build();
        WriteSheet writeSheet = EasyExcel.writerSheet("健康记录详情").build();

        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        HorizontalCellStyleStrategy horizontalCellStyleStrategy = 
                new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
        writeSheet.setCustomWriteHandlerList(Collections.singletonList(horizontalCellStyleStrategy));

        List<List<String>> head = new ArrayList<>();
        List<List<Object>> dataList = new ArrayList<>();

        addTitleRow(dataList);
        addEmptyRow(dataList);
        addBasicInfoRows(data, dataList);
        addEmptyRow(dataList);
        addSectionTitleRow(dataList, "【体征数据】");
        addVitalSignsRows(data, dataList);
        addEmptyRow(dataList);
        addSectionTitleRow(dataList, "【诊断信息】");
        addDiagnosisRows(data, dataList);
        addEmptyRow(dataList);

        if (data.getMedicationList() != null && !data.getMedicationList().isEmpty()) {
            addSectionTitleRow(dataList, "【用药提醒】");
            addMedicationHeaderRow(dataList);
            addMedicationDataRows(data.getMedicationList(), dataList);
        }

        for (int i = 0; i < 6; i++) {
            head.add(Collections.singletonList(""));
        }

        excelWriter.write(dataList, writeSheet);

        Workbook workbook = excelWriter.writeContext().writeWorkbookHolder().getWorkbook();
        Sheet sheet = workbook.getSheetAt(0);

        sheet.setColumnWidth(0, 18 * 256);
        sheet.setColumnWidth(1, 18 * 256);
        sheet.setColumnWidth(2, 18 * 256);
        sheet.setColumnWidth(3, 18 * 256);
        sheet.setColumnWidth(4, 18 * 256);

        int medicationCount = data.getMedicationList() != null ? data.getMedicationList().size() : 0;
        applyStyles(workbook, sheet, medicationCount);

        excelWriter.finish();

        byte[] bytes = byteArrayOutputStream.toByteArray();
        int fileSize = bytes.length;

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        response.setContentLength(fileSize);

        response.getOutputStream().write(bytes);
        response.getOutputStream().flush();
    }

    private static void applyStyles(Workbook workbook, Sheet sheet, int medicationCount) {
        XSSFWorkbook xssfWorkbook = (XSSFWorkbook) workbook;

        styleTitleRow(xssfWorkbook, sheet, 0);
        styleBasicInfoRows(workbook, sheet, 2, 3);
        styleSectionTitleRow(xssfWorkbook, sheet, 5, THEME_COLOR);
        styleVitalSignsRows(workbook, sheet, 6, 7);
        styleSectionTitleRow(xssfWorkbook, sheet, 9, THEME_COLOR);
        styleDiagnosisRows(workbook, sheet, 10, 13);

        if (medicationCount > 0) {
            int medicationHeaderRowIndex = 15;
//            styleSectionTitleRow(xssfWorkbook, sheet, 14, THEME_COLOR);
            styleMedicationHeaderRow(xssfWorkbook, sheet, medicationHeaderRowIndex);
            styleMedicationDataRows(workbook, sheet, medicationHeaderRowIndex + 1, medicationCount);
        }
    }

    private static void addTitleRow(List<List<Object>> dataList) {
        List<Object> row = new ArrayList<>();
        row.add("健康记录详情");
        dataList.add(row);
    }

    private static void addEmptyRow(List<List<Object>> dataList) {
        List<Object> row = new ArrayList<>();
        row.add("");
        dataList.add(row);
    }

    private static void addBasicInfoRows(HealthRecordExportData data, List<List<Object>> dataList) {
        List<Object> row1 = new ArrayList<>();
        row1.add("用户名称：" + (data.getUsername() != null ? data.getUsername() : "-"));
        row1.add("");
        row1.add("");
        row1.add("记录日期：" + (data.getRecordDate() != null ? data.getRecordDate().toString() : "-"));
        row1.add("");
        dataList.add(row1);

        List<Object> row2 = new ArrayList<>();
        row2.add("创建时间：" + (data.getCreateTime() != null ? data.getCreateTime().toString() : "-"));
        row2.add("");
        row2.add("");
        row2.add("");
        row2.add("");
        dataList.add(row2);
    }

    private static void addSectionTitleRow(List<List<Object>> dataList, String title) {
        List<Object> row = new ArrayList<>();
        row.add(title);
        dataList.add(row);
    }

    private static void addVitalSignsRows(HealthRecordExportData data, List<List<Object>> dataList) {
        List<Object> row1 = new ArrayList<>();
        row1.add("身高：" + (data.getHeight() != null ? data.getHeight() + " cm" : "-"));
        row1.add("");
        row1.add("体重：" + (data.getWeight() != null ? data.getWeight() + " kg" : "-"));
        row1.add("");
        row1.add("心率：" + (data.getHeartRate() != null ? data.getHeartRate() + " 次/分" : "-"));
        dataList.add(row1);

        List<Object> row2 = new ArrayList<>();
        row2.add("收缩压：" + (data.getBloodPressureSystolic() != null ? data.getBloodPressureSystolic() + " mmHg" : "-"));
        row2.add("");
        row2.add("舒张压：" + (data.getBloodPressureDiastolic() != null ? data.getBloodPressureDiastolic() + " mmHg" : "-"));
        row2.add("");
        row2.add("");
        dataList.add(row2);
    }

    private static void addDiagnosisRows(HealthRecordExportData data, List<List<Object>> dataList) {
        List<Object> row1 = new ArrayList<>();
        row1.add("主要症状：" + (data.getSymptoms() != null ? data.getSymptoms() : "-"));
        dataList.add(row1);

        List<Object> row2 = new ArrayList<>();
        row2.add("既往病史：" + (data.getMedicalHistory() != null ? data.getMedicalHistory() : "-"));
        dataList.add(row2);

        List<Object> row3 = new ArrayList<>();
        row3.add("诊断结果：" + (data.getDiagnosis() != null ? data.getDiagnosis() : "-"));
        dataList.add(row3);

        List<Object> row4 = new ArrayList<>();
        row4.add("备注：" + (data.getRemark() != null ? data.getRemark() : "-"));
        dataList.add(row4);
    }

    private static void addMedicationHeaderRow(List<List<Object>> dataList) {
        List<Object> row = new ArrayList<>();
        row.add("药品名称");
        row.add("剂量");
        row.add("频率");
        row.add("疗程");
        row.add("注意事项");
        dataList.add(row);
    }

    private static void addMedicationDataRows(List<MedicationReminderExcelVO> medicationList, List<List<Object>> dataList) {
        for (MedicationReminderExcelVO medication : medicationList) {
            List<Object> row = new ArrayList<>();
            row.add(medication.getMedicineName() != null ? medication.getMedicineName() : "-");
            row.add(medication.getDosage() != null ? medication.getDosage() : "-");
            row.add(medication.getFrequency() != null ? medication.getFrequency() : "-");
            row.add(medication.getCourse() != null ? medication.getCourse() : "-");
            row.add(medication.getRemark() != null ? medication.getRemark() : "-");
            dataList.add(row);
        }
    }

    private static XSSFCellStyle createCellStyleWithColor(XSSFWorkbook workbook, byte[] rgb) {
        XSSFCellStyle style = workbook.createCellStyle();
        XSSFColor color = new XSSFColor(rgb, null);
        style.setFillForegroundColor(color);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private static void styleTitleRow(XSSFWorkbook workbook, Sheet sheet, int rowIndex) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) return;

        XSSFCellStyle style = createCellStyleWithColor(workbook, hexToRgb(THEME_COLOR));
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 18);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);

        row.setHeightInPoints(35);
        Cell cell = row.getCell(0);
        if (cell != null) {
            cell.setCellStyle(style);
        }
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(rowIndex, rowIndex, 0, 4));
    }

    private static void styleBasicInfoRows(Workbook workbook, Sheet sheet, int startRow, int endRow) {
        for (int i = startRow; i <= endRow; i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            CellStyle style = workbook.createCellStyle();
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            Font font = workbook.createFont();
            font.setFontHeightInPoints((short) 12);
            style.setFont(font);

            for (int j = 0; j < 5; j++) {
                Cell cell = row.getCell(j);
                if (cell != null) {
                    cell.setCellStyle(style);
                }
            }
        }
    }

    private static void styleSectionTitleRow(XSSFWorkbook workbook, Sheet sheet, int rowIndex, String colorHex) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) return;

        XSSFCellStyle style = createCellStyleWithColor(workbook, hexToRgb(colorHex));
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);

        row.setHeightInPoints(25);
        Cell cell = row.getCell(0);
        if (cell != null) {
            cell.setCellStyle(style);
        }
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(rowIndex, rowIndex, 0, 4));
    }

    private static void styleVitalSignsRows(Workbook workbook, Sheet sheet, int startRow, int endRow) {
        for (int i = startRow; i <= endRow; i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            CellStyle style = workbook.createCellStyle();
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setFillForegroundColor(IndexedColors.WHITE.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font font = workbook.createFont();
            font.setFontHeightInPoints((short) 11);
            font.setColor(IndexedColors.BLACK.getIndex());
            style.setFont(font);

            for (int j = 0; j < 5; j++) {
                Cell cell = row.getCell(j);
                if (cell != null) {
                    cell.setCellStyle(style);
                }
            }
        }
    }

    private static void styleDiagnosisRows(Workbook workbook, Sheet sheet, int startRow, int endRow) {
        for (int i = startRow; i <= endRow; i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            CellStyle style = workbook.createCellStyle();
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            style.setWrapText(true);
            Font font = workbook.createFont();
            font.setFontHeightInPoints((short) 11);
            style.setFont(font);

            Cell cell = row.getCell(0);
            if (cell != null) {
                cell.setCellStyle(style);
            }
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(i, i, 0, 4));
        }
    }

    private static void styleMedicationHeaderRow(XSSFWorkbook workbook, Sheet sheet, int rowIndex) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) return;

        XSSFCellStyle style = createCellStyleWithColor(workbook, hexToRgb(HEADER_BG_COLOR));
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);

        for (int j = 0; j < 5; j++) {
            Cell cell = row.getCell(j);
            if (cell != null) {
                cell.setCellStyle(style);
            }
        }
    }

    private static void styleMedicationDataRows(Workbook workbook, Sheet sheet, int startRow, int count) {
        for (int i = 0; i < count; i++) {
            Row row = sheet.getRow(startRow + i);
            if (row == null) continue;

            CellStyle style = workbook.createCellStyle();
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            Font font = workbook.createFont();
            font.setFontHeightInPoints((short) 11);
            style.setFont(font);

            for (int j = 0; j < 5; j++) {
                Cell cell = row.getCell(j);
                if (cell != null) {
                    cell.setCellStyle(style);
                }
            }
        }
    }

    private static byte[] hexToRgb(String hex) {
        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);
        return new byte[]{(byte) r, (byte) g, (byte) b};
    }
}
