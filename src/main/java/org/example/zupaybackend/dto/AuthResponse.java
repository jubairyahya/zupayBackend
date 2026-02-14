package org.example.zupaybackend.dto;

public class AuthResponse {
    private String message;
    private String token;
    private String uniqueUserId;
    private String qrCode;

    public AuthResponse(String message, String token, String uniqueUserId, String qrCode) {
        this.message = message;
        this.token = token;
        this.uniqueUserId = uniqueUserId;
        this.qrCode = qrCode;
    }

    public String getMessage() { return message; }
    public String getToken() { return token; }
    public String getUniqueUserId() { return uniqueUserId; }
    public String getQrCode() { return qrCode; }

}
