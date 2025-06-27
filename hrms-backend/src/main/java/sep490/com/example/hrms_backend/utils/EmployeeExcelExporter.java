package sep490.com.example.hrms_backend.utils;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import sep490.com.example.hrms_backend.dto.EmployeeDetailDTO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EmployeeExcelExporter {

    public static ByteArrayInputStream export(List<EmployeeDetailDTO> employees) throws IOException {
        String[] columns = {
                "Mã NV", "Họ tên", "Giới tính", "Ngày sinh",
                "Nơi sinh", "Nguyên quán", "Quốc tịch", "CCCD",
                "Ngày cấp CCCD", "Ngày hết hạn CCCD",
                "Địa chỉ", "SĐT", "Email", "Ngày vào làm",
                "Phòng ban", "Vị trí", "Chuyền"
        };

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Danhsachnhanvien");

            try (InputStream logoStream = new ClassPathResource("elts_logo.png").getInputStream()) {
                byte[] imageBytes = IOUtils.toByteArray(logoStream);
                int pictureIdx = workbook.addPicture(imageBytes, Workbook.PICTURE_TYPE_PNG);

                Drawing<?> drawing = sheet.createDrawingPatriarch();
                ClientAnchor anchor = workbook.getCreationHelper().createClientAnchor();


                anchor.setCol1(0);
                anchor.setRow1(0);
                anchor.setCol2(5);
                anchor.setRow2(4);
                anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_DONT_RESIZE);

                Picture pict = drawing.createPicture(anchor, pictureIdx);

            }


            Row headerRow = sheet.createRow(5);
            for (int col = 0; col < columns.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(columns[col]);

                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);
                cell.setCellStyle(style);
            }

            DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            int rowIdx = 6;
            for (EmployeeDetailDTO emp : employees) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(emp.getEmployeeCode());
                row.createCell(1).setCellValue(emp.getEmployeeName());
                row.createCell(2).setCellValue(emp.getGender());
                row.createCell(3).setCellValue(emp.getDob() != null ? emp.getDob().format(df) : "");
                row.createCell(4).setCellValue(emp.getPlaceOfBirth());
                row.createCell(5).setCellValue(emp.getOriginPlace());
                row.createCell(6).setCellValue(emp.getNationality());
                row.createCell(7).setCellValue(emp.getCitizenId());
                row.createCell(8).setCellValue(emp.getCitizenIssueDate() != null ? emp.getCitizenIssueDate().format(df) : "");
                row.createCell(9).setCellValue(emp.getCitizenExpiryDate() != null ? emp.getCitizenExpiryDate().format(df) : "");
                row.createCell(10).setCellValue(emp.getAddress());
                row.createCell(11).setCellValue(emp.getPhoneNumber());
                row.createCell(12).setCellValue(emp.getEmail());
                row.createCell(13).setCellValue(emp.getStartWorkAt() != null ? emp.getStartWorkAt().format(df) : "");
                row.createCell(14).setCellValue(emp.getDepartmentName());
                row.createCell(15).setCellValue(emp.getPositionName());
                row.createCell(16).setCellValue(emp.getLineName());
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}
