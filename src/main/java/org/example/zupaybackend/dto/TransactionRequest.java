package org.example.zupaybackend.dto;

public class TransactionRequest {

    private String receiverUniqueId;
    private Double amount;
    private String description;

    public String getReceiverUniqueId() {
        return receiverUniqueId;
    }

    public void setReceiverUniqueId(String receiverUniqueId) {
        this.receiverUniqueId = receiverUniqueId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}