package org.example.zupaybackend.controller;

import org.example.zupaybackend.model.Bill;
import org.example.zupaybackend.model.BillType;
import org.example.zupaybackend.dto.PayBillRequest;
import org.example.zupaybackend.dto.TransactionResponse;
import org.example.zupaybackend.service.BillService;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/bills")
public class BillController {

    private final BillService billService;

    public BillController(BillService billService) {
        this.billService = billService;
    }
    @GetMapping("/fetch")
    public Bill fetchBill(@RequestParam String reference,
                          @RequestParam String type,@RequestParam String providerName) {

        BillType billType;
        try {
            billType = BillType.valueOf(type.toUpperCase()); // force uppercase
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid bill type: " + type);
        }

        return billService.fetchBill(reference, billType,providerName);
    }

    @PostMapping("/pay")
    public TransactionResponse payBill(@RequestBody PayBillRequest request) {

        String loggedInUserId = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        return billService.payBill(
                loggedInUserId, // use JWT principal, not request body
                request.getReference(),
                request.getType(),
                request.getProviderName()
        );
    }

}