package org.example.zupaybackend.dto;

import org.example.zupaybackend.model.Transaction;

import java.time.LocalDateTime;

public class TransactionResponse {

    private String transactionId;
    private String senderUniqueId;
    private String receiverUniqueId;
    private String senderName;
    private String receiverName;
    private Double amount;
    private String description;
    private String status;
    private LocalDateTime time;
    private String transactionType;
    private String reference;
    private String type;


    public TransactionResponse(Transaction tx) {
        this.transactionId = tx.getTransactionId();
        this.senderUniqueId = tx.getSender().getUniqueUserId();
        this.receiverUniqueId = tx.getReceiver() != null ? tx.getReceiver().getUniqueUserId() : null;
        this.senderName = tx.getSenderName();
        this.receiverName = tx.getReceiverName();
        this.amount = tx.getAmount();
        this.description = tx.getDescription();
        this.status = tx.getStatus().name();
        this.time = tx.getTransactionTime();
        this.transactionType = tx.getTransactionType();
        this.reference = tx.getBillReference();
        this.type = tx.getBillReference() != null ? tx.getDescription().replace("Bill Payment - ", "") : "P2P";
    }

    public String getTransactionId() { return transactionId; }
    public String getSenderUniqueId() { return senderUniqueId; }
    public String getReceiverUniqueId() { return receiverUniqueId; }
    public String getSenderName() { return senderName; }
    public String getReceiverName() { return receiverName; }
    public Double getAmount() { return amount; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public LocalDateTime getTime() { return time; }
    public String getTransactionType() { return transactionType; }
    public String getReference() { return reference; }
    public String getType() { return type; }
}