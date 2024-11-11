package github.studentpp1.advancedloginform.users.controller;

import github.studentpp1.advancedloginform.users.data.CreateUserRequest;
import github.studentpp1.advancedloginform.users.data.UserResponse;
import github.studentpp1.advancedloginform.users.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // register a new user, then verification email will be sent to the user
    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        UserResponse user = userService.create(request);
        return ResponseEntity.ok(user);
    }
}
