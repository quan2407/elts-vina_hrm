package sep490.com.example.hrms_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sep490.com.example.hrms_backend.service.BenefitRegistrationService;

@RestController
@RequestMapping("api/benefit")
@RequiredArgsConstructor
public class BenefitRegistrationController {


    private final BenefitRegistrationService benefitRegistrationService;

    //view benefitByEmployee

    //đăng kí Benefit


}
