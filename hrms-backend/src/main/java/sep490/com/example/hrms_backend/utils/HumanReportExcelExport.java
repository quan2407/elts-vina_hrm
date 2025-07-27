package sep490.com.example.hrms_backend.utils;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.core.io.ClassPathResource;
import sep490.com.example.hrms_backend.dto.AttendanceMonthlyViewDTO;
import sep490.com.example.hrms_backend.entity.Department;
import sep490.com.example.hrms_backend.entity.Line;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class HumanReportExcelExport {

    public static ByteArrayInputStream exportToExcel(
            LocalDate date,
            List<Department> departments,
            Department sanXuat,
            List<Line> lines,
            Map<String, List<AttendanceMonthlyViewDTO>> fullEmp,
            Map<String, List<AttendanceMonthlyViewDTO>> absentEmp,
            Map<String, List<AttendanceMonthlyViewDTO>> absentEmpKL) {

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Báo cáo nhân lực");

            // Logo
            try (InputStream logoStream = new ClassPathResource("elts_logo.png").getInputStream()) {
                byte[] imageBytes = IOUtils.toByteArray(logoStream);
                int pictureIdx = workbook.addPicture(imageBytes, Workbook.PICTURE_TYPE_PNG);
                Drawing<?> drawing = sheet.createDrawingPatriarch();
                ClientAnchor anchor = workbook.getCreationHelper().createClientAnchor();
                anchor.setCol1(0);
                anchor.setRow1(0);
                anchor.setCol2(5);
                anchor.setRow2(4);
                drawing.createPicture(anchor, pictureIdx);
            }

            // Style
            CellStyle borderStyle = workbook.createCellStyle();
            borderStyle.setBorderTop(BorderStyle.THIN);
            borderStyle.setBorderBottom(BorderStyle.THIN);
            borderStyle.setBorderLeft(BorderStyle.THIN);
            borderStyle.setBorderRight(BorderStyle.THIN);
            borderStyle.setAlignment(HorizontalAlignment.CENTER);
            borderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            borderStyle.setWrapText(true);

            int rowIdx = 5;
            Row headerRow1 = sheet.createRow(rowIdx++);
            Row headerRow2 = sheet.createRow(rowIdx++);

            sheet.addMergedRegion(new CellRangeAddress(5, 6, 0, 0));
            Cell departmentHeader = headerRow1.createCell(0);
            departmentHeader.setCellValue("Phòng ban");
            departmentHeader.setCellStyle(borderStyle);
            Cell deptEmpty = headerRow2.createCell(0);
            deptEmpty.setCellValue("");
            deptEmpty.setCellStyle(borderStyle);

            int colIdx = 1;
            for (Department dep : departments) {
                if (dep.equals(sanXuat)) {
                    int start = colIdx;
                    int end = colIdx + lines.size() - 1;
                    sheet.addMergedRegion(new CellRangeAddress(5, 5, start, end));

                    // Set border for all cells in merged region
                    for (int i = start; i <= end; i++) {
                        Cell mergedCell = headerRow1.createCell(i);
                        mergedCell.setCellStyle(borderStyle);
                    }
                    headerRow1.getCell(start).setCellValue("Sản Xuất");

                    for (Line l : lines) {
                        Cell lineCell = headerRow2.createCell(colIdx++);
                        lineCell.setCellValue(l.getLineName());
                        lineCell.setCellStyle(borderStyle);
                    }
                } else {
                    sheet.addMergedRegion(new CellRangeAddress(5, 6, colIdx, colIdx));
                    Cell cell = headerRow1.createCell(colIdx);
                    cell.setCellValue(dep.getDepartmentName());
                    cell.setCellStyle(borderStyle);

                    Cell cell2 = headerRow2.createCell(colIdx);
                    cell2.setCellValue("");
                    cell2.setCellStyle(borderStyle);
                    colIdx++;
                }
            }

            // Ghi chú
            sheet.addMergedRegion(new CellRangeAddress(5, 6, colIdx, colIdx));
            Cell noteCell1 = headerRow1.createCell(colIdx);
            noteCell1.setCellValue("Chú thích");
            noteCell1.setCellStyle(borderStyle);
            Cell noteCell2 = headerRow2.createCell(colIdx);
            noteCell2.setCellValue("");
            noteCell2.setCellStyle(borderStyle);

            // Body
            String[] labels = {"Tỷ lệ công nhân viên đi làm", "Nhân lực hiện có", "Số lượng đi làm", "Nghỉ phép", "Nghỉ không lương"};
            String[] keys = {"workRate", "currentStaff", "presentToday", "onLeave", "noSalary"};

            for (int i = 0; i < labels.length; i++) {
                Row row = sheet.createRow(rowIdx++);
                Cell labelCell = row.createCell(0);
                labelCell.setCellValue(labels[i]);
                labelCell.setCellStyle(borderStyle);

                colIdx = 1;
                for (Department dep : departments) {
                    if (dep.equals(sanXuat)) {
                        for (Line l : lines) {
                            String key = l.getLineName();
                            List<AttendanceMonthlyViewDTO> all = fullEmp.getOrDefault(key, List.of());
                            List<AttendanceMonthlyViewDTO> off = absentEmp.getOrDefault(key, List.of());
                            List<AttendanceMonthlyViewDTO> offKL = absentEmpKL.getOrDefault(key, List.of());
                            int total = all.size();
                            int present = total - off.size() - offKL.size();

                            Cell cell = row.createCell(colIdx++);
                            cell.setCellStyle(borderStyle);
                            switch (keys[i]) {
                                case "workRate" -> cell.setCellValue(total > 0 ? Math.round(((double) present / total) * 100) + "%" : "0%");
                                case "currentStaff" -> cell.setCellValue(total);
                                case "presentToday" -> cell.setCellValue(present);
                                case "onLeave" -> cell.setCellValue(off.size());
                                case "noSalary" -> cell.setCellValue(offKL.size());
                            }
                        }
                    } else {
                        String key = dep.getDepartmentName();
                        List<AttendanceMonthlyViewDTO> all = fullEmp.getOrDefault(key, List.of());
                        List<AttendanceMonthlyViewDTO> off = absentEmp.getOrDefault(key, List.of());
                        List<AttendanceMonthlyViewDTO> offKL = absentEmpKL.getOrDefault(key, List.of());
                        int total = all.size();
                        int present = total - off.size() - offKL.size();

                        Cell cell = row.createCell(colIdx++);
                        cell.setCellStyle(borderStyle);
                        switch (keys[i]) {
                            case "workRate" -> cell.setCellValue(total > 0 ? Math.round(((double) present / total) * 100) + "%" : "0%");
                            case "currentStaff" -> cell.setCellValue(total);
                            case "presentToday" -> cell.setCellValue(present);
                            case "onLeave" -> cell.setCellValue(off.size());
                            case "noSalary" -> cell.setCellValue(offKL.size());
                        }
                    }
                }

                // Comment
                Cell commentCell = row.createCell(colIdx);
                commentCell.setCellStyle(borderStyle);
                if ("onLeave".equals(keys[i])) {
                    String comment = absentEmp.values().stream()
                            .flatMap(List::stream)
                            .map(AttendanceMonthlyViewDTO::getEmployeeName)
                            .distinct()
                            .reduce((a, b) -> a + ", " + b).orElse("");
                    commentCell.setCellValue(comment);
                } else if ("noSalary".equals(keys[i])) {
                    String comment = absentEmpKL.values().stream()
                            .flatMap(List::stream)
                            .map(AttendanceMonthlyViewDTO::getEmployeeName)
                            .distinct()
                            .reduce((a, b) -> a + ", " + b).orElse("");
                    commentCell.setCellValue(comment);
                }
            }

            for (int i = 0; i <= colIdx; i++) {
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
}
