package com.example.simplepayment.transaction.application;

import com.example.simplepayment.common.Snowflake;
import com.example.simplepayment.transaction.domain.Transaction;
import com.example.simplepayment.transaction.domain.TransactionRepository;
import com.example.simplepayment.transaction.domain.TransactionTarget;
import com.example.simplepayment.transaction.presentation.request.ChargeTransactionRequest;
import com.example.simplepayment.transaction.presentation.request.PaymentTransactionRequest;
import com.example.simplepayment.transaction.presentation.response.ChargeTransactionResponse;
import com.example.simplepayment.transaction.presentation.response.PaymentTransactionResponse;
import com.example.simplepayment.wallet.application.WalletService;
import com.example.simplepayment.wallet.presentation.request.AddBalanceWalletRequest;
import com.example.simplepayment.wallet.presentation.response.AddBalanceWalletResponse;
import com.example.simplepayment.wallet.presentation.response.SearchWalletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final Snowflake snowflake = new Snowflake();

    private final WalletService walletService;
    private final TransactionRepository transactionRepository;

    @Transactional
    public ChargeTransactionResponse charge(ChargeTransactionRequest request) {
        SearchWalletResponse wallet = walletService.findWalletByUserId(request.userId());

        boolean isPresentChargeTransaction = transactionRepository.findByTransactionTarget(TransactionTarget.charge(request.orderId())).isPresent();
        if (isPresentChargeTransaction) {
            throw new IllegalStateException("이미 충전된 거래입니다");
        }

        AddBalanceWalletResponse addBalanceWalletResponse = walletService.addBalance(new AddBalanceWalletRequest(wallet.id(), request.amount()));

        Transaction transaction = Transaction.createChargeTransaction(snowflake.nextId(), request.userId(), wallet.id(), request.orderId(), request.amount());
        transactionRepository.save(transaction);

        return new ChargeTransactionResponse(addBalanceWalletResponse.id(), addBalanceWalletResponse.balance());
    }

    @Transactional
    public PaymentTransactionResponse payment(PaymentTransactionRequest request) {
        boolean isPresentPaymentTransaction = transactionRepository.findByTransactionTarget(TransactionTarget.payment(request.courseId())).isPresent();
        if (isPresentPaymentTransaction) {
            throw new IllegalStateException("이미 결제된 강의입니다");
        }

        SearchWalletResponse wallet = walletService.findWalletByWalletId(request.walletId());
        AddBalanceWalletResponse addBalanceWalletResponse = walletService.addBalance(new AddBalanceWalletRequest(wallet.id(), request.amount().negate()));

        Transaction transaction = Transaction.createPaymentTransaction(snowflake.nextId(), wallet.userId(), wallet.id(), request.courseId(), request.amount());
        transactionRepository.save(transaction);

        return new PaymentTransactionResponse(addBalanceWalletResponse.id(), addBalanceWalletResponse.balance());
    }
}
