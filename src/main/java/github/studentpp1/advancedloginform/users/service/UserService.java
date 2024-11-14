package github.studentpp1.advancedloginform.users.service;

import github.studentpp1.advancedloginform.auth.utils.SecurityUtils;
import github.studentpp1.advancedloginform.users.data.CreateUserRequest;
import github.studentpp1.advancedloginform.users.data.UpdateUserPasswordRequest;
import github.studentpp1.advancedloginform.users.data.UpdateUserRequest;
import github.studentpp1.advancedloginform.users.data.UserResponse;
import github.studentpp1.advancedloginform.users.jobs.SendResetPasswordEmailJob;
import github.studentpp1.advancedloginform.users.jobs.SendVerificationEmailJob;
import github.studentpp1.advancedloginform.users.models.PasswordResetToken;
import github.studentpp1.advancedloginform.users.models.UploadedFile;
import github.studentpp1.advancedloginform.users.models.User;
import github.studentpp1.advancedloginform.users.models.VerificationCode;
import github.studentpp1.advancedloginform.users.repository.PasswordResetTokenRepository;
import github.studentpp1.advancedloginform.users.repository.UploadedFileRepository;
import github.studentpp1.advancedloginform.users.repository.UserRepository;
import github.studentpp1.advancedloginform.users.repository.VerificationCodeRepository;
import github.studentpp1.advancedloginform.users.utils.ImageUtils;
import github.studentpp1.advancedloginform.utils.exception.ApiException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CountDownLatch;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final SendVerificationEmailJob sendVerificationEmailJob;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final SendResetPasswordEmailJob sendResetPasswordEmailJob;
    private final PasswordEncoder passwordEncoder;
    private final UploadedFileRepository uploadedFileRepository;

    private TaskScheduler scheduler = new SimpleAsyncTaskScheduler();

    @Transactional
    public UserResponse create(@Valid CreateUserRequest request) throws Exception {
        User user = new User(request);
        user = userRepository.save(user);
        User finalUser = user;
        new Thread(() -> {
                    try {
                        sendVerificationEmail(finalUser);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).start();
        return new UserResponse(user);
    }

    // create a new thread to send email and wait result of it
    private void sendVerificationEmail(User user) throws Exception {
        VerificationCode verificationCode = new VerificationCode(user);
        user.setVerificationCode(verificationCode); // set relation
        verificationCodeRepository.save(verificationCode);
        sendVerificationEmailJob.run(user.getId());
    }

    @Transactional
    public void verifyEmail(String code) {
        VerificationCode verificationCode = verificationCodeRepository.findByCode(code)
                .orElseThrow(() -> ApiException.builder().status(400).message("Invalid token").build());
        User user = verificationCode.getUser();
        user.setVerified(true);
        userRepository.save(user);
        verificationCodeRepository.delete(verificationCode);
    }

    @Transactional
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> ApiException.builder().status(404).message("User not found").build());
        PasswordResetToken resetToken = new PasswordResetToken(user);
        passwordResetTokenRepository.save(resetToken);
        new Thread(() -> {
            try {
                sendResetPasswordEmailJob.run(resetToken.getId());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @Transactional
    public void resetPassword(UpdateUserPasswordRequest request) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(request.getPasswordResetToken())
                .orElseThrow(() -> ApiException.builder().status(404).message("Password reset token not found").build());
        if (passwordResetToken.isExpired()) {
            throw ApiException.builder().status(400).message("Password reset token is expired").build();
        }
        User user = passwordResetToken.getUser();
        user.updatePassword(request.getPassword());
        userRepository.save(user);
    }

    @Transactional
    public UserResponse update(UpdateUserRequest request) {
        User user = SecurityUtils.getAuthenticatedUser();
        // lazy request & error if not found
        user = userRepository.getReferenceById(user.getId());
        user.update(request);
        user = userRepository.save(user);
        return new UserResponse(user);
    }

    @Transactional
    public UserResponse updatePassword(UpdateUserPasswordRequest request) {
        User user = SecurityUtils.getAuthenticatedUser();
        if (user.getPassword() != null
                && !passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw ApiException.builder().status(400).message("Wrong password").build();
        }
        user.updatePassword(request.getPassword());
        user = userRepository.save(user);
        return new UserResponse(user);
    }

    public UserResponse updateProfilePicture(MultipartFile file) throws IOException {
        User user = SecurityUtils.getAuthenticatedUser();
        UploadedFile picture = new UploadedFile(
                ImageUtils.compressImage(file.getBytes()),
                file.getSize(),
                file.getOriginalFilename(),
                user
        );
        user.setProfileImage(picture.getFile());
        user = userRepository.save(user);
        uploadedFileRepository.save(picture);
        return new UserResponse(user);
    }
}
