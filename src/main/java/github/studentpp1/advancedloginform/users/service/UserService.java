package github.studentpp1.advancedloginform.users.service;

import github.studentpp1.advancedloginform.users.data.CreateUserRequest;
import github.studentpp1.advancedloginform.users.data.UserResponse;
import github.studentpp1.advancedloginform.users.jobs.SendVerificationEmailJob;
import github.studentpp1.advancedloginform.users.models.User;
import github.studentpp1.advancedloginform.users.models.VerificationCode;
import github.studentpp1.advancedloginform.users.repository.UserRepository;
import github.studentpp1.advancedloginform.users.repository.VerificationCodeRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CountDownLatch;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final SendVerificationEmailJob sendVerificationEmailJob;
    private TaskScheduler scheduler = new SimpleAsyncTaskScheduler();

    @Transactional
    public UserResponse create(@Valid CreateUserRequest request) throws Exception {
        User user = new User(request);
        user = userRepository.save(user);
        User finalUser = user;
        new Thread(
                () -> {
                    try {
                        sendVerificationEmail(finalUser);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        ).start();
        return new UserResponse(user);
    }

    // create a new thread to send email and wait result of it
    private void sendVerificationEmail(User user) throws Exception {
        VerificationCode verificationCode = new VerificationCode(user);
        user.setVerificationCode(verificationCode); // set relation
        verificationCodeRepository.save(verificationCode);
        sendVerificationEmailJob.run(user.getId());
    }
}
