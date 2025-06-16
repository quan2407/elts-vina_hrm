package sep490.com.example.hrms_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/benefit")
public class BenefitRegistrationController {

    @Autowired
    private BenefitRegistrationController benefitRegistrationController;


}
