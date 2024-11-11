package github.studentpp1.advancedloginform.users.data;

import github.studentpp1.advancedloginform.users.enums.Role;
import github.studentpp1.advancedloginform.users.models.User;
import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private Role role;
    private String email;
    private String firstName;
    private String lastName;

    public UserResponse(User user) {
        this.id = user.getId();
        this.role = user.getRole();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
    }

}
