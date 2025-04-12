package com.example.simplepayment.wallet.presentation.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AddBalanceWalletResponse(Long id, Long userId, BigDecimal balance, LocalDateTime createdAt, LocalDateTime updatedAt) {
}
