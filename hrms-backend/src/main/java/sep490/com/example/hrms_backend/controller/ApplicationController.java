package sep490.com.example.hrms_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sep490.com.example.hrms_backend.dto.*;
import sep490.com.example.hrms_backend.enums.ApplicationStatus;
import sep490.com.example.hrms_backend.service.ApplicationService;
import sep490.com.example.hrms_backend.utils.CurrentUserUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;
    private final CurrentUserUtils currentUserUtils;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'PMC','PRODUCTION_MANAGER','EMPLOYEE')")
    public ResponseEntity<String> createApplication(
            @ModelAttribute ApplicationCreateDTO dto,
            @RequestParam(value = "attachment", required = false) MultipartFile attachment
    ) {
        Long employeeId = currentUserUtils.getCurrentEmployeeId();

        if (attachment != null && !attachment.isEmpty()) {
            try {
                String filePath = saveFile(attachment);
                dto.setAttachmentPath(filePath);
            } catch (IOException e) {
                return ResponseEntity.internalServerError().body("Lỗi khi lưu file");
            }
        }

        applicationService.createApplication(dto, employeeId);
        return ResponseEntity.ok("Application created successfully");
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN', 'PMC','PRODUCTION_MANAGER','EMPLOYEE')")
    public ResponseEntity<Page<ApplicationListItemDTO>> getMyApplications(
            @RequestParam(value = "status", required = false) ApplicationStatus status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Long employeeId = currentUserUtils.getCurrentEmployeeId();
        Page<ApplicationListItemDTO> apps = applicationService.getApplicationsForEmployee(
                employeeId,
                status,
                PageRequest.of(page, size)
        );
        return ResponseEntity.ok(apps);
    }

    public String saveFile(MultipartFile file) throws IOException {
        String uploadDir = "uploads/applications";
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        String originalFilename = file.getOriginalFilename();
        String filename = System.currentTimeMillis() + "_" + originalFilename;
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return uploadDir + "/" + filename;
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PMC','PRODUCTION_MANAGER','EMPLOYEE')")
    public ResponseEntity<ApplicationDetailDTO> getApplicationDetail(@PathVariable Long id) {
        ApplicationDetailDTO dto = applicationService.getApplicationDetail(id);
        return ResponseEntity.ok(dto);
    }
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'PMC','PRODUCTION_MANAGER','EMPLOYEE')")
    public ResponseEntity<String> updateApplication(
            @PathVariable Long id,
            @ModelAttribute ApplicationCreateDTO dto,
            @RequestParam(value = "attachment", required = false) MultipartFile attachment
    ) {
        Long employeeId = currentUserUtils.getCurrentEmployeeId();

        if (attachment != null && !attachment.isEmpty()) {
            try {
                String filePath = saveFile(attachment);
                dto.setAttachmentPath(filePath);
            } catch (IOException e) {
                return ResponseEntity.internalServerError().body("Lỗi khi lưu file");
            }
        }

        applicationService.updateApplication(id, dto, employeeId);
        return ResponseEntity.ok("Application updated successfully");
    }
    @GetMapping("/step-1")
    @PreAuthorize("hasRole('PRODUCTION_MANAGER')")
    public ResponseEntity<Page<ApplicationApprovalListItemDTO>> getStep1Applications(
            @RequestParam(value = "status", required = false) ApplicationStatus status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Page<ApplicationApprovalListItemDTO> apps = applicationService.getStep1Applications(
                status,
                PageRequest.of(page, size)
        );
        return ResponseEntity.ok(apps);
    }


    @PutMapping("/{id}/approve-step-1")
    @PreAuthorize("hasRole('PRODUCTION_MANAGER')")
    public ResponseEntity<String> approveStep1(
            @PathVariable Long id,
            @RequestBody ApplicationApprovalRequestDTO request
    ) {
        Long approverId = currentUserUtils.getCurrentEmployeeId();
        applicationService.approveStep1(id, approverId, request);
        return ResponseEntity.ok("Đã xử lý đơn ở bước 1");
    }

}
