package github.studentpp1.advancedloginform.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j // logger
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender emailSender;

    public void sendHtmlMessage(List<String> to, String subject, String htmlBody){
        try {
            // body of message
            MimeMessage message = emailSender.createMimeMessage();
            // true -> відсилання вкладенного змісту
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to.toArray(new String[0]));
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            emailSender.send(message);
            log.info("Sending email to: {}", to);
        } catch (MessagingException e) {
            throw new RuntimeException("Error sending email", e);
        }
    }

    public void sendSimpleEmail(List<String> to, String subject, String content) {
        log.info("Sending email to: {}", to);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to.toArray(new String[0]));
        message.setSubject(subject);
        message.setText(content);
        emailSender.send(message);
    }
}
