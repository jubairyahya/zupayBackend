package org.example.zupaybackend.controller;

import org.example.zupaybackend.dto.*;
import org.example.zupaybackend.model.User;
import org.example.zupaybackend.service.AuthService;
import org.example.zupaybackend.service.TokenBlacklist;
import org.example.zupaybackend.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<?> login(
            @RequestBody LoginRequest req,
            @RequestHeader(value = "X-Client", required = false, defaultValue = "web") String client,
            HttpServletResponse response) {
        try {
            AuthResponse resp = authService.login(req);

            if (!"mobile".equals(client)) {

                response.setHeader("Set-Cookie",
                        "zupay_access=" + resp.getToken()
                                + "; HttpOnly"
                                + "; Path=/"
                                + "; Max-Age=3600"
                                + "; SameSite=Strict"
                );
                resp.setToken(null);
            }
            // Mobile
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
    public ResponseEntity<String> logout(
            HttpServletRequest request,
            HttpServletResponse response) {

        //  Blacklist the token
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("zupay_access".equals(c.getName())) {
                    tokenBlacklist.add(c.getValue());
                }
            }
        }

        //  Clear the cookie on logout
        Cookie clear = new Cookie("zupay_access", "");
        clear.setHttpOnly(true);
        clear.setPath("/");
        clear.setMaxAge(0); // delete it
        response.setHeader("Set-Cookie",
                "zupay_access="
                        + "; HttpOnly"
                        + "; Path=/"
                        + "; Max-Age=0"
                        + "; SameSite=Strict"
        );

        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        //  Read token
        String token = getTokenFromCookie(request);
        if (token == null) return ResponseEntity.status(401).build();

        try {
            String username = jwtService.extractUsername(token);
            User user = authService.getUserByUsername(username);
            AuthResponse response = new AuthResponse(
                    "Profile fetched successfully",
                    null, // never send token in body
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
            HttpServletRequest request,
            @RequestBody BankLinkRequest bankRequest) {

        String token = getTokenFromCookie(request);
        if (token == null) return ResponseEntity.status(401).body("Missing token");

        try {
            String username = jwtService.extractUsername(token);
            User user = authService.linkBankAccount(
                    username,
                    bankRequest.getAccountHolderName(),
                    bankRequest.getAccountNumber(),
                    bankRequest.getSortCode()
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
    public ResponseEntity<?> getUserByUniqueId(@PathVariable String uniqueId) {
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
    public ResponseEntity<?> refreshToken(
            @RequestBody Map<String, String> body,
            HttpServletResponse response) {
        try {
            String refreshToken = body.get("refreshToken");
            User user = authService.getUserByRefreshToken(refreshToken);
            if (user == null) throw new RuntimeException("Invalid refresh token");

            String newJwt = jwtService.generateToken(user);

            //  Issue new access token as HttpOnly cookie
            Cookie cookie = new Cookie("zupay_access", newJwt);
            cookie.setHttpOnly(true);
            cookie.setSecure(false); // true in production
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60);
            response.setHeader("Set-Cookie",
                    "zupay_access=" + newJwt
                            + "; HttpOnly"
                            + "; Path=/"
                            + "; Max-Age=3600"
                            + "; SameSite=Strict"
            );

            return ResponseEntity.ok(Map.of("message", "Token refreshed"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }


    private String getTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("zupay_access".equals(c.getName())) return c.getValue();
            }
        }
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

}