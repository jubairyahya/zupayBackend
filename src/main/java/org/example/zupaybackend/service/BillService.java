package org.example.zupaybackend.service;

import org.example.zupaybackend.model.*;
import org.example.zupaybackend.repository.BillRepository;
import org.example.zupaybackend.dto.TransactionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class BillService {

    private final BillRepository billRepository;
    private final TransactionService transactionService;

    public BillService(BillRepository billRepository,
                       TransactionService transactionService) {
        this.billRepository = billRepository;
        this.transactionService = transactionService;
    }

    public Bill fetchBill(String reference, BillType type,String providerName) {

        Bill bill = billRepository
                .findByReferenceNumberAndBillType(reference, type,providerName)
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        if (bill.isPaid()) {
            throw new RuntimeException("No outstanding bills");
        }

        bill.setDueDate(LocalDate.now().plusDays(7));

        return bill;
    }

    @Transactional
    public TransactionResponse payBill(String userId,
                                       String reference,
                                       BillType type,String providerName) {

        Bill bill = billRepository
                .findByReferenceNumberAndBillType(reference, type,providerName)
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        if (bill.isPaid()) {
            throw new RuntimeException("Already paid");
        }

        TransactionResponse response =
                transactionService.payBill(userId, bill, reference, type,providerName);

        bill.setPaid(true);

        return response;
    }
}