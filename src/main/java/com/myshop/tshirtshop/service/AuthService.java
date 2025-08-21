package com.myshop.tshirtshop.service;

import com.myshop.tshirtshop.dto.AuthResponse;
import com.myshop.tshirtshop.dto.LoginRequest;
import com.myshop.tshirtshop.dto.RegisterRequest;
import com.myshop.tshirtshop.exception.InvalidCredentialsException;
import com.myshop.tshirtshop.exception.UserAlreadyExistsException;
import com.myshop.tshirtshop.model.User;
import com.myshop.tshirtshop.repository.UserRepository;
import com.myshop.tshirtshop.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public void register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed: Email {} already in use", request.getEmail());
            throw new UserAlreadyExistsException("Email already in use");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Registration failed: Username {} already in use", request.getUsername());
            throw new UserAlreadyExistsException("Username already in use");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Collections.singleton("ROLE_USER"))
                .createdAt(LocalDateTime.now()) // Устанавливаем createdAt вручную (или через аудит)
                .build();

        userRepository.save(user);
        log.info("User registered successfully: {}", request.getEmail());
    }

    public AuthResponse login(LoginRequest request) {
        log.debug("Attempting login for email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("Login failed: User with email {} not found", request.getEmail());
                    return new InvalidCredentialsException("User not found");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Login failed: Invalid password for email {}", request.getEmail());
            throw new InvalidCredentialsException("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRoles());
        log.info("User logged in successfully: {}", request.getEmail());
        return new AuthResponse(token);
    }
}