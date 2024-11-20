package github.studentpp1.advancedloginform.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@Data
// compare values in app.yml to fields with <prefix>
@ConfigurationProperties(prefix = "app")
public class ApplicationProperties {
    private List<String> allowedOrigins;
    private String applicationName;
    private String baseUrl;
    private String frontUrl;
    private String loginPageUrl;
    private String loginSuccessUrl;
    private String adminUserEmail;
    private String adminUserPassword;
}
