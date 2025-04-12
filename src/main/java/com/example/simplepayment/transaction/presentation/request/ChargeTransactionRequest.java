package com.example.simplepayment.transaction.presentation.request;

import java.math.BigDecimal;

public record ChargeTransactionRequest(Long userId, Long orderId, BigDecimal amount) {
}
