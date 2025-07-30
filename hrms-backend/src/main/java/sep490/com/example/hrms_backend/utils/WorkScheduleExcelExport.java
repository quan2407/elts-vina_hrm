package sep490.com.example.hrms_backend.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sep490.com.example.hrms_backend.dto.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class WorkScheduleExcelExport {

    public static ByteArrayInputStream exportToExcel(List<DepartmentWorkScheduleViewDTO> data, int month, int year) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Lịch sản xuất");

            // === STYLE ===
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);

            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setFont(boldFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(boldFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setWrapText(true);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);

            CellStyle weekendStyle = workbook.createCellStyle();
            weekendStyle.cloneStyleFrom(cellStyle);
            weekendStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            weekendStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // === TIÊU ĐỀ TRÊN CÙNG ===
            Row titleRow = sheet.createRow(0);
            titleRow.setHeightInPoints(30);
            String title = String.format("KẾ HOẠCH THỜI GIAN SẢN XUẤT THÁNG %02d NĂM %d", month, year);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(title);
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2 + data.get(0).getLines().get(0).getWorkDetails().size()));

            // === HEADER ===
            Row headerRow = sheet.createRow(1);
            headerRow.createCell(0).setCellValue("Xưởng");
            headerRow.createCell(1).setCellValue("Line SX");

            List<WorkScheduleDayDetailDTO> days = data.get(0).getLines().get(0).getWorkDetails();
            DateTimeFormatter dayFmt = DateTimeFormatter.ofPattern("dd/MM");
            for (int i = 0; i < days.size(); i++) {
                WorkScheduleDayDetailDTO day = days.get(i);
                String value = day.getDate().format(dayFmt) + "\n" + day.getWeekday();
                Cell cell = headerRow.createCell(2 + i);
                cell.setCellValue(value);
                cell.setCellStyle(headerStyle);
            }

            headerRow.getCell(0).setCellStyle(headerStyle);
            headerRow.getCell(1).setCellStyle(headerStyle);

            // === GHI DỮ LIỆU ===
            int rowIndex = 2;
            for (DepartmentWorkScheduleViewDTO dept : data) {
                int deptStartRow = rowIndex;
                for (LineWorkScheduleViewDTO line : dept.getLines()) {
                    Row row = sheet.createRow(rowIndex++);
                    Cell deptCell = row.createCell(0);
                    deptCell.setCellValue(dept.getDepartmentName());

                    Cell lineCell = row.createCell(1);
                    lineCell.setCellValue(line.getLineName() != null ? line.getLineName() : "-");

                    deptCell.setCellStyle(cellStyle);
                    lineCell.setCellStyle(cellStyle);

                    for (int i = 0; i < line.getWorkDetails().size(); i++) {
                        WorkScheduleDayDetailDTO detail = line.getWorkDetails().get(i);
                        Cell cell = row.createCell(2 + i);

                        if (detail.getStartTime() != null && detail.getEndTime() != null) {
                            String time = detail.getStartTime() + "–" + detail.getEndTime();
                            cell.setCellValue(time);
                        } else {
                            cell.setCellValue("");
                        }

                        boolean isWeekend = "CN".equals(detail.getWeekday());
                        cell.setCellStyle(isWeekend ? weekendStyle : cellStyle);
                    }
                }
                // Gộp ô cột "Xưởng"
                if (rowIndex - deptStartRow > 1) {
                    sheet.addMergedRegion(new CellRangeAddress(deptStartRow, rowIndex - 1, 0, 0));
                }
            }

            // === ĐỘ RỘNG CỘT ===
            sheet.setColumnWidth(0, 5000); // Xưởng
            sheet.setColumnWidth(1, 4000); // Line
            for (int i = 0; i < days.size(); i++) {
                sheet.setColumnWidth(2 + i, 4000);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
