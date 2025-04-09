package com.example.simplepayment.controller.response;

import java.math.BigDecimal;

public record CreateWalletResponse(Long id, Long userId, BigDecimal balance) {
}
