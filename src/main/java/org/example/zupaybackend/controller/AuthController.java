package org.example.zupaybackend.controller;



import org.example.zupaybackend.dto.RegisterRequest;
import org.example.zupaybackend.dto.LoginRequest;
import org.example.zupaybackend.dto.AuthResponse;
import org.example.zupaybackend.model.User;
import org.example.zupaybackend.service.AuthService;
import org.example.zupaybackend.service.TokenBlacklist;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final TokenBlacklist tokenBlacklist;

    public AuthController(AuthService authService, TokenBlacklist tokenBlacklist) {
        this.authService = authService;
        this.tokenBlacklist = tokenBlacklist;
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        User user = authService.register(request);

        // convert qr bytes to base 64

        String qrBase64 = Base64.getEncoder().encodeToString(user.getQrCode());

        return ResponseEntity.ok(Map.of(
                "message", "Registered successfully!",
                "uniqueUserId", user.getUniqueUserId(),
                "qrCode", qrBase64
        ));
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest req) {
        AuthResponse resp = authService.login(req);
        return ResponseEntity.ok(resp);
    }
    @GetMapping("/test")
    public String testEndpoint() {
        return "JWT Filter Works!";
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenBlacklist.add(token);
        }
        return ResponseEntity.ok("Logged out successfully");
    }
}