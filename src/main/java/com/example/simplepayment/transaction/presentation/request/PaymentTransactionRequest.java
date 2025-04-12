package com.example.simplepayment.transaction.presentation.request;

import java.math.BigDecimal;

public record PaymentTransactionRequest(Long walletId, Long courseId, BigDecimal amount) {
}
