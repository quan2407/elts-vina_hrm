package sep490.com.example.hrms_backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class HRMSAPIException extends RuntimeException {
    @Getter
    private HttpStatus status;
    private String message;

    public HRMSAPIException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }


    public HRMSAPIException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST; // default
    }
}

