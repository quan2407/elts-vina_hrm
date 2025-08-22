package sep490.com.example.hrms_backend.service.impl;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sep490.com.example.hrms_backend.dto.EmployeeOCRResponseDTO;
import sep490.com.example.hrms_backend.service.OcrService;

import java.io.IOException;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class OcrServiceImpl implements OcrService {

    @Override
    public EmployeeOCRResponseDTO scanCCCDWithCloudVision(MultipartFile frontImage, MultipartFile backImage) {
        EmployeeOCRResponseDTO dto = new EmployeeOCRResponseDTO();
        String frontImagePath = null;
        if (frontImage != null && !frontImage.isEmpty()) {
            try {
                AnnotateImageResponse res = annotateImage(frontImage);
                String frontText = res.getTextAnnotationsCount() > 0 ? res.getTextAnnotations(0).getDescription() : "";
                log.info("Front OCR: {}", frontText);

                if (!isValidFrontCCCD(frontText)) {
                    throw new IllegalArgumentException("Ảnh không hợp lệ.");
                }


                dto.setCitizenId(extract(frontText, "(?i)Sol\\s*No\\.?[:\\s]*(\\d{9,12})"));
                dto.setEmployeeName(extractBelowLine(frontText, "(?i)Họ và tên"));
                dto.setDob(parseDate(extract(frontText, "(?i)ngày sinh[^\\d]*(\\d{2}/\\d{2}/\\d{4})")));
                dto.setGender(extract(frontText, "(?i)Giới tính\\s*/?\\s*Sex\\s*(Nam|Nữ)"));
                dto.setOriginPlace(extractBelowLine(frontText, "(?i)Quê quán"));

                dto.setAddress(extractAddress(frontText));

                dto.setNationality(extract(frontText, "(?i)Quốc tịch[^:]*[:\\s]*([A-Za-zÀ-Ỵà-ỵ\\s]+)(?=\\s*\\n|\\s*$)"));

                dto.setCitizenExpiryDate(parseDate(extract(frontText, "(?i)có giá trị đến[^\\d]*(\\d{2}/\\d{2}/\\d{4})")));

                if (!res.getFaceAnnotationsList().isEmpty()) {
                    BoundingPoly boundingBox = res.getFaceAnnotations(0).getFdBoundingPoly();
                    dto.setFaceBoundingBox(boundingBox.toString());
                }

            } catch (IOException e) {
                log.error("Failed to process front image", e);
            }
        }
        String backImagePath = null;
        if (backImage != null && !backImage.isEmpty()) {
            try {
                String backText = extractTextOnly(backImage);
                log.info("Back OCR: {}", backText);
                if (!isValidBackCCCD(backText)) {
                    throw new IllegalArgumentException("Ảnh không hợp lệ.");
                }
                dto.setCitizenIssueDate(parseDate(extract(backText, "(?i)ngày, tháng, năm[^\\d]*(\\d{2}/\\d{2}/\\d{4})")));
                String frontName = dto.getEmployeeName();
                String backNameRaw = extractFullNameFromMRZ(backText);
                String frontNorm = normalizeName(frontName);
                System.out.println("Ten mat truoc " + frontNorm);
                String backNorm = normalizeName(backNameRaw);
                System.out.println("Ten mat sau " + backNorm);
                System.out.println("equal or not: " + frontNorm.equals(backNorm) );
                if (backNameRaw != null && frontNorm != null && backNorm != null
                        && !frontNorm.equals(backNorm)
                        && !backNorm.equals(frontNorm)) {
                    throw new IllegalArgumentException("Ảnh mặt trước và mặt sau không thuộc cùng một người.");
                }
            } catch (IOException e) {
                log.error("Failed to process back image", e);
            }
        }

        dto.setFrontImagePath(frontImagePath);
        dto.setBackImagePath(backImagePath);

        return dto;
    }


    private AnnotateImageResponse annotateImage(MultipartFile file) throws IOException {
        ByteString imgBytes = ByteString.readFrom(file.getInputStream());

        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature textFeature = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
        Feature faceFeature = Feature.newBuilder().setType(Feature.Type.FACE_DETECTION).build();

        AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                .addFeatures(textFeature)
                .addFeatures(faceFeature)
                .setImage(img)
                .build();

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(Collections.singletonList(request));
            AnnotateImageResponse res = response.getResponses(0);

            if (res.hasError()) {
                log.error("Cloud Vision API error: {}", res.getError().getMessage());
                throw new RuntimeException("Cloud Vision API error");
            }

            return res;
        }
    }

    private String extractTextOnly(MultipartFile file) throws IOException {
        return annotateImage(file).getTextAnnotationsCount() > 0
                ? annotateImage(file).getTextAnnotations(0).getDescription()
                : "";
    }

    private String extract(String text, String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group(1).trim() : null;
    }

    private String extractBelowLine(String text, String keywordRegex) {
        String[] lines = text.split("\\r?\\n");
        for (int i = 0; i < lines.length - 1; i++) {
            if (lines[i].toLowerCase().matches(".*" + keywordRegex.toLowerCase() + ".*")) {
                return lines[i + 1].trim();
            }
        }
        return null;
    }

    private String extractAddress(String text) {
        String[] lines = text.split("\\r?\\n");
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].toLowerCase().contains("nơi thường trú") || lines[i].toLowerCase().contains("place of residence")) {
                StringBuilder result = new StringBuilder();

                String[] parts = lines[i].split("(?i)nơi thường trú I place of residence");
                if (parts.length > 1 && !parts[1].trim().isEmpty()) {
                    result.append(parts[1].trim());
                } else {
                    result.append(lines[i].replaceAll("(?i)nơi thường trú I place of residence", "").trim());
                }

                if (i + 1 < lines.length && !lines[i + 1].trim().isEmpty()) {
                    if (result.length() > 0) result.append(", ");
                    result.append(lines[i + 1].trim());
                }

                return result.toString();
            }
        }
        return null;
    }

    private java.time.LocalDate parseDate(String dateStr) {
        if (dateStr == null) return null;
        try {
            String[] parts = dateStr.split("/");
            return java.time.LocalDate.of(
                    Integer.parseInt(parts[2]),
                    Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[0])
            );
        } catch (Exception e) {
            return null;
        }
    }
    private boolean isValidFrontCCCD(String text) {
        return text.contains("CĂN CƯỚC CÔNG DÂN")
                && text.toLowerCase().contains("họ và tên")
                && text.toLowerCase().contains("ngày sinh")
                && text.toLowerCase().contains("giới tính")
                && text.toLowerCase().contains("quốc tịch");
    }

    private boolean isValidBackCCCD(String text) {
        return text.toLowerCase().contains("đặc điểm nhận dạng")
                || text.toLowerCase().contains("ngày, tháng, năm")
                || text.matches(".*<<.*<<.*");
    }
    private String normalizeName(String name) {
        if (name == null) return null;
        String noDiacritics = java.text.Normalizer.normalize(name, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return noDiacritics.toUpperCase().replaceAll("[^A-Z]", "");
    }

    private String extractFullNameFromMRZ(String text) {
        // Chỉ lấy dòng có '<<' và có chữ cái, loại bỏ dòng chứa VNM hoặc số
        for (String line : text.split("\\r?\\n")) {
            if (line.contains("<<") && line.matches("^[A-Z<]+$") && !line.contains("VNM")) {
                String[] parts = line.split("<<");
                if (parts.length >= 2) {
                    String lastName = parts[0].replace("<", " ").trim();
                    String givenName = parts[1].replace("<", " ").trim();
                    return (lastName + " " + givenName).replaceAll("\\s+", " ").trim();
                }
            }
        }
        return null;
    }



}
