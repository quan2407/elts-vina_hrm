package sep490.com.example.hrms_backend.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sep490.com.example.hrms_backend.entity.BenefitRegistration;
import sep490.com.example.hrms_backend.repository.BenefitRegistrationRepository;
import sep490.com.example.hrms_backend.repository.BenefitRepository;
import sep490.com.example.hrms_backend.service.BenefitRegistrationService;

@Service
public class BenefitRegistrationImpl implements BenefitRegistrationService {

    @Autowired
    private BenefitRegistrationRepository benefitRegistrationRepository;

    @Autowired
    private ModelMapper modelMapper;
}
