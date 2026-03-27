package org.example.zupaybackend.service;

import org.example.zupaybackend.dto.TransactionRequest;
import org.example.zupaybackend.dto.TransactionResponse;
import org.example.zupaybackend.model.Transaction;
import org.example.zupaybackend.model.User;
import org.example.zupaybackend.model.Bill;
import org.example.zupaybackend.model.BillType;
import org.example.zupaybackend.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AuthService authService;

    public TransactionService(TransactionRepository transactionRepository,
                              AuthService authService) {
        this.transactionRepository = transactionRepository;
        this.authService = authService;
    }

    @Transactional
    public TransactionResponse sendMoney(String senderUniqueId,
                                         TransactionRequest request) {

        User sender = authService.getUserByUniqueId(senderUniqueId);
        User receiver = authService.getUserByUniqueId(request.getReceiverUniqueId());

        if (sender.getBankBalance() < request.getAmount()) {
            throw new RuntimeException("Insufficient balance");
        }

        // update balances
        sender.setBankBalance(sender.getBankBalance() - request.getAmount());
        receiver.setBankBalance(receiver.getBankBalance() + request.getAmount());

        // create transaction
        Transaction tx = new Transaction();
        tx.setTransactionId(UUID.randomUUID().toString());
        tx.setSender(sender);
        tx.setReceiver(receiver);
        tx.setSenderName(sender.getName());
        tx.setReceiverName(receiver.getName());
        tx.setAmount(request.getAmount());
        tx.setDescription(request.getDescription());
        tx.setTransactionTime(LocalDateTime.now());
        tx.setStatus(Transaction.Status.SUCCESS);

        transactionRepository.save(tx);

        return new TransactionResponse(tx);
    }

    public List<TransactionResponse> getHistory(String userUniqueId) {
        User user = authService.getUserByUniqueId(userUniqueId);

        return transactionRepository
                .findBySenderOrReceiverOrderByTransactionTimeDesc(user, user)
                .stream()
                .map(TransactionResponse::new)
                .toList();
    }
    @Transactional
    public TransactionResponse payBill(String userUniqueId,
                                       Bill bill,
                                       String reference,
                                       BillType type,
                                       String providerName) {

        User user = authService.getUserByUsername(userUniqueId);

        if (user.getBankBalance() < bill.getAmount()) {
            throw new RuntimeException("Insufficient balance");
        }

        user.setBankBalance(user.getBankBalance() - bill.getAmount());

        Transaction tx = new Transaction();
        tx.setTransactionId(UUID.randomUUID().toString());
        tx.setSender(user);
        tx.setReceiver(null);

        tx.setSenderName(user.getName());
        tx.setReceiverName(providerName);

        tx.setAmount(bill.getAmount());
        tx.setDescription("Bill Payment - " + type.name());
        tx.setTransactionTime(LocalDateTime.now());
        tx.setStatus(Transaction.Status.SUCCESS);

        tx.setTransactionType("BILL_PAYMENT");
        tx.setBillReference(reference);

        transactionRepository.save(tx);

        return new TransactionResponse(tx);
    }
}