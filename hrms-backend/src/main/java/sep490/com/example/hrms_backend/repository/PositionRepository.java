package sep490.com.example.hrms_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sep490.com.example.hrms_backend.entity.Position;

import java.util.List;

public interface PositionRepository extends JpaRepository<Position, Long> {
    boolean existsByPositionName(String positionName);

    @Query("SELECT COUNT(p) > 0 FROM Position p JOIN p.departments d WHERE p.positionId = :positionId AND d.departmentId = :departmentId")
    boolean existsDepartmentPositionMapping(@Param("departmentId") Long departmentId, @Param("positionId") Long positionId);

    List<Position> findByDepartments_DepartmentId(Long departmentId);
}
