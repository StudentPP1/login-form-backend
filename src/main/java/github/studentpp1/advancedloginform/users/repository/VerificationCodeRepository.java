package github.studentpp1.advancedloginform.users.repository;

import github.studentpp1.advancedloginform.users.models.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
}
