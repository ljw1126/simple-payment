package com.example.simplepayment.transaction.presentation.response;

import java.math.BigDecimal;

public record PaymentTransactionResponse(Long walletId, BigDecimal balance) {
}
