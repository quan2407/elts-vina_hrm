package sep490.com.example.hrms_backend.service;

import sep490.com.example.hrms_backend.dto.AccountRequestDTO;

import java.util.List;

public interface AccountRequestService {

    void approveRequest(Long requestId);
    void rejectRequest(Long requestId);
    List<AccountRequestDTO> getRequestsByStatus(String status);

}
