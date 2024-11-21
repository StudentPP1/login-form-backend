package github.studentpp1.advancedloginform.auth.controller;

import github.studentpp1.advancedloginform.auth.data.LoginRequest;
import github.studentpp1.advancedloginform.auth.service.AuthService;
import github.studentpp1.advancedloginform.users.data.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(
            HttpServletRequest request,
            HttpServletResponse response,
            @Valid @RequestBody LoginRequest loginRequest) {
        // set session cookie in response
        authService.login(request, response, loginRequest);
        return ResponseEntity.ok().build();
    }

    // get info about current authenticated user
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getSession() {
        return ResponseEntity.ok(authService.getSession());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        return ResponseEntity.ok().build();
    }
}