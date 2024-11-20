package github.studentpp1.advancedloginform.users.models;

import github.studentpp1.advancedloginform.users.entity.AbstractEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class UserConnectedAccount extends AbstractEntity {
    private String provider;
    private String providerId;
    private LocalDateTime connectedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public UserConnectedAccount(String provider, String providerId, User user) {
        this.provider = provider;
        this.providerId = providerId;
        this.connectedAt = LocalDateTime.now();
        this.user = user;
    }
}
