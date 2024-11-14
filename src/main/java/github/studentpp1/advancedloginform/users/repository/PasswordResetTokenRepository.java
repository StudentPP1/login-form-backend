package github.studentpp1.advancedloginform.users.repository;

import github.studentpp1.advancedloginform.users.models.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findById(Long id);

    Optional<PasswordResetToken> findByToken(String token);
}