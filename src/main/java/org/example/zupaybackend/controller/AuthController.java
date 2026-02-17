package org.example.zupaybackend.controller;



import org.example.zupaybackend.dto.RegisterRequest;
import org.example.zupaybackend.dto.LoginRequest;
import org.example.zupaybackend.dto.AuthResponse;
import org.example.zupaybackend.model.User;
import org.example.zupaybackend.service.AuthService;
import org.example.zupaybackend.service.TokenBlacklist;
import org.example.zupaybackend.service.JwtService;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final TokenBlacklist tokenBlacklist;
    private final JwtService jwtService;

    public AuthController(AuthService authService, TokenBlacklist tokenBlacklist, JwtService jwtService) {
        this.authService = authService;
        this.tokenBlacklist = tokenBlacklist;
        this.jwtService = jwtService;
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
    @GetMapping("/profile")
    public ResponseEntity<AuthResponse> getProfile(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }

        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);

        User user = authService.getUserByUsername(username); // add this method in AuthService

        String qrBase64 = Base64.getEncoder().encodeToString(user.getQrCode());

        boolean bankLinked = false;
        int bankBalance = 0;

        AuthResponse response = new AuthResponse(
                "Profile fetched successfully",
                token,
                user.getUniqueUserId(),
                qrBase64,
                user.getName(),
                bankLinked,
                bankBalance
        );

        return ResponseEntity.ok(response);
    }
}