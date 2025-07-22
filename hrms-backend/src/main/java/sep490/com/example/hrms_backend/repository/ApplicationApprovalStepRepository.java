package sep490.com.example.hrms_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import sep490.com.example.hrms_backend.entity.ApplicationApprovalStep;
import sep490.com.example.hrms_backend.enums.ApprovalStepStatus;

public interface ApplicationApprovalStepRepository extends JpaRepository<ApplicationApprovalStep, Long> {


    Page<ApplicationApprovalStep> findByStepAndApprover_EmployeeIdAndStatus(int i, Long approverId, ApprovalStepStatus stepStatus, PageRequest of);

    Page<ApplicationApprovalStep> findByStepAndApprover_EmployeeId(int i, Long approverId, PageRequest of);
}