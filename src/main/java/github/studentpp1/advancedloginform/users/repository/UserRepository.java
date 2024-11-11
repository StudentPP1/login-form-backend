package github.studentpp1.advancedloginform.users.repository;

import github.studentpp1.advancedloginform.users.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
