package com.example.simplepayment.transaction.domain;

public enum TransactionType {
    CHARGE("충전"),
    PAYMENT("결제");

    private final String text;

    TransactionType(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
