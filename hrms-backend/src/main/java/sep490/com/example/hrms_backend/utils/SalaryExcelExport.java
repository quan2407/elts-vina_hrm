package sep490.com.example.hrms_backend.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sep490.com.example.hrms_backend.dto.SalaryBenefitDTO;
import sep490.com.example.hrms_backend.dto.SalaryDTO;
import sep490.com.example.hrms_backend.enums.BenefitType;
import sep490.com.example.hrms_backend.entity.Benefit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class SalaryExcelExport {

    public static ByteArrayInputStream exportSalariesToExcel(
            List<SalaryDTO> salaries,
            List<Benefit> allBenefits,
            int month,
            int year) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Báo cáo lương");

            List<Benefit> allowances = allBenefits.stream()
                    .filter(b -> b.getBenefitType() == BenefitType.PHU_CAP)
                    .toList();
            List<Benefit> deductions = allBenefits.stream()
                    .filter(b -> b.getBenefitType() == BenefitType.KHAU_TRU)
                    .toList();

            int phuCapSize = allowances.size();
            int khauTruSize = deductions.size();

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
            int totalColumns = 8 + phuCapSize + 2 + 2 + khauTruSize + 2;
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, totalColumns));

            // === Group Header (Row 1) ===
            Row groupHeader = sheet.createRow(1);
            int col = 0;
            groupHeader.createCell(col++).setCellValue("STT");
            groupHeader.createCell(col++).setCellValue("Mã NV");
            groupHeader.createCell(col++).setCellValue("Họ và Tên");
            groupHeader.createCell(col++).setCellValue("Chức vụ");
            groupHeader.createCell(col++).setCellValue("Mức lương cơ bản");

            int phuCapStart = col;
            for (int i = 0; i < phuCapSize; i++) {
                groupHeader.createCell(col++); // tạo các cell cho phụ cấp
            }
            sheet.addMergedRegion(new CellRangeAddress(1, 1, phuCapStart, col - 1));
            groupHeader.getCell(phuCapStart).setCellValue("Phụ cấp");

            groupHeader.createCell(col++).setCellValue("Số công");
            groupHeader.createCell(col++).setCellValue("Tiền lương");

            groupHeader.createCell(col++).setCellValue("Số giờ");
            groupHeader.createCell(col++).setCellValue("Tiền lương thêm giờ");

            int khauTruStart = col;
            for (int i = 0; i < khauTruSize; i++) {
                groupHeader.createCell(col++); // tạo các cell cho khấu trừ
            }
            sheet.addMergedRegion(new CellRangeAddress(1, 1, khauTruStart, col - 1));
            groupHeader.getCell(khauTruStart).setCellValue("Các khoản khấu trừ");

            groupHeader.createCell(col++).setCellValue("Tổng trừ");
            groupHeader.createCell(col++).setCellValue("Tổng thu nhập");

// ✅ Đảm bảo đã createCell cho tất cả các cột trước khi set style
            for (int i = 0; i < col; i++) {
                Cell cell = groupHeader.getCell(i);
                if (cell == null) cell = groupHeader.createCell(i); // nếu chưa có thì tạo
                cell.setCellStyle(headerStyle);
            }


            // === Sub Header (Row 2) ===
            Row header = sheet.createRow(2);
            col = 0;
            header.createCell(col++).setCellValue(""); // STT
            header.createCell(col++).setCellValue(""); // Mã NV
            header.createCell(col++).setCellValue(""); // Họ tên
            header.createCell(col++).setCellValue(""); // Chức vụ
            header.createCell(col++).setCellValue(""); // Lương cơ bản

            for (Benefit b : allowances) {
                header.createCell(col++).setCellValue(b.getTitle());
            }
            header.createCell(col++).setCellValue("Số công");
            header.createCell(col++).setCellValue("Tiền lương");
            header.createCell(col++).setCellValue("Số giờ");
            header.createCell(col++).setCellValue("Tiền lương thêm giờ");
            for (Benefit b : deductions) {
                header.createCell(col++).setCellValue(b.getTitle());
            }
            header.createCell(col++).setCellValue("Tổng trừ");
            header.createCell(col++).setCellValue("Tổng thu nhập");

            for (int i = 0; i < col; i++) {
                header.getCell(i).setCellStyle(headerStyle);
            }

            // === Data Rows ===
            int rowIndex = 3;
            int stt = 1;
            for (SalaryDTO dto : salaries) {
                Row row = sheet.createRow(rowIndex++);
                col = 0;

                row.createCell(col++).setCellValue(stt++);
                row.createCell(col++).setCellValue(dto.getEmployeeCode());
                row.createCell(col++).setCellValue(dto.getEmployeeName());
                row.createCell(col++).setCellValue(dto.getPositionName());
                row.createCell(col++).setCellValue(toNumber(dto.getBasicSalary()));

                List<SalaryBenefitDTO> appliedBenefits = dto.getAppliedBenefits() != null
                        ? dto.getAppliedBenefits()
                        : new ArrayList<>();

                Map<String, BigDecimal> benefitMap = appliedBenefits.stream()
                        .collect(Collectors.toMap(SalaryBenefitDTO::getTitle, SalaryBenefitDTO::getAmount));

                for (Benefit b : allowances) {
                    row.createCell(col++).setCellValue(toNumber(benefitMap.get(b.getTitle())));
                }

                row.createCell(col++).setCellValue(dto.getWorkingDays());
                row.createCell(col++).setCellValue(toNumber(dto.getProductionSalary()));
                row.createCell(col++).setCellValue(dto.getOvertimeHours());
                row.createCell(col++).setCellValue(toNumber(dto.getOvertimeSalary()));

                for (Benefit b : deductions) {
                    row.createCell(col++).setCellValue(toNumber(benefitMap.get(b.getTitle())));
                }

                row.createCell(col++).setCellValue(toNumber(dto.getTotalDeduction()));
                row.createCell(col++).setCellValue(toNumber(dto.getTotalIncome()));

                // set border style
                for (int i = 0; i < col; i++) {
                    row.getCell(i).setCellStyle(borderStyle);
                }
            }

            for (int i = 0; i <= totalColumns; i++) {
                sheet.autoSizeColumn(i);
            }

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