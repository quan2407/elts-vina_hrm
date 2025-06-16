package sep490.com.example.hrms_backend.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sep490.com.example.hrms_backend.repository.BenefitRepository;
import sep490.com.example.hrms_backend.service.BenefitService;

@Service
public class BenefitImpl implements BenefitService {

    @Autowired
    private BenefitRepository benefitRepository;

    @Autowired
    private ModelMapper modelMapper;
}
