package github.studentpp1.advancedloginform.auth.data;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class LoginRequest {
    @Email
    private String email;
    private String password;
}
