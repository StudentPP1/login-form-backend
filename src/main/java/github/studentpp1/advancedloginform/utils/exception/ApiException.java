package github.studentpp1.advancedloginform.utils.exception;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

// represent own exception globally that we can handle it
@Getter
@Builder
public class ApiException extends RuntimeException {
    private String message;
    private int status = 400;
    private Map<String, String> errors;
}