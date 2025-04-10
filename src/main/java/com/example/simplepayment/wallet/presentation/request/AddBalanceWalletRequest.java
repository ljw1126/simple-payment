package com.example.simplepayment.wallet.presentation.request;

import java.math.BigDecimal;

public record AddBalanceWalletRequest(Long walletId, BigDecimal amount) {
}
