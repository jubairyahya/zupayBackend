package org.example.zupaybackend.controller;

import org.example.zupaybackend.dto.TransactionRequest;
import org.example.zupaybackend.dto.TransactionResponse;
import org.example.zupaybackend.model.User;
import org.example.zupaybackend.service.AuthService;
import org.example.zupaybackend.service.JwtService;
import org.example.zupaybackend.service.TransactionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public TransactionResponse sendMoney(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody TransactionRequest request) {

        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);
        User sender = authService.getUserByUsername(username);

        return transactionService.sendMoney(sender.getUniqueUserId(), request);
    }

    @GetMapping("/history")
    public List<TransactionResponse> getHistory(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);
        User user = authService.getUserByUsername(username);

        return transactionService.getHistory(user.getUniqueUserId());
    }
}