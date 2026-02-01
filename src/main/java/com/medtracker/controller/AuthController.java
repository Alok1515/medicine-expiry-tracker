package com.medtracker.controller;

import com.medtracker.dto.LoginDTO;
import com.medtracker.dto.RegisterDTO;
import com.medtracker.model.User;
import com.medtracker.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterDTO dto) {
        User user = authService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Registration successful",
                "email", user.getEmail()
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @PutMapping("/preferences")
    public ResponseEntity<Map<String, Boolean>> updatePreferences(@RequestBody Map<String, Boolean> body,
                                                                  Authentication auth) {
        String userId = (String) auth.getCredentials();
        boolean emailNotifications = body.getOrDefault("emailNotifications", true);
        User user = authService.updateNotificationPreference(userId, emailNotifications);
        return ResponseEntity.ok(Map.of("emailNotifications", user.isEmailNotifications()));
    }
}
