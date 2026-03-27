package org.example.zupaybackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable=false)
    private String password;

    @Column(unique = true, nullable = false)
    private String uniqueUserId;

    @Column(columnDefinition = "TEXT")
    private String qrCode;

    @Column(nullable = false)
    private boolean bankLinked = false;

    @Column(nullable = false)
    private Double bankBalance = 0.0;

    private String accountHolderName;
    private String sortCode;
    private String accountNumber;

    @Column(name = "refresh_token")
    private String refreshToken;

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUniqueUserId() {
        return uniqueUserId;
    }

    public void setUniqueUserId(String uniqueUserId) {
        this.uniqueUserId = uniqueUserId;
    }

    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }

    public boolean isBankLinked() {
        return bankLinked;
    }
    public void setBankLinked(boolean bankLinked) {this.bankLinked = bankLinked;
    }
    public Double getBankBalance() {
        return bankBalance;
    }
    public void setBankBalance(Double bankBalance) {this.bankBalance = bankBalance;
    }
    public String getAccountHolderName() { return accountHolderName; }
    public void setAccountHolderName(String accountHolderName) { this.accountHolderName = accountHolderName; }

    public String getSortCode() { return sortCode; }
    public void setSortCode(String sortCode) { this.sortCode = sortCode; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}