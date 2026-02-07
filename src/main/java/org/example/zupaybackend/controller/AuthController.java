package org.example.zupaybackend.controller;



import org.example.zupaybackend.dto.RegisterRequest;
import org.example.zupaybackend.model.User;
import org.example.zupaybackend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
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
}