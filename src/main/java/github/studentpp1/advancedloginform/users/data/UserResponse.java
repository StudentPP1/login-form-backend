package github.studentpp1.advancedloginform.users.data;

import github.studentpp1.advancedloginform.users.enums.Role;
import github.studentpp1.advancedloginform.users.models.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
public class UserResponse {
    private Long id;
    private Role role;
    private String firstName;
    private String lastName;
    private String email;
    private byte[] profileImage;
    private List<ConnectedAccountResponse> connectedAccounts = new ArrayList<>();
    private List<String> authorities = new ArrayList<>();

    public UserResponse(User user) {
        this.id = user.getId();
        this.role = user.getRole();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.profileImage = user.getProfileImage();
        user.getConnectedAccounts().forEach((provider) -> {
            this.connectedAccounts.add(new ConnectedAccountResponse(provider.getProvider(), provider.getConnectedAt()));
        });
    }

    public UserResponse(User user, Collection<? extends GrantedAuthority> authorities) {
        this.id = user.getId();
        this.role = user.getRole();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.profileImage = user.getProfileImage();
        user.getConnectedAccounts().forEach((provider) -> {
            this.connectedAccounts.add(new ConnectedAccountResponse(provider.getProvider(), provider.getConnectedAt()));
        });
        authorities.forEach(authority -> {
            this.authorities.add(authority.getAuthority());
        });
    }

    public record ConnectedAccountResponse(String provider, LocalDateTime connectedAt) {
    }

}
