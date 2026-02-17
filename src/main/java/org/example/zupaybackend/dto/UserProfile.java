package org.example.zupaybackend.dto;

public class UserProfile {
    private String name;
    private String username;
    private String uniqueUserId;
    private String qrCode;
    private boolean bankLinked;
    private int bankBalance;

// Constructors
public UserProfile() {}

public UserProfile(String name, String username, String uniqueUserId, String qrCode,
                   boolean bankLinked, int bankBalance) {
    this.name = name;
    this.username = username;
    this.uniqueUserId = uniqueUserId;
    this.qrCode = qrCode;
    this.bankLinked = bankLinked;
    this.bankBalance = bankBalance;
}

// Getters and setters
public String getName() { return name; }
public void setName(String name) { this.name = name; }

public String getUsername() { return username; }
public void setUsername(String username) { this.username = username; }

public String getUniqueUserId() { return uniqueUserId; }
public void setUniqueUserId(String uniqueUserId) { this.uniqueUserId = uniqueUserId; }

public String getQrCode() { return qrCode; }
public void setQrCode(String qrCode) { this.qrCode = qrCode; }

public boolean isBankLinked() { return bankLinked; }
public void setBankLinked(boolean bankLinked) { this.bankLinked = bankLinked; }

public int getBankBalance() { return bankBalance; }
public void setBankBalance(int bankBalance) { this.bankBalance = bankBalance; }
}