package org.example.zupaybackend.dto;

public class AuthResponse {
    private String message;
    private String token;
    private String uniqueUserId;
    private String qrCode;
    private String name;
    private boolean bankLinked;
    private Double bankBalance;
    private String refreshToken;

    public AuthResponse(String message, String token, String uniqueUserId, String qrCode,
                        String name, boolean bankLinked, Double bankBalance) {
        this.message = message;
        this.token = token;
        this.uniqueUserId = uniqueUserId;
        this.qrCode = qrCode;
        this.name = name;
        this.bankLinked = bankLinked;
        this.bankBalance = bankBalance;
    }

    public String getMessage() { return message; }
    public String getUniqueUserId() { return uniqueUserId; }
    public String getQrCode() { return qrCode; }
    public String getName() { return name; }
    public boolean isBankLinked() { return bankLinked; }
    public Double getBankBalance() { return bankBalance; }

    public String getToken() { return token; }        // ✅ keep ONE
    public void setToken(String token) { this.token = token; }  // ✅ keep

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}