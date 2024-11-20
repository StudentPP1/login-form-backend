package github.studentpp1.advancedloginform.users.controller;

import github.studentpp1.advancedloginform.config.ApplicationProperties;
import github.studentpp1.advancedloginform.users.data.*;
import github.studentpp1.advancedloginform.users.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.HashMap;

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
    
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        userService.forgotPassword(request.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody UpdateUserPasswordRequest request) {
        userService.resetPassword(request);
        return ResponseEntity.ok().build();
    }

    // update first & last names at profile
    // PUT - for updating, POST - for creating
    @PutMapping ("/update-details")
    public ResponseEntity<UserResponse> update(@Valid @RequestBody UpdateUserRequest request) {
        UserResponse user = userService.update(request);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/password")
    public ResponseEntity<UserResponse> updatePassword(@Valid @RequestBody UpdateUserPasswordRequest request) {
        UserResponse user = userService.updatePassword(request);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/profile-picture") // for changing
    public ResponseEntity<UserResponse> updateProfilePicture(
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        UserResponse user = userService.updateProfilePicture(file);
        return ResponseEntity.ok(user);
    }
}
