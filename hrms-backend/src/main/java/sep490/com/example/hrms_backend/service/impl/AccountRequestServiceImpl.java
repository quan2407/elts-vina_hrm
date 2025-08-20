package sep490.com.example.hrms_backend.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import sep490.com.example.hrms_backend.dto.AccountRequestDTO;
import sep490.com.example.hrms_backend.entity.AccountRequest;
import sep490.com.example.hrms_backend.exception.HRMSAPIException;
import sep490.com.example.hrms_backend.exception.ResourceNotFoundException;
import sep490.com.example.hrms_backend.repository.AccountRepository;
import sep490.com.example.hrms_backend.repository.AccountRequestRepository;
import sep490.com.example.hrms_backend.service.AccountRequestService;
import sep490.com.example.hrms_backend.service.AccountService;
import sep490.com.example.hrms_backend.utils.CurrentUserUtils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountRequestServiceImpl implements AccountRequestService {

    private final AccountRequestRepository requestRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final CurrentUserUtils currentUserUtils;

    @Override
    @Transactional
    public void approveRequest(Long requestId) {
        AccountRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("AccountRequest", "id", requestId));

        if (Boolean.TRUE.equals(request.getApproved())) {
            throw new HRMSAPIException(HttpStatus.BAD_REQUEST, "Yêu cầu đã được duyệt trước đó");
        }

        accountService.createAutoAccountForEmployee(request.getEmployee());

        request.setApproved(true);
        request.setApprovedAt(LocalDateTime.now());
        request.setProcessedBy(currentUserUtils.getCurrentEmployeeName());
        requestRepository.save(request);
    }

    @Override
    @Transactional
    public void rejectRequest(Long requestId) {
        AccountRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("AccountRequest", "id", requestId));

        request.setApproved(false);
        request.setApprovedAt(LocalDateTime.now());
        request.setProcessedBy(currentUserUtils.getCurrentEmployeeName());
        requestRepository.save(request);
    }

    // AccountRequestServiceImpl.java
    @Override
    public Page<AccountRequestDTO> getRequestsByStatus(
            String status,
            int page,
            int size,
            String search,
            Long departmentId,
            Long positionId,
            Long lineId
    ) {
        Page<AccountRequest> basePage;
        if ("approved".equalsIgnoreCase(status)) {
            basePage = requestRepository.findByApproved(true, Pageable.unpaged());
        } else if ("rejected".equalsIgnoreCase(status)) {
            basePage = requestRepository.findByApproved(false, Pageable.unpaged());
        } else if ("pending".equalsIgnoreCase(status)) {
            basePage = requestRepository.findByApprovedIsNull(Pageable.unpaged());
        } else {
            basePage = requestRepository.findAll(Pageable.unpaged());
        }

        var list = basePage.getContent();
        String q = (search == null || search.isBlank()) ? null : search.trim().toLowerCase();
        var filtered = list.stream()
                .filter(ar -> {
                    var e = ar.getEmployee();
                    if (q != null) {
                        boolean byCode = e != null && e.getEmployeeCode() != null
                                && e.getEmployeeCode().toLowerCase().contains(q);
                        boolean byName = e != null && e.getEmployeeName() != null
                                && e.getEmployeeName().toLowerCase().contains(q);
                        if (!(byCode || byName)) return false;
                    }
                    if (departmentId != null) {
                        Long depId = (e != null && e.getDepartment() != null) ? e.getDepartment().getDepartmentId() : null;
                        if (!departmentId.equals(depId)) return false;
                    }
                    if (positionId != null) {
                        Long posId = (e != null && e.getPosition() != null) ? e.getPosition().getPositionId() : null;
                        if (!positionId.equals(posId)) return false;
                    }
                    if (lineId != null) {
                        Long lnId = (e != null && e.getLine() != null) ? e.getLine().getLineId() : null;
                        if (!lineId.equals(lnId)) return false;
                    }
                    return true;
                })
                // --- Sort requestedAt DESC, nulls last ---
                .sorted((a, b) -> {
                    var va = a.getRequestedAt();
                    var vb = b.getRequestedAt();
                    if (va == null && vb == null) return 0;
                    if (va == null) return 1;
                    if (vb == null) return -1;
                    return vb.compareTo(va); // desc
                })
                .toList();

        var dtoList = filtered.stream()
                .map(req -> {
                    var emp = req.getEmployee();
                    return new AccountRequestDTO(
                            req.getId(),
                            emp != null ? emp.getEmployeeCode() : null,
                            emp != null ? emp.getEmployeeName() : null,
                            (emp != null && emp.getPosition() != null) ? emp.getPosition().getPositionName() : null,
                            (emp != null && emp.getDepartment() != null) ? emp.getDepartment().getDepartmentName() : null,
                            (emp != null && emp.getLine() != null) ? emp.getLine().getLineName() : null,
                            req.getRequestedAt(),
                            req.getApproved()
                    );
                })
                .toList();

        int start = Math.min(page * size, dtoList.size());
        int end = Math.min(start + size, dtoList.size());
        var pageContent = dtoList.subList(start, end);

        return new PageImpl<>(pageContent, PageRequest.of(page, size), dtoList.size());
    }




}
