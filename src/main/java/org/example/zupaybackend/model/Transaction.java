package org.example.zupaybackend.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true)
    private String transactionId; // unique receipt ID

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender; // null for system/bills

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver; // null for bills/merchants

    private Double amount;

    @Column(nullable=false)
    private LocalDateTime transactionTime;

    private String description; // e.g., "coffee", "electricity bill"

    @Enumerated(EnumType.STRING)
    private Status status; // SUCCESS, FAILED, PENDING

    public enum Status { SUCCESS, FAILED, PENDING }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }

    public User getReceiver() { return receiver; }
    public void setReceiver(User receiver) { this.receiver = receiver; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public LocalDateTime getTransactionTime() { return transactionTime; }
    public void setTransactionTime(LocalDateTime transactionTime) { this.transactionTime = transactionTime; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}
