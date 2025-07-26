// PermissionServiceImpl.java
package sep490.com.example.hrms_backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sep490.com.example.hrms_backend.dto.PermissionDTO;
import sep490.com.example.hrms_backend.entity.Permission;
import sep490.com.example.hrms_backend.exception.ResourceNotFoundException;
import sep490.com.example.hrms_backend.mapper.PermissionMapper;
import sep490.com.example.hrms_backend.repository.PermissionRepository;
import sep490.com.example.hrms_backend.service.PermissionService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    @Override
    public List<PermissionDTO> getAll() {
        return permissionRepository.findAll()
                .stream()
                .map(PermissionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PermissionDTO create(PermissionDTO dto) {
        if (permissionRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Permission đã tồn tại");
        }
        if (permissionRepository.existsByApiPathAndMethod(dto.getApiPath(), dto.getMethod())) {
            throw new IllegalArgumentException("Permission trùng apiPath + method đã tồn tại");
        }
        Permission permission = PermissionMapper.toEntity(dto);
        permission.setCreatedAt(LocalDateTime.now());
        return PermissionMapper.toDTO(permissionRepository.save(permission));
    }

    @Override
    @Transactional
    public PermissionDTO update(Long id, PermissionDTO dto) {
        Permission existing = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission", "id", id));

        existing.setName(dto.getName());
        existing.setApiPath(dto.getApiPath());
        existing.setMethod(dto.getMethod());
        existing.setModule(dto.getModule());
        existing.setUpdatedAt(LocalDateTime.now());

        return PermissionMapper.toDTO(permissionRepository.save(existing));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!permissionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Permission", "id", id);
        }
        permissionRepository.deleteById(id);
    }
}
