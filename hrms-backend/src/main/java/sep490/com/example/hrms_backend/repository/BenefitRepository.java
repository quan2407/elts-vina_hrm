package sep490.com.example.hrms_backend.repository;

import jakarta.validation.constraints.NotBlank;
import jdk.jfr.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sep490.com.example.hrms_backend.entity.Benefit;

@Repository
public interface BenefitRepository extends JpaRepository<Benefit, Long>, JpaSpecificationExecutor<Benefit> {

    Benefit findByTitle(@NotBlank String title);

    Page<Benefit> findByTitleLikeIgnoreCase(String s, Pageable pageable);

    
}
