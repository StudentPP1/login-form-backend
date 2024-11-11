package github.studentpp1.advancedloginform.users.models;

import github.studentpp1.advancedloginform.users.data.CreateUserRequest;
import github.studentpp1.advancedloginform.users.entity.AbstractEntity;
import github.studentpp1.advancedloginform.users.enums.Role;
import github.studentpp1.advancedloginform.util.ApplicationContextProvider;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
public class User extends AbstractEntity {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private boolean verified = false;
    @Enumerated(EnumType.STRING)
    private Role role;

    public User(CreateUserRequest data) {
        // inject a PasswordEncoder bean
        PasswordEncoder passwordEncoder = ApplicationContextProvider.bean(PasswordEncoder.class);
        this.email = data.getEmail();
        this.password = passwordEncoder.encode(data.getPassword());
        this.firstName = data.getFirstName();
        this.lastName = data.getLastName();
        this.role = Role.USER;
    }
}
