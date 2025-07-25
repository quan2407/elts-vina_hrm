package sep490.com.example.hrms_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

public class HRMSFieldValidationException extends ResponseStatusException {
    private final Map<String, List<String>> fieldErrors;

    public HRMSFieldValidationException(Map<String, List<String>> fieldErrors) {
        super(HttpStatus.BAD_REQUEST, "Validation failed");
        this.fieldErrors = fieldErrors;
    }

    public Map<String, List<String>> getFieldErrors() {
        return fieldErrors;
    }
}
