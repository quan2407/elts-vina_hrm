package sep490.com.example.hrms_backend.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sep490.com.example.hrms_backend.dto.SalaryDTO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;

public class SalaryExcelExport {

    public static ByteArrayInputStream exportSalariesToExcel(List<SalaryDTO> salaries, int month, int year) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Báo cáo lương");

            // === Styles ===
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            CellStyle borderStyle = workbook.createCellStyle();
            borderStyle.setBorderTop(BorderStyle.THIN);
            borderStyle.setBorderBottom(BorderStyle.THIN);
            borderStyle.setBorderLeft(BorderStyle.THIN);
            borderStyle.setBorderRight(BorderStyle.THIN);

            // === Title ===
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("BÁO CÁO LƯƠNG THÁNG " + month + "/" + year);
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 18));

            // === Group Header (Row 1) ===
            Row groupHeader = sheet.createRow(1);
            groupHeader.createCell(0).setCellValue("STT");
            groupHeader.createCell(1).setCellValue("Mã NV");
            groupHeader.createCell(2).setCellValue("Họ và Tên");
            groupHeader.createCell(3).setCellValue("Chức vụ");
            groupHeader.createCell(4).setCellValue("Mức lương cơ bản");

            groupHeader.createCell(5).setCellValue("Phụ cấp");
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 5, 8));

            groupHeader.createCell(9).setCellValue("Lương sản xuất");
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 9, 10));

            groupHeader.createCell(11).setCellValue("Lương thêm giờ");
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 11, 12));

            groupHeader.createCell(13).setCellValue("Các khoản khấu trừ");
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 13, 17));

            groupHeader.createCell(18).setCellValue("Tổng thu nhập");
            sheet.addMergedRegion(new CellRangeAddress(1, 2, 18, 18));

            for (int i = 0; i <= 18; i++) {
                Cell cell = groupHeader.getCell(i);
                if (cell == null) {
                    cell = groupHeader.createCell(i);
                }
                cell.setCellStyle(headerStyle);
            }

            // === Sub Header (Row 2) ===
            Row header = sheet.createRow(2);
            String[] headers = {
                    "", "", "", "", "", // 0-4
                    "Điện thoại", "Nhà ở", "Chuyên Cần", "Đi lại", // 5-8
                    "Số công", "Tiền lương", // 9-10
                    "Số giờ", "Tiền lương thêm giờ", // 11-12
                    "BHXH 8%", "BHYT 1.5%", "BHTN 1%", "Đoàn phí", "Tổng trừ", // 13-17
                    "" // 18
            };
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // === Data Rows ===
            int rowIndex = 3;
            int stt = 1;
            for (SalaryDTO dto : salaries) {
                Row row = sheet.createRow(rowIndex++);
                int col = 0;

                row.createCell(col++).setCellValue(stt++);
                row.createCell(col++).setCellValue(dto.getEmployeeCode());
                row.createCell(col++).setCellValue(dto.getEmployeeName());
                row.createCell(col++).setCellValue(dto.getPositionName());
                row.createCell(col++).setCellValue(toNumber(dto.getBasicSalary()));
                row.createCell(col++).setCellValue(toNumber(dto.getAllowancePhone()));
                row.createCell(col++).setCellValue(toNumber(dto.getAllowanceMeal()));
                row.createCell(col++).setCellValue(toNumber(dto.getAllowanceAttendance()));
                row.createCell(col++).setCellValue(toNumber(dto.getAllowanceTransport()));
                row.createCell(col++).setCellValue(dto.getWorkingDays());
                row.createCell(col++).setCellValue(toNumber(dto.getProductionSalary()));
                row.createCell(col++).setCellValue(dto.getOvertimeHours());
                row.createCell(col++).setCellValue(toNumber(dto.getOvertimeSalary()));
                row.createCell(col++).setCellValue(toNumber(dto.getSocialInsurance()));
                row.createCell(col++).setCellValue(toNumber(dto.getHealthInsurance()));
                row.createCell(col++).setCellValue(toNumber(dto.getUnemploymentInsurance()));
                row.createCell(col++).setCellValue(toNumber(dto.getUnionFee()));
                row.createCell(col++).setCellValue(toNumber(dto.getTotalDeduction()));
                row.createCell(col++).setCellValue(toNumber(dto.getTotalIncome()));

                for (int i = 0; i < col; i++) {
                    row.getCell(i).setCellStyle(borderStyle);
                }
            }

            // Auto-size all columns
            for (int i = 0; i <= 18; i++) {
                sheet.autoSizeColumn(i);
            }
            sheet.setColumnWidth(18, 20 * 256);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static double toNumber(BigDecimal value) {
        return value != null ? value.doubleValue() : 0.0;
    }
}
