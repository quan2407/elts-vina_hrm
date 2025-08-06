package sep490.com.example.hrms_backend.dto;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolePermissionRequestDTO {
    private Set<Long> permissionIds;
}
