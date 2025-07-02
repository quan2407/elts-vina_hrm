package sep490.com.example.hrms_backend.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import sep490.com.example.hrms_backend.exception.HRMSAPIException;

@Component
public class CurrentUserUtils {

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null ||
                authentication.getName().isBlank()) {
            throw new HRMSAPIException("Username is missing or empty!");
        }

        return authentication.getName();
    }
}
