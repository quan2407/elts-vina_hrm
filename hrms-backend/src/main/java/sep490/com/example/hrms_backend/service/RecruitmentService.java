package sep490.com.example.hrms_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sep490.com.example.hrms_backend.enums.RecruitmentStatus;

@Component
public class RecruitmentService {

    @Autowired
    private RecruitmentStatus recruitmentStatus;


}
