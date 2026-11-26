package com.commutertea.user;

import com.commutertea.user.dto.BackgroundResponse;
import com.commutertea.user.dto.LoginRequest;
import com.commutertea.user.dto.SaveBackgroundRequest;
import com.commutertea.user.dto.RegisterRequest;
import com.commutertea.user.dto.UpdateProfileRequest;
import com.commutertea.user.dto.UpdateTransportTypeRequest;
import com.commutertea.user.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(request));
    }

    @PostMapping("/login")
    public UserResponse login(@Valid @RequestBody LoginRequest request) {
        return userService.login(request);
    }

    @GetMapping("/me")
    public UserResponse me(@RequestAttribute("currentUser") User user) {
        return UserResponse.from(user);
    }

    @PostMapping("/logout")
    public void logout(@RequestAttribute("currentUser") User user) {
        userService.logout(user);
    }

    @PutMapping("/transport-type")
    public UserResponse updateTransportType(
            @RequestAttribute("currentUser") User user,
            @Valid @RequestBody UpdateTransportTypeRequest request
    ) {
        return userService.updateTransportType(user, request.getTransportType());
    }

    @PutMapping("/profile")
    public UserResponse updateProfile(
            @RequestAttribute("currentUser") User user,
            @RequestBody UpdateProfileRequest request
    ) {
        return userService.updateProfile(user, request);
    }

    @GetMapping("/backgrounds")
    public BackgroundResponse listBackgrounds(@RequestAttribute("currentUser") User user) {
        return userService.listBackgrounds(user);
    }

    @PostMapping("/backgrounds")
    public BackgroundResponse saveBackground(
            @RequestAttribute("currentUser") User user,
            @RequestBody SaveBackgroundRequest request
    ) {
        return userService.saveBackground(user, request.url());
    }

    @PutMapping("/backgrounds/{id}")
    public BackgroundResponse selectBackground(
            @RequestAttribute("currentUser") User user,
            @PathVariable Long id
    ) {
        return userService.selectBackground(user, id);
    }

    @DeleteMapping("/backgrounds")
    public BackgroundResponse clearBackground(@RequestAttribute("currentUser") User user) {
        return userService.clearBackground(user);
    }
}
