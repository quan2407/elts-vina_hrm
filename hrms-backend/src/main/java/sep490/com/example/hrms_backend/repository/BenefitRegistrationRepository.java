package sep490.com.example.hrms_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sep490.com.example.hrms_backend.entity.Benefit;
import sep490.com.example.hrms_backend.entity.BenefitRegistration;
import sep490.com.example.hrms_backend.entity.Employee;

public interface BenefitRegistrationRepository extends JpaRepository<BenefitRegistration, Long> {

    Page<Benefit> findByEmployeeOrderById(Employee employee, Pageable pageable);

    long countByBenefit(Benefit benefit);

    boolean existsByBenefitAndEmployee(Benefit benefit, Employee employee);
}

