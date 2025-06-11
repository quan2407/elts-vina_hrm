package sep490.com.example.hrms_backend.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    @GetMapping
    public String view(){
        return "view is accept";
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String create(){
        return "create is ok";
    }
}
