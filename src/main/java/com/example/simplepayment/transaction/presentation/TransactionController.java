package com.example.simplepayment.transaction.presentation;

import com.example.simplepayment.transaction.application.TransactionService;
import com.example.simplepayment.transaction.presentation.request.ChargeTransactionRequest;
import com.example.simplepayment.transaction.presentation.request.PaymentTransactionRequest;
import com.example.simplepayment.transaction.presentation.response.ChargeTransactionResponse;
import com.example.simplepayment.transaction.presentation.response.PaymentTransactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/api/balance/charge")
    public ChargeTransactionResponse charge(@RequestBody ChargeTransactionRequest request) {
        return transactionService.charge(request);
    }

    @PostMapping("/api/balance/payment")
    public PaymentTransactionResponse payment(@RequestBody PaymentTransactionRequest request) {
        return transactionService.payment(request);
    }
}
