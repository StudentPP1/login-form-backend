package github.studentpp1.advancedloginform.users.jobs;

import github.studentpp1.advancedloginform.config.ApplicationProperties;
import github.studentpp1.advancedloginform.email.EmailService;
import github.studentpp1.advancedloginform.users.models.User;
import github.studentpp1.advancedloginform.users.models.VerificationCode;
import github.studentpp1.advancedloginform.users.repository.UserRepository;
import github.studentpp1.advancedloginform.users.repository.VerificationCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class SendVerificationEmailJob {
    private final UserRepository userRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final SpringTemplateEngine templateEngine;
    private final EmailService emailService;
    private final ApplicationProperties applicationProperties;

    @Transactional
    public void run(Long userId) throws Exception {
        log.info("check if we have a user");
        // check if we have a user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        log.info("Sending welcome email to user with id: {}", userId);
        if (user.getVerificationCode() != null && !user.getVerificationCode().isEmailSent()) {
            // if code != null & not already sent
            log.info("call sendWelcomeEmail");
            sendWelcomeEmail(user, user.getVerificationCode());
        }
    }

    private void sendWelcomeEmail(User user, VerificationCode code) {
        String verificationLink = applicationProperties.getBaseUrl() + "/api/users/verify-email?token=" + code.getCode();
        Context thymeleafContext = new Context();
        thymeleafContext.setVariable("user", user);
        thymeleafContext.setVariable("verificationLink", verificationLink);
        thymeleafContext.setVariable("applicationName", applicationProperties.getApplicationName());
        String htmlBody = templateEngine.process("welcome-email", thymeleafContext);
        emailService.sendHtmlMessage(List.of(user.getEmail()), "Welcome to our platform", htmlBody);
        code.setEmailSent(true);
        verificationCodeRepository.save(code);
    }
}
