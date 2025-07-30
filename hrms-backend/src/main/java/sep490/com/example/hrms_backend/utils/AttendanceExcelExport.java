package sep490.com.example.hrms_backend.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sep490.com.example.hrms_backend.dto.AttendanceCellDTO;
import sep490.com.example.hrms_backend.dto.AttendanceMonthlyViewDTO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public class AttendanceExcelExport {

    public static ByteArrayInputStream exportAttendanceToExcel(
            List<AttendanceMonthlyViewDTO> attendanceData, int month, int year) {

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Báo cáo chấm công");
            int totalDays = YearMonth.of(year, month).lengthOfMonth();

            // === STYLE ===
            Font titleFont = workbook.createFont();
            titleFont.setFontHeightInPoints((short) 14);
            titleFont.setBold(true);

            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle borderStyle = workbook.createCellStyle();
            borderStyle.setBorderTop(BorderStyle.THIN);
            borderStyle.setBorderBottom(BorderStyle.THIN);
            borderStyle.setBorderLeft(BorderStyle.THIN);
            borderStyle.setBorderRight(BorderStyle.THIN);

            CellStyle alignTopStyle = workbook.createCellStyle();
            alignTopStyle.cloneStyleFrom(borderStyle);
            alignTopStyle.setVerticalAlignment(VerticalAlignment.TOP);
            alignTopStyle.setAlignment(HorizontalAlignment.LEFT);

            // === TIÊU ĐỀ CHÍNH ===
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6 + totalDays));
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("BÁO CÁO CHẤM CÔNG THÁNG " + month + "/" + year);
            titleCell.setCellStyle(titleStyle);

            // === HEADER ===
            Row headerRow = sheet.createRow(1);
            headerRow.createCell(0).setCellValue("STT");
            headerRow.createCell(1).setCellValue("Mã nhân viên");
            headerRow.createCell(2).setCellValue("Tên nhân viên");
            headerRow.createCell(3).setCellValue("Phòng ban");
            headerRow.createCell(4).setCellValue("Chức vụ");
            headerRow.createCell(5).setCellValue("Chuyền");
            headerRow.createCell(6).setCellValue("Phân loại");

            for (int day = 1; day <= totalDays; day++) {
                headerRow.createCell(6 + day).setCellValue("Ngày " + day);
            }

            // Cố định độ rộng cột thông tin nhân viên
            sheet.setColumnWidth(0, 2000);  // STT
            sheet.setColumnWidth(1, 4500);  // Mã nhân viên
            sheet.setColumnWidth(2, 6000);  // Tên
            sheet.setColumnWidth(3, 6000);  // Phòng ban
            sheet.setColumnWidth(4, 6000);  // Chức vụ
            sheet.setColumnWidth(5, 4000);  // Chuyền

            int rowIndex = 2;
            int stt = 1;

            if (attendanceData != null) {
                for (AttendanceMonthlyViewDTO employee : attendanceData) {
                    Map<String, AttendanceCellDTO> byDate = employee.getAttendanceByDate();

                    Row caNgayRow = sheet.createRow(rowIndex);
                    Row tangCaRow = sheet.createRow(rowIndex + 1);
                    Row cuoiTuanRow = sheet.createRow(rowIndex + 2);
                    Row ngayLeRow = sheet.createRow(rowIndex + 3);

                    // Gộp ô và ghi thông tin nhân viên
                    for (int col = 0; col <= 5; col++) {
                        sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex + 3, col, col));
                        Cell cell = caNgayRow.createCell(col);
                        switch (col) {
                            case 0 -> cell.setCellValue(stt++);
                            case 1 -> cell.setCellValue(employee.getEmployeeCode());
                            case 2 -> cell.setCellValue(employee.getEmployeeName());
                            case 3 -> cell.setCellValue(employee.getDepartmentName());
                            case 4 -> cell.setCellValue(employee.getPositionName());
                            case 5 -> cell.setCellValue(employee.getLineName());
                        }
                        cell.setCellStyle(alignTopStyle);
                    }

                    // Phân loại
                    caNgayRow.createCell(6).setCellValue("Ca ngày");
                    tangCaRow.createCell(6).setCellValue("Tăng ca");
                    cuoiTuanRow.createCell(6).setCellValue("Cuối tuần");
                    ngayLeRow.createCell(6).setCellValue("Ngày lễ");

                    caNgayRow.getCell(6).setCellStyle(borderStyle);
                    tangCaRow.getCell(6).setCellStyle(borderStyle);
                    cuoiTuanRow.getCell(6).setCellStyle(borderStyle);
                    ngayLeRow.getCell(6).setCellStyle(borderStyle);

                    for (int day = 1; day <= totalDays; day++) {
                        String dayKey = String.valueOf(day);
                        AttendanceCellDTO cell = byDate != null ? byDate.get(dayKey) : null;

                        String shift = (cell != null && cell.getShift() != null) ? cell.getShift() : "0";
                        String ot = (cell != null && cell.getOvertime() != null) ? cell.getOvertime() : "0";
                        String weekend = (cell != null && cell.getWeekend() != null) ? cell.getWeekend() : "0";
                        String holiday = (cell != null && cell.getHoliday() != null) ? cell.getHoliday() : "0";

                        caNgayRow.createCell(6 + day).setCellValue(shift);
                        tangCaRow.createCell(6 + day).setCellValue(ot);
                        cuoiTuanRow.createCell(6 + day).setCellValue(weekend);
                        ngayLeRow.createCell(6 + day).setCellValue(holiday);

                        caNgayRow.getCell(6 + day).setCellStyle(borderStyle);
                        tangCaRow.getCell(6 + day).setCellStyle(borderStyle);
                        cuoiTuanRow.getCell(6 + day).setCellStyle(borderStyle);
                        ngayLeRow.getCell(6 + day).setCellStyle(borderStyle);
                    }

                    // Viền quanh toàn bộ block 4 dòng
                    for (int r = rowIndex; r < rowIndex + 4; r++) {
                        Row row = sheet.getRow(r);
                        for (int c = 0; c <= 6 + totalDays; c++) {
                            Cell cell = row.getCell(c);
                            if (cell == null) cell = row.createCell(c);
                            cell.setCellStyle(borderStyle);
                        }
                    }

                    rowIndex += 4;
                }
            }

            // Tự động căn cột cho phần ngày
            for (int i = 6; i <= 6 + totalDays; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
