package github.studentpp1.advancedloginform.auth.oauth2;

import github.studentpp1.advancedloginform.config.ApplicationProperties;
import github.studentpp1.advancedloginform.users.models.User;
import github.studentpp1.advancedloginform.users.models.UserConnectedAccount;
import github.studentpp1.advancedloginform.users.repository.UserConnectedAccountRepository;
import github.studentpp1.advancedloginform.users.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private final UserConnectedAccountRepository userConnectedAccountRepository;
    private final ApplicationProperties applicationProperties;
    private final UserRepository userRepository;

    public OAuth2LoginSuccessHandler(
            UserConnectedAccountRepository userConnectedAccountRepository,
            ApplicationProperties applicationProperties,
            UserRepository userRepository) {
        this.userConnectedAccountRepository = userConnectedAccountRepository;
        this.applicationProperties = applicationProperties;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        log.info("get auth");
        OAuth2AuthenticationToken auth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        String provider = auth2AuthenticationToken.getAuthorizedClientRegistrationId(); // Google / GitHub
        String providerId = authentication.getName(); // username on provider
        String email = auth2AuthenticationToken.getPrincipal().getAttribute("email");

        System.out.println(provider);
        System.out.println(providerId);
        // check if you have user based on this account
        Optional<UserConnectedAccount> connectedAccount = userConnectedAccountRepository.findByProviderAndProviderId(
                provider, providerId
        );
        if (connectedAccount.isPresent()) {
            authenticateUser(connectedAccount.get().getUser(), response);
            return;
        }

        // find user by email & add connect user
        // or create a new user
        User existingUser = userRepository.findByEmail(email).orElse(null);
        if (existingUser != null) {
            UserConnectedAccount newConnectedAccount = new UserConnectedAccount(provider, providerId, existingUser);
            existingUser.addConnectedAccount(newConnectedAccount);
            System.out.println(newConnectedAccount);
            existingUser = userRepository.save(existingUser);
            authenticateUser(existingUser, response);
        }
        else {
            User newUser = createUserFromOauth2User(auth2AuthenticationToken);
            authenticateUser(newUser, response);
        }
    }

    private void authenticateUser(User user, HttpServletResponse response) throws IOException {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                user,
                null,
                user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        response.sendRedirect(applicationProperties.getLoginSuccessUrl());
    }
    private User createUserFromOauth2User(OAuth2AuthenticationToken token) {
        User user = new User(token.getPrincipal());
        String provider = token.getAuthorizedClientRegistrationId();
        String providerId = token.getName();
        UserConnectedAccount connectedAccount = new UserConnectedAccount(provider, providerId, user);
        user.addConnectedAccount(connectedAccount);
        user = userRepository.save(user);
        userConnectedAccountRepository.save(connectedAccount);
        return user;
    }
}
