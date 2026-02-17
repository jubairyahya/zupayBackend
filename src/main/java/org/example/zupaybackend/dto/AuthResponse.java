package org.example.zupaybackend.dto;

public class AuthResponse {
    private String message;
    private String token;
    private String uniqueUserId;
    private String qrCode;


    private String name;
    private boolean bankLinked;
    private int bankBalance;

    public AuthResponse(String message, String token, String uniqueUserId, String qrCode,
                        String name, boolean bankLinked, int bankBalance) {
        this.message = message;
        this.token = token;
        this.uniqueUserId = uniqueUserId;
        this.qrCode = qrCode;
        this.name = name;
        this.bankLinked = bankLinked;
        this.bankBalance = bankBalance;
    }

    public String getMessage() { return message; }
    public String getToken() { return token; }
    public String getUniqueUserId() { return uniqueUserId; }
    public String getQrCode() { return qrCode; }
    public String getName() { return name; }
    public boolean isBankLinked() { return bankLinked; }
    public int getBankBalance() { return bankBalance; }

}
