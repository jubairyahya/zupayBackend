package org.example.zupaybackend.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.example.zupaybackend.dto.TransactionRequest;
import org.example.zupaybackend.dto.TransactionResponse;
import org.example.zupaybackend.model.User;
import org.example.zupaybackend.service.AuthService;
import org.example.zupaybackend.service.JwtService;
import org.example.zupaybackend.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

    private final TransactionService transactionService;
    private final JwtService jwtService;
    private final AuthService authService;

    public TransactionController(TransactionService transactionService,
                                 JwtService jwtService,
                                 AuthService authService) {
        this.transactionService = transactionService;
        this.jwtService = jwtService;
        this.authService = authService;
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendMoney(
            HttpServletRequest request,
            @RequestBody TransactionRequest transactionRequest) {

        String token = getTokenFromCookie(request);
        if (token == null) return ResponseEntity.status(401)
                .body(Map.of("message", "Unauthorised"));

        String username = jwtService.extractUsername(token);
        User sender = authService.getUserByUsername(username);

        return ResponseEntity.ok(
                transactionService.sendMoney(sender.getUniqueUserId(), transactionRequest)
        );
    }

    @GetMapping("/history")
    public ResponseEntity<?> getHistory(HttpServletRequest request) {

        String token = getTokenFromCookie(request);
        if (token == null) return ResponseEntity.status(401)
                .body(Map.of("message", "Unauthorised"));

        String username = jwtService.extractUsername(token);
        User user = authService.getUserByUsername(username);

        return ResponseEntity.ok(
                transactionService.getHistory(user.getUniqueUserId())
        );
    }


    private String getTokenFromCookie(HttpServletRequest request) {
        //  Try cookie (web)
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("zupay_access".equals(c.getName())) return c.getValue();
            }
        }
        //  Authorization header (mobile)
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}