package me.axelfrache.questify.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.axelfrache.questify.dto.auth.AuthRequest;
import me.axelfrache.questify.dto.auth.AuthResponse;
import me.axelfrache.questify.dto.auth.RegisterRequest;
import me.axelfrache.questify.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
