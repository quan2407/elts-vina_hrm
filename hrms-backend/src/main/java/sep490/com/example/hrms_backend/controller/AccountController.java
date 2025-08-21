package sep490.com.example.hrms_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sep490.com.example.hrms_backend.dto.AccountResponseDTO;
import sep490.com.example.hrms_backend.service.AccountService;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<Page<AccountResponseDTO>> getAllAccounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long positionId,
            @RequestParam(required = false) Long lineId,
            @RequestParam(required = false) String role
    ) {
        return ResponseEntity.ok(
                accountService.getAllAccounts(page, size, search, departmentId, positionId, lineId, role)
        );
    }


    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<?> toggleAccountStatus(@PathVariable Long id) {
        accountService.toggleAccountStatus(id);
        return ResponseEntity.ok().build();
    }
}
