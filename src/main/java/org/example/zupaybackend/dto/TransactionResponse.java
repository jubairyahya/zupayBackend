package org.example.zupaybackend.dto;

import org.example.zupaybackend.model.Transaction;

import java.time.LocalDateTime;

public class TransactionResponse {

    private String transactionId;
    private String senderUniqueId;
    private String receiverUniqueId;
    private Double amount;
    private String description;
    private String status;
    private LocalDateTime time;

    public TransactionResponse(Transaction tx) {
        this.transactionId = tx.getTransactionId();
        this.senderUniqueId = tx.getSender().getUniqueUserId();
        this.receiverUniqueId = tx.getReceiver().getUniqueUserId();
        this.amount = tx.getAmount();
        this.description = tx.getDescription();
        this.status = tx.getStatus().name();
        this.time = tx.getTransactionTime();
    }

    public String getTransactionId() { return transactionId; }
    public String getSenderUniqueId() { return senderUniqueId; }
    public String getReceiverUniqueId() { return receiverUniqueId; }
    public Double getAmount() { return amount; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public LocalDateTime getTime() { return time; }
}