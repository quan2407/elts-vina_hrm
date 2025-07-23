package sep490.com.example.hrms_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sep490.com.example.hrms_backend.entity.Benefit;
import sep490.com.example.hrms_backend.entity.BenefitRegistration;
import sep490.com.example.hrms_backend.entity.Employee;

import java.util.List;

public interface BenefitRegistrationRepository extends JpaRepository<BenefitRegistration, Long> {

    Page<Benefit> findByEmployeeOrderById(Employee employee, Pageable pageable);

    @Query("SELECT bp.position.positionId  FROM BenefitPosition bp WHERE bp.benefit.id = :benefitId")
    List<Long> findPositionIdsByBenefitId(@Param("benefitId") Long benefitId);

//    long countByBenefit(Benefit benefit);

//    boolean existsByBenefitAndEmployee(Benefit benefit, Employee employee);

//    @Query("SELECT br.benefit.id, COUNT(br.employee)" +
//            "FROM BenefitRegistration br " +
//            "WHERE br.isRegister = true AND br.benefit IN :benefitIds " +
//            "GROUP BY br.benefit")
//    List<Object[]> countRegisteredParticipants(@Param("benefitIds") List<Long> benefitIds);
}

