package github.studentpp1.advancedloginform.users.data;

import github.studentpp1.advancedloginform.util.validators.PasswordMatch;
import github.studentpp1.advancedloginform.util.validators.Unique;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@PasswordMatch(
        passwordField = "password",
        passwordConfirmationField = "passwordConfirmation")
@Builder
public class CreateUserRequest {
    @Email
    @Unique( // check if email is already exists in database
            columnName = "email",
            tableName = "users",
            message = "User with this email already exists")
    private String email;
    @NotNull
    @Length(min = 8)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
            message = "must contain at least one uppercase letter, one lowercase letter, and one digit.")
    // these two fields must be the same (check it in PasswordMatch)
    private String password;
    private String passwordConfirmation;
    private String firstName;
    private String lastName;
}
