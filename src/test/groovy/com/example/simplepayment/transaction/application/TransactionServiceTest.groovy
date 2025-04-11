package com.example.simplepayment.transaction.application

import com.example.simplepayment.transaction.domain.Transaction
import com.example.simplepayment.transaction.domain.TransactionRepository
import com.example.simplepayment.transaction.domain.TransactionTarget
import com.example.simplepayment.transaction.presentation.request.ChargeTransactionRequest
import com.example.simplepayment.transaction.presentation.request.PaymentTransactionRequest
import com.example.simplepayment.wallet.application.WalletService
import com.example.simplepayment.wallet.presentation.response.AddBalanceWalletResponse
import com.example.simplepayment.wallet.presentation.response.SearchWalletResponse
import spock.lang.Specification

import java.time.LocalDateTime

class TransactionServiceTest extends Specification {
    TransactionService transactionService
    WalletService walletService = Mock()
    TransactionRepository transactionRepository = Mock()

    void setup() {
        transactionService = new TransactionService(walletService, transactionRepository)
    }

    def "지갑이 없다면 충전이 실패한다" () {
        given:
        def userId = 1L
        def chargeTransactionRequest = new ChargeTransactionRequest(userId, 99L, BigDecimal.TEN);

        walletService.findWalletByUserId(userId) >> { throw new IllegalStateException("사용자의 지갑이 존재하지 않습니다") }

        when:
        transactionService.charge(chargeTransactionRequest)

        then:
        def ex = thrown(IllegalStateException)
        ex != null
    }

    def "충전 내역이 있다면 충전이 실패한다" () {
        given:
        def userId = 1L
        walletService.findWalletByUserId(userId) >> new SearchWalletResponse(1L, userId, BigDecimal.ZERO, LocalDateTime.now(), LocalDateTime.now())

        def chargeTransactionRequest = new ChargeTransactionRequest(userId, 99L, BigDecimal.TEN);
        transactionRepository.findByTransactionTarget(_) >> Optional.of(Transaction.createChargeTransaction(1L, userId, 1L, 1L, BigDecimal.TEN))

        when:
        transactionService.charge(chargeTransactionRequest)

        then:
        def ex = thrown(IllegalStateException)
        ex != null
    }

    def "충전이 성공한다" () {
        given:
        def request = new ChargeTransactionRequest(1L, 99L, BigDecimal.TEN)
        def searchWalletResponse = new SearchWalletResponse(1L, request.userId(), BigDecimal.ZERO, LocalDateTime.now(), LocalDateTime.now())
        walletService.findWalletByUserId(request.userId()) >> searchWalletResponse

        transactionRepository.findByTransactionTarget(_) >> Optional.empty()

        def addBalanceWalletResponse = new AddBalanceWalletResponse(
                searchWalletResponse.id(),
                searchWalletResponse.userId(),
                searchWalletResponse.balance().add(request.amount()),
                searchWalletResponse.createdAt(),
                searchWalletResponse.updatedAt()
        )

        walletService.addBalance(_) >> addBalanceWalletResponse

        when:
        def result = transactionService.charge(request)

        then:
        1 * transactionRepository.save(_)
        result.balance() == BigDecimal.TEN
    }

    def "이미 결제된 강의이면 결제 실패한다"() {
        given:
        def request = new PaymentTransactionRequest(1L, 99L, BigDecimal.TEN)
        def transactionTarget = TransactionTarget.payment(request.courseId())
        transactionRepository.findByTransactionTarget(transactionTarget) >> Optional.of(Transaction.createPaymentTransaction(1L, 1L, 1L, request.courseId(), request.amount()))

        when:
        transactionService.payment(request)

        then:
        def ex = thrown(IllegalStateException)
        ex != null
    }

    def "지갑이 없으면 결제 실패한다"() {
        given:
        def request = new PaymentTransactionRequest(1L, 99L, BigDecimal.TEN)

        transactionRepository.findByTransactionTarget(_) >> Optional.empty()

        walletService.findWalletByWalletId(request.walletId()) >> { throw new IllegalStateException("사용자의 지갑이 존재하지 않습니다") }

        when:
        transactionService.payment(request)

        then:
        def ex = thrown(IllegalStateException)
        ex != null
    }

    def "강의 결제 성공한다"() {
        given:
        def request = new PaymentTransactionRequest(1L, 99L, BigDecimal.TEN)

        transactionRepository.findByTransactionTarget(_) >> Optional.empty()

        def searchWalletResponse = new SearchWalletResponse(request.walletId(), 1L, BigDecimal.valueOf(1000), LocalDateTime.now(), LocalDateTime.now())
        walletService.findWalletByWalletId(request.walletId()) >> searchWalletResponse

        def addBalanceWalletResponse = new AddBalanceWalletResponse(
                searchWalletResponse.id(),
                searchWalletResponse.userId(),
                searchWalletResponse.balance().add(request.amount().negate()),
                searchWalletResponse.createdAt(),
                searchWalletResponse.updatedAt()
        )

        walletService.addBalance(_) >> addBalanceWalletResponse

        when:
        def result = transactionService.payment(request)

        then:
        1 * transactionRepository.save(_)
        result.balance() == BigDecimal.valueOf(990)
    }
}
