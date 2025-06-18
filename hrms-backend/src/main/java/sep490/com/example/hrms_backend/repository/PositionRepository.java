package sep490.com.example.hrms_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sep490.com.example.hrms_backend.entity.Position;

public interface PositionRepository extends JpaRepository<Position, Long> {
}
