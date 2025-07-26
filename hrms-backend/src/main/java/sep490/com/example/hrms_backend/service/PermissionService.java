package sep490.com.example.hrms_backend.service;

import sep490.com.example.hrms_backend.dto.PermissionDTO;

import java.util.List;

public interface PermissionService {
    List<PermissionDTO> getAll();
    PermissionDTO create(PermissionDTO dto);
    PermissionDTO update(Long id, PermissionDTO dto);
    void delete(Long id);
}
