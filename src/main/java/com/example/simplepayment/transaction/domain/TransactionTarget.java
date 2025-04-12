package com.example.simplepayment.transaction.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransactionTarget {

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(nullable = false)
    private Long targetId;

    public static TransactionTarget charge(Long orderId) {
        return new TransactionTarget(TransactionType.CHARGE, orderId);
    }

    public static TransactionTarget payment(Long courseId) {
        return new TransactionTarget(TransactionType.PAYMENT, courseId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionTarget that = (TransactionTarget) o;
        return transactionType == that.transactionType && Objects.equals(targetId, that.targetId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionType, targetId);
    }
}
