package github.studentpp1.advancedloginform.users.controller;

import github.studentpp1.advancedloginform.config.ApplicationProperties;
import github.studentpp1.advancedloginform.users.data.CreateUserRequest;
import github.studentpp1.advancedloginform.users.data.UserResponse;
import github.studentpp1.advancedloginform.users.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final ApplicationProperties applicationProperties;

    // register a new user, then verification email will be sent to the user
    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) throws Exception {
        UserResponse user = userService.create(request);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/verify-email")
    public RedirectView verifyEmail(@RequestParam String token) {
        userService.verifyEmail(token);
        return new RedirectView(applicationProperties.getLoginPageUrl());
    }
}
