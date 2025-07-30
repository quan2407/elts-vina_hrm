package sep490.com.example.hrms_backend.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
import java.util.List;

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
    @Override
    public Page<AccountRequestDTO> getRequestsByStatus(String status, int page, int size) {
        List<AccountRequest> requests;

        if ("approved".equalsIgnoreCase(status)) {
            requests = requestRepository.findByApproved(true);
        } else if ("rejected".equalsIgnoreCase(status)) {
            requests = requestRepository.findByApproved(false);
        } else {
            requests = requestRepository.findByApprovedIsNull();
        }

        List<AccountRequestDTO> dtoList = requests.stream()
                .map(req -> new AccountRequestDTO(
                        req.getId(),
                        req.getEmployee().getEmployeeName(),
                        req.getRequestedAt(),
                        req.getApproved()
                ))
                .toList();

        // Phân trang thủ công từ danh sách
        int start = Math.min(page * size, dtoList.size());
        int end = Math.min(start + size, dtoList.size());
        List<AccountRequestDTO> pageContent = dtoList.subList(start, end);

        return new PageImpl<>(pageContent, PageRequest.of(page, size), dtoList.size());
    }



}
