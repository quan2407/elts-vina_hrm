package sep490.com.example.hrms_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sep490.com.example.hrms_backend.service.BenefitService;

@RestController
@RequestMapping("api/benefit")
public class BenefitController {

    @Autowired
    private BenefitService benefitService;



}
