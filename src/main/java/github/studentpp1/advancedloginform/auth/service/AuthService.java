package github.studentpp1.advancedloginform.auth.service;

import github.studentpp1.advancedloginform.auth.data.LoginRequest;
import github.studentpp1.advancedloginform.auth.utils.SecurityUtils;
import github.studentpp1.advancedloginform.users.data.UserResponse;
import github.studentpp1.advancedloginform.users.models.User;
import github.studentpp1.advancedloginform.users.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository contextRepository = new HttpSessionSecurityContextRepository();
    SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();

    /*
    https://docs.spring.io/spring-security/reference/servlet/authentication/session-management.html
    */
    public void login(
            HttpServletRequest request,
            HttpServletResponse response,
            LoginRequest loginRequest) {
        var token = UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.getEmail(), loginRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolderStrategy holderStrategy = SecurityContextHolder.getContextHolderStrategy();
        SecurityContext context = holderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        holderStrategy.setContext(context);
        contextRepository.saveContext(context, request, response); // set session in cookie
    }

    @Transactional
    public UserResponse getSession() {
        User user = SecurityUtils.getAuthenticatedUser();
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        return new UserResponse(user, authorities);
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        this.logoutHandler.logout(request, response, authentication);
    }
}
