package github.studentpp1.advancedloginform.auth.config;

import github.studentpp1.advancedloginform.auth.oauth2.OAuth2LoginSuccessHandler;
import github.studentpp1.advancedloginform.auth.service.UserDetailsServiceImpl;
import github.studentpp1.advancedloginform.config.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final ApplicationProperties applicationProperties;
    private final UserDetailsServiceImpl userDetailsService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable) // because we have 'same-site': strict
                .cors(config -> config.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(request -> request
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users/verify-email").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/reset-password").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/forgot-password").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new UsernamePasswordAuthenticationFilter(), LogoutFilter.class)
                .userDetailsService(userDetailsService)
                // add exception handling for 403 forbidden unauthorized
                // (for our api app => redirect to login page)
                .exceptionHandling(config -> config
                        .authenticationEntryPoint(
                                (request, response, authException) -> {
                                    String acceptHandler = request.getHeader(HttpHeaders.ACCEPT);
                                    // if client need json to response send 401
                                    if (acceptHandler != null && acceptHandler.contains("application/json")) {
                                        response.setStatus(401);
                                    }
                                    else {
                                        response.sendRedirect(applicationProperties.getLoginPageUrl());
                                    }
                                }
                        )
                )
                .oauth2Login(auth -> {
                    auth.loginPage(applicationProperties.getLoginPageUrl());
                    auth.successHandler(oAuth2LoginSuccessHandler);
                    auth.redirectionEndpoint(redirectionEndpointConfig ->
                            redirectionEndpointConfig.baseUri("/oauth2/callback/*"));
                    auth.authorizationEndpoint(authorizationEndpointConfig ->
                            authorizationEndpointConfig.baseUri("/oauth2/authorize"));
                })
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(applicationProperties.getAllowedOrigins());
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configurationSource.registerCorsConfiguration("/**", configuration);
        return configurationSource;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authenticationProvider);
    }
}
