package github.studentpp1.advancedloginform.users.jobs;

import github.studentpp1.advancedloginform.config.ApplicationProperties;
import github.studentpp1.advancedloginform.email.EmailService;
import github.studentpp1.advancedloginform.users.models.PasswordResetToken;
import github.studentpp1.advancedloginform.users.models.User;
import github.studentpp1.advancedloginform.users.repository.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SendResetPasswordEmailJob {

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    private final ApplicationProperties applicationProperties;
    private final SpringTemplateEngine templateEngine;

    @Transactional
    public void run(Long tokenId) {
        log.info("check if we have a token");
        PasswordResetToken token = passwordResetTokenRepository.findById(tokenId)
                .orElseThrow(() -> new IllegalArgumentException("Token not found"));
        if (!token.isEmailSent()) {
            log.info("call sendResetPasswordEmail");
            sendResetPasswordEmail(token.getUser(), token);
        }
    }

    private void sendResetPasswordEmail(User user, PasswordResetToken token) {
        String link = applicationProperties.getBaseUrl() + "/auth/reset-password?token=" + token.getToken();
        Context context = new Context();
        context.setVariable("user", user);
        context.setVariable("link", link);
        String html = templateEngine.process("password-reset", context);
        emailService.sendHtmlMessage(List.of(user.getEmail()), "Password reset requested", html);
        token.onEmailSent();
        passwordResetTokenRepository.save(token);
    }
}
