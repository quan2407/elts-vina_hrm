package sep490.com.example.hrms_backend.service;

import sep490.com.example.hrms_backend.dto.PermissionDTO;

import java.util.List;
import java.util.Map;

public interface PermissionService {
    Map<String, List<PermissionDTO>> getGroupedByModule();
    PermissionDTO create(PermissionDTO dto);
    PermissionDTO update(Long id, PermissionDTO dto);
    void delete(Long id);
}
