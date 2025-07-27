package sep490.com.example.hrms_backend.service;

import java.util.Collection;

public interface PermissionRegistrationService {
    void registerPermission(String apiPath, String method, String module, Collection<String> roleNames);
}
