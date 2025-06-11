package sep490.com.example.hrms_backend.service;

import sep490.com.example.hrms_backend.dto.LoginDto;

public interface AuthService {
    String login(LoginDto loginDto);
}
