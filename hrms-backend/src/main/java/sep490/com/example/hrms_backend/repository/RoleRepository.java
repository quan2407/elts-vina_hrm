package sep490.com.example.hrms_backend.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sep490.com.example.hrms_backend.entity.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(String roleName);
    @Query("SELECT r FROM Role r WHERE r.roleId = :roleId")
    @EntityGraph(attributePaths = "permissions")
    Optional<Role> findWithPermissionsById(@Param("roleId") Long roleId);

    Optional<Role> findByName(String roleAdmin);
}
