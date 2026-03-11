package org.example.zupaybackend.controller;

import org.example.zupaybackend.dto.*;
import org.example.zupaybackend.model.User;
import org.example.zupaybackend.service.AuthService;
import org.example.zupaybackend.service.TokenBlacklist;
import org.example.zupaybackend.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        try {
            User user = authService.register(request);
            return ResponseEntity.ok(Map.of(
                    "message", "Registered successfully!",
                    "uniqueUserId", user.getUniqueUserId(),
                    "qrCode", user.getQrCode()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            AuthResponse resp = authService.login(req);
            return ResponseEntity.ok(resp);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
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
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }
        try {
            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);
            User user = authService.getUserByUsername(username);
            AuthResponse response = new AuthResponse(
                    "Profile fetched successfully",
                    token,
                    user.getUniqueUserId(),
                    user.getQrCode(),
                    user.getName(),
                    user.isBankLinked(),
                    user.getBankBalance()
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/link-bank")
    public ResponseEntity<?> linkBank(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody BankLinkRequest request) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Missing token");
        }
        try {
            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);
            User user = authService.linkBankAccount(
                    username,
                    request.getAccountHolderName(),
                    request.getAccountNumber(),
                    request.getSortCode()
            );
            return ResponseEntity.ok(Map.of(
                    "message", "Bank linked successfully",
                    "bankLinked", user.isBankLinked(),
                    "bankBalance", user.getBankBalance()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
    @GetMapping("/user/{uniqueId}")
    public ResponseEntity<?> getUserByUniqueId(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String uniqueId) {
        try {
            User user = authService.getUserByUniqueId(uniqueId);
            return ResponseEntity.ok(Map.of(
                    "uniqueUserId", user.getUniqueUserId(),
                    "name", user.getName()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> body) {
        try {
            String refreshToken = body.get("refreshToken");
            User user = authService.getUserByRefreshToken(refreshToken);
            if (user == null) throw new RuntimeException("Invalid refresh token");

            String newJwt = jwtService.generateToken(user);
            return ResponseEntity.ok(Map.of(
                    "token", newJwt,
                    "message", "Token refreshed"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}