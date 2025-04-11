package com.example.simplepayment.transaction.domain;

import com.example.simplepayment.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
@Table(name = "transaction")
@Entity
public class Transaction extends BaseTimeEntity {

    @Id
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long walletId;

    @Column(nullable = false)
    @Embedded
    private TransactionTarget transactionTarget;

    @Column(nullable = false)
    private BigDecimal amount;

    public static Transaction createChargeTransaction(Long id, Long userId, Long walletId, Long orderId, BigDecimal amount) {
        return new Transaction(id, userId, walletId, TransactionTarget.charge(orderId), amount);
    }

    public static Transaction createPaymentTransaction(Long id, Long userId, Long walletId, Long courseId, BigDecimal amount) {
        return new Transaction(id, userId, walletId, TransactionTarget.payment(courseId), amount);
    }
}
