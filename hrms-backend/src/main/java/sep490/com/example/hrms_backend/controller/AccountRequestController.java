package sep490.com.example.hrms_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sep490.com.example.hrms_backend.dto.AccountRequestDTO;
import sep490.com.example.hrms_backend.service.AccountRequestService;

@RestController
@RequestMapping("/api/account-requests")
@RequiredArgsConstructor
public class AccountRequestController {

    private final AccountRequestService accountRequestService;

    @GetMapping
    public ResponseEntity<Page<AccountRequestDTO>> getRequestsByStatus(
            @RequestParam(defaultValue = "pending") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(accountRequestService.getRequestsByStatus(status, page, size));
    }


    @PostMapping("/{id}/approve")
    public ResponseEntity<String> approveRequest(@PathVariable Long id) {
        accountRequestService.approveRequest(id);
        return ResponseEntity.ok("Tài khoản đã được tạo thành công.");
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<String> rejectRequest(@PathVariable Long id) {
        accountRequestService.rejectRequest(id);
        return ResponseEntity.ok("Yêu cầu tạo tài khoản đã bị từ chối.");
    }
}
