package sep490.com.example.hrms_backend.service;

import org.springframework.web.multipart.MultipartFile;
import sep490.com.example.hrms_backend.dto.EmployeeOCRResponseDTO;

public interface OcrService {
    EmployeeOCRResponseDTO scanCCCDWithCloudVision(MultipartFile frontImage, MultipartFile backImage);
}
