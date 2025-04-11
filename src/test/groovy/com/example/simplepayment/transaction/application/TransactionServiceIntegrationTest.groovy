package com.example.simplepayment.transaction.application

import com.example.simplepayment.transaction.domain.Transaction
import com.example.simplepayment.transaction.domain.TransactionRepository
import com.example.simplepayment.transaction.presentation.request.ChargeTransactionRequest
import com.example.simplepayment.transaction.presentation.request.PaymentTransactionRequest
import com.example.simplepayment.wallet.application.WalletService
import com.example.simplepayment.wallet.domain.Wallet
import com.example.simplepayment.wallet.domain.WalletRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

import java.time.LocalDateTime

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TransactionServiceIntegrationTest extends Specification{

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private WalletRepository walletRepository

    def "지갑이 없다면 충전이 실패한다" () {
        given:
        def userId = 1L
        def request = new ChargeTransactionRequest(userId, 99L, BigDecimal.TEN)

        when:
        transactionService.charge(request)

        then:
        def ex = thrown(IllegalStateException)
        ex != null
    }

    def "충전 내역이 있다면 충전이 실패한다" () {
        given:
        def userId = 1L
        def orderId = 99L

        def wallet = walletRepository.save(new Wallet(1L))
        transactionRepository.save(Transaction.createChargeTransaction(1L, userId, wallet.getId(), orderId, BigDecimal.TEN))

        def request = new ChargeTransactionRequest(userId, orderId, BigDecimal.TEN)

        when:
        transactionService.charge(request)

        then:
        def ex = thrown(IllegalStateException)
        ex != null
    }

    def "충전이 성공한다" () {
        given:
        def userId = 1L
        def orderId = 99L
        def wallet = walletRepository.save(new Wallet(1L))

        def request = new ChargeTransactionRequest(userId, 99L, BigDecimal.TEN)

        when:
        def result = transactionService.charge(request)

        then:
        result.walletId() == wallet.getId()
        result.balance() == BigDecimal.TEN
    }

    def "이미 결제된 강의이면 결제 실패한다" () {
        given:
        def walletId = 1L
        def courseId = 99L
        def amount = BigDecimal.valueOf(10000)

        transactionRepository.save(Transaction.createPaymentTransaction(1L, 1L, walletId, courseId, amount))

        def request = new PaymentTransactionRequest(walletId, courseId, amount)

        when:
        transactionService.payment(request)

        then:
        def ex = thrown(IllegalStateException)
        ex != null
    }

    def "지갑이 없으면 결제 실패한다" () {
        given:
        def walletId = 1L
        def courseId = 99L
        def amount = BigDecimal.valueOf(10000)

        def request = new PaymentTransactionRequest(walletId, courseId, amount)

        when:
        transactionService.payment(request)

        then:
        def ex = thrown(IllegalStateException)
        ex != null
    }

    def "강의 결제시 금액이 부족하면 실패한다" () {
        given:
        def courseId = 99L
        def amount = BigDecimal.valueOf(10000)

        def wallet = walletRepository.save(new Wallet(1L))
        def request = new PaymentTransactionRequest(wallet.getId(), courseId, amount)

        when:
        transactionService.payment(request)

        then:
        def ex = thrown(IllegalStateException)
        ex != null
        ex.getMessage() == "잔액이 충분하지 않습니다"
    }

    def "강의 결제 성공한다" () {
        def courseId = 99L
        def amount = BigDecimal.valueOf(10000)

        def wallet = walletRepository.save(new Wallet(null, 1L, BigDecimal.valueOf(10001), LocalDateTime.now(), LocalDateTime.now()))
        def request = new PaymentTransactionRequest(wallet.getId(), courseId, amount)

        when:
        def result = transactionService.payment(request)

        then:
        result.walletId() == wallet.getId()
        result.balance() == BigDecimal.valueOf(1)
    }
}
