package com.example.simplepayment.wallet.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Wallet {
    private final static BigDecimal BALANCE_LIMIT = new BigDecimal(100_000);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private BigDecimal balance;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Wallet(Long userId) {
        this.userId = userId;
        this.balance = new BigDecimal(0);
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void addBalance(BigDecimal amount, LocalDateTime timestamp) {
        BigDecimal newBalance = this.balance.add(amount);

        if(newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("잔액이 충분하지 않습니다");
        }

        if(BALANCE_LIMIT.compareTo(newBalance) < 0) {
            throw new IllegalStateException("한도를 초과했습니다");
        }

        this.balance = newBalance;
        this.updatedAt = timestamp;
    }
}
