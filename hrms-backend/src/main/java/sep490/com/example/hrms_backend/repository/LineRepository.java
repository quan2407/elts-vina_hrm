package sep490.com.example.hrms_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sep490.com.example.hrms_backend.entity.Line;

import java.util.List;

public interface LineRepository extends JpaRepository<Line,Long> {
    List<Line> findByDepartmentDepartmentId(Long departmentId);

    List<Line> findByDepartment_DepartmentId(Long departmentId);
    @Query("SELECT l FROM Line l JOIN FETCH l.department")
    List<Line> findAllWithDepartment();
}
