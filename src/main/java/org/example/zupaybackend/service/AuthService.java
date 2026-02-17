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

    public User register(RegisterRequest req) {

        // Strong password validation
        if (!STRONG_PASSWORD.matcher(req.getPassword()).matches()) {
            throw new RuntimeException("Weak password. Use upper, lower, number, special char, min 8 chars.");
        }

        //  Username check
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        // Generate unique userId like joh5428
        String uniqueUserId;
        do {
            uniqueUserId = generateUniqueUserId(req.getName());
        } while (userRepository.existsByUniqueUserId(uniqueUserId));

        //  Generate QR code for unique userId
        byte[] qrCode = generateQrCode(uniqueUserId);

        // 5Save user
        User user = new User();
        user.setName(req.getName());
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setUniqueUserId(uniqueUserId);
        user.setQrCode(qrCode);

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
        String qrCodeBase64 = Base64.getEncoder().encodeToString(user.getQrCode());

        boolean bankLinked=false;
        int bankBalance=0;

        return new AuthResponse("Login successful", token, user.getUniqueUserId(),
                qrCodeBase64,user.getName(),bankLinked,bankBalance);
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
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

}