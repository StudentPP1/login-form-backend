package github.studentpp1.advancedloginform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@SpringBootApplication
public class AdvancedLoginFormApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdvancedLoginFormApplication.class, args);
    }

}
