package github.studentpp1.advancedloginform.users.repository;


import github.studentpp1.advancedloginform.users.models.UserConnectedAccount;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface UserConnectedAccountRepository extends JpaRepository<UserConnectedAccount, Long> {
    Optional<UserConnectedAccount> findByProviderAndProviderId(String provider, String providerId);
}
