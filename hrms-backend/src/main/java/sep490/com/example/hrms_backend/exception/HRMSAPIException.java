package sep490.com.example.hrms_backend.exception;

import org.springframework.http.HttpStatus;

public class HRMSAPIException extends RuntimeException{
    private HttpStatus status;
    private String message;

    public HRMSAPIException(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HRMSAPIException(String message, HttpStatus status, String message1) {
        super(message);
        this.status = status;
        this.message = message1;
    }

    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
