package com.example.simplepayment.wallet.presentation.response;

import java.math.BigDecimal;

public record CreateWalletResponse(Long id, Long userId, BigDecimal balance) {
}
