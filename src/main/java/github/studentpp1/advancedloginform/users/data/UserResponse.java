package github.studentpp1.advancedloginform.users.data;

import github.studentpp1.advancedloginform.users.enums.Role;
import github.studentpp1.advancedloginform.users.models.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
public class UserResponse {
    private Long id;
    private Role role;
    private String email;
    private String firstName;
    private String lastName;
    private List<String> authorities = new ArrayList<>();

    public UserResponse(User user) {
        this.id = user.getId();
        this.role = user.getRole();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
    }
    public UserResponse(User user, Collection<? extends GrantedAuthority> authorities) {
        this.id = user.getId();
        this.role = user.getRole();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        authorities.forEach(authority -> {
            this.authorities.add(authority.getAuthority());
        });
    }

}
