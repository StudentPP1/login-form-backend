package github.studentpp1.advancedloginform.users.models;

import github.studentpp1.advancedloginform.users.data.CreateUserRequest;
import github.studentpp1.advancedloginform.users.data.UpdateUserRequest;
import github.studentpp1.advancedloginform.users.entity.AbstractEntity;
import github.studentpp1.advancedloginform.users.enums.Role;
import github.studentpp1.advancedloginform.utils.providers.ApplicationContextProvider;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

@Entity(name="users")
@Getter
@NoArgsConstructor
public class User extends AbstractEntity implements UserDetails {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    @Setter
    private boolean verified = false;
    @Setter
    private byte[] profileImage;
    @Enumerated(EnumType.STRING)
    @Setter
    private Role role;

    @Setter
    @OneToOne(mappedBy = "user")
    private VerificationCode verificationCode;

    // automatically pull back child entities -> FetchType.EAGER
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<UserConnectedAccount> connectedAccounts = new ArrayList<>();


    public User(CreateUserRequest data) {
        PasswordEncoder passwordEncoder = ApplicationContextProvider.bean(PasswordEncoder.class);
        this.email = data.getEmail();
        this.password = passwordEncoder.encode(data.getPassword());
        this.firstName = data.getFirstName();
        this.lastName = data.getLastName();
        this.role = Role.USER;
    }

    public User (OAuth2User oAuth2User) {
        this.email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        if (name != null) {
            List<String> names = List.of(name.split(" "));
            if (names.size() > 1) {
                this.firstName = names.get(0);
                this.lastName = names.get(1);
            } else {
                this.firstName = names.get(0);
            }
        }
        this.verified = true;
        this.role = Role.USER;
    }

    public void addConnectedAccount(UserConnectedAccount connectedAccount) {
        connectedAccounts.add(connectedAccount);
    }

    public void update(UpdateUserRequest request) {
        this.firstName = request.getFirstName();
        this.lastName = request.getLastName();
    }

    public void updatePassword(String newPassword) {
        PasswordEncoder passwordEncoder = ApplicationContextProvider.bean(PasswordEncoder.class);
        this.password = passwordEncoder.encode(newPassword);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // If you want to not allow the user to login before verifying their email, you can change this to
    // return verified;
    @Override
    public boolean isEnabled() {
        return verified;
    }
}