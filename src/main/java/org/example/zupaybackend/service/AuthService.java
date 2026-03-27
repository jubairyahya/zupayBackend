package org.example.zupaybackend.service;

import org.example.zupaybackend.dto.RegisterRequest;
import org.example.zupaybackend.dto.LoginRequest;
import org.example.zupaybackend.dto.AuthResponse;
import org.example.zupaybackend.model.User;
import org.example.zupaybackend.repository.UserRepository;

import com.google.zxing.*;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.util.regex.Pattern;
import java.util.Base64;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final SecureRandom random = new SecureRandom();

    private static final Pattern STRONG_PASSWORD =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$");

    public AuthService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;

    }
    // Fetch user
    public User getUserByUniqueId(String uniqueId) {
        return userRepository.findByUniqueUserId(uniqueId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + uniqueId));
    }

    public User register(RegisterRequest req) {


        if (!STRONG_PASSWORD.matcher(req.getPassword()).matches()) {
            throw new RuntimeException("Weak password. Use upper, lower, number, special char, min 8 chars.");
        }


        if (userRepository.existsByUsername(req.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        String uniqueUserId;
        do {
            uniqueUserId = generateUniqueUserId(req.getName());
        } while (userRepository.existsByUniqueUserId(uniqueUserId));

        //  Generate QR code for unique userId
        byte[] qrBytes = generateQrCode(uniqueUserId);
        String qrBase64 = Base64.getEncoder().encodeToString(qrBytes);

        // Save user
        User user = new User();
        user.setName(req.getName());
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setUniqueUserId(uniqueUserId);
        user.setQrCode(qrBase64);

        return userRepository.save(user);
    }
    //  Login
    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(user);


        String refreshToken = java.util.UUID.randomUUID().toString();
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        AuthResponse response = new AuthResponse("Login successful", token, user.getUniqueUserId(),
                user.getQrCode(), user.getName(), user.isBankLinked(), user.getBankBalance());
        response.setRefreshToken(refreshToken);
        return response;
    }

    //  Helper Methods

    private String generateUniqueUserId(String name) {
        String prefix = name.length() >= 3
                ? name.substring(0, 3).toLowerCase()
                : name.toLowerCase();

        int number = 1000 + random.nextInt(9000);
        return prefix + number;
    }

    private byte[] generateQrCode(String text) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(text, BarcodeFormat.QR_CODE, 250, 250);

            BufferedImage image = new BufferedImage(250, 250, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < 250; x++) {
                for (int y = 0; y < 250; y++) {
                    image.setRGB(x, y, matrix.get(x, y) ? 0x000000 : 0xFFFFFF);
                }
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, "png", out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("QR generation failed", e);
        }
    }
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }
    @Transactional
    public User linkBankAccount(String username, String accountHolderName, String accountNumber, String sortCode) {
        User user = getUserByUsername(username);

        if (user.isBankLinked()) {
            throw new RuntimeException("Bank already linked");
        }

        user.setAccountHolderName(accountHolderName);
        user.setAccountNumber(accountNumber);
        user.setSortCode(sortCode);
        user.setBankLinked(true);
        user.setBankBalance(1000.00); // initial balance
        return userRepository.save(user);


    }
    public User getUserByRefreshToken(String refreshToken) {
        return userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));
    }

}