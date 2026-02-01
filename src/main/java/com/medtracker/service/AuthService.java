package com.medtracker.service;

import com.medtracker.dto.LoginDTO;
import com.medtracker.dto.RegisterDTO;
import com.medtracker.model.User;
import com.medtracker.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public User register(RegisterDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        return userRepository.save(user);
    }

    public Map<String, Object> login(LoginDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid password");
        }
        String token = jwtService.generateToken(user.getEmail(), user.getId(), user.getName());
        return Map.of(
                "token", token,
                "userId", user.getId(),
                "name", user.getName(),
                "email", user.getEmail()
        );
    }

    public User updateNotificationPreference(String userId, boolean emailNotifications) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEmailNotifications(emailNotifications);
        return userRepository.save(user);
    }
}
