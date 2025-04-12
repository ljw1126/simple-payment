package com.example.simplepayment.wallet.domain;

import com.example.simplepayment.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Table(name = "wallet")
@Entity
public class Wallet extends BaseTimeEntity {
    private final static BigDecimal BALANCE_LIMIT = new BigDecimal(100_000);

    @Id
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private BigDecimal balance;

    public Wallet(Long id, Long userId) {
        this(id, userId, BigDecimal.ZERO);
    }

    public void addBalance(BigDecimal amount) {
        BigDecimal newBalance = this.balance.add(amount);

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("잔액이 충분하지 않습니다");
        }

        if (BALANCE_LIMIT.compareTo(newBalance) < 0) {
            throw new IllegalStateException("한도를 초과했습니다");
        }

        this.balance = newBalance;
    }
}
