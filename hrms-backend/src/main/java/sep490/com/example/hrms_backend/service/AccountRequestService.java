package sep490.com.example.hrms_backend.service;

import org.springframework.data.domain.Page;
import sep490.com.example.hrms_backend.dto.AccountRequestDTO;

public interface AccountRequestService {

    void approveRequest(Long requestId);
    void rejectRequest(Long requestId);
    Page<AccountRequestDTO> getRequestsByStatus(
            String status, int page, int size,
            String search, Long departmentId, Long positionId, Long lineId
    );


}
