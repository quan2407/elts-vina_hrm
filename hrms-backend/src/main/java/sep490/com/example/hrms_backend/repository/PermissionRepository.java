package sep490.com.example.hrms_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sep490.com.example.hrms_backend.entity.Permission;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    boolean existsByName(String name);
    boolean existsByApiPathAndMethod(String apiPath, String method);

    Optional<Permission> findByName(String name);

    Optional<Permission> findByApiPathAndMethod(String apiPath, String method);
}
