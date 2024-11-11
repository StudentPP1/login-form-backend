package github.studentpp1.advancedloginform.users.service;

import github.studentpp1.advancedloginform.users.data.CreateUserRequest;
import github.studentpp1.advancedloginform.users.data.UserResponse;
import github.studentpp1.advancedloginform.users.models.User;
import github.studentpp1.advancedloginform.users.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserResponse create(@Valid CreateUserRequest request) {
        User user = new User(request);
        user = userRepository.save(user);
        // TODO: Send verification email
        return new UserResponse(user);
    }
}
