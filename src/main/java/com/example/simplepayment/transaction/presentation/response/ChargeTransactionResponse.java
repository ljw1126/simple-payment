package com.example.simplepayment.transaction.presentation.response;

import java.math.BigDecimal;

public record ChargeTransactionResponse(Long walletId, BigDecimal balance) {
}
