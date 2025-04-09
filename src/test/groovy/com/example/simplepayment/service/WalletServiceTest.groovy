package com.example.simplepayment.service

import com.example.simplepayment.controller.request.AddBalanceWalletRequest
import com.example.simplepayment.controller.request.CreateWalletRequest
import com.example.simplepayment.domain.Wallet
import com.example.simplepayment.domain.WalletRepository
import spock.lang.Specification

class WalletServiceTest extends Specification {
    WalletService walletService
    WalletRepository walletRepository = Mock()

    void setup() {
        walletService= new WalletService(walletRepository)
    }

    def "지갑 생성 요청시 지갑을 갖고 있지 않다면 생성된다"() {
        given:
        def userId = 1L
        CreateWalletRequest request = new CreateWalletRequest(userId);
        walletRepository.findWalletByUserId(_) >> Optional.empty()
        walletRepository.save(_) >> new Wallet(userId)

        when:
        def createdWallet = walletService.create(request);

        then:
        1 * walletRepository.save(_) >> new Wallet(userId)
        createdWallet != null
        createdWallet.userId == 1L
        createdWallet.balance == BigDecimal.ZERO
    }

    def "지갑 생성 요청시 지갑을 이미 가지고 있다면 예외를 반환한다" () {
        given:
        def userId = 1L
        CreateWalletRequest request = new CreateWalletRequest(userId);
        walletRepository.findWalletByUserId(_) >> Optional.of(new Wallet(1L, 1L, BigDecimal.ZERO, null, null))

        when:
        walletService.create(request);

        then:
        def ex = thrown(IllegalStateException)
        ex != null
    }

    def "지갑을 조회한다 - 생성되어 있는 경우"() {
        given:
        def userId = 1L
        def wallet = new Wallet(userId)
        wallet.balance = new BigDecimal(1000)
        walletRepository.findWalletByUserId(userId) >> Optional.of(wallet)

        when:
        def result = walletService.findWalletByUserId(userId);

        then:
        result != null
        result.balance() == new BigDecimal(1000)
    }

    def "지갑을 조회한다 - 생성되어 있지 않은 경우"() {
        given:
        def userId = 1L
        walletRepository.findWalletByUserId(userId) >> Optional.empty()

        when:
        def result = walletService.findWalletByUserId(userId);

        then:
        result == null
    }

    def "잔액을 충전한다 - 지갑이 없는 경우 예외를 반환한다" () {
        given:
        def request = new AddBalanceWalletRequest(1L, BigDecimal.ONE);
        walletRepository.findById(_) >> Optional.empty();

        when:
        walletService.addBalance(request);

        then:
        def ex = thrown(IllegalStateException);
        ex.getMessage() == "지갑이 없습니다"
    }

    def "잔액을 충전한다 - 잔액이 충분하지 않는 경우 예외를 반환한다" () {
        given:
        def walletId = 1L
        def request = new AddBalanceWalletRequest(walletId, BigDecimal.valueOf(-1L));
        walletRepository.findById(_) >> Optional.of(new Wallet(walletId, 1L, BigDecimal.ZERO, null, null))

        when:
        walletService.addBalance(request);

        then:
        def ex = thrown(IllegalStateException)
        ex.getMessage() == "잔액이 충분하지 않습니다"
    }


    def "잔액을 충전한다 - 한도를 초과한 경우 예외를 반환한다" () {
        given:
        def walletId = 1L
        def request = new AddBalanceWalletRequest(walletId, BigDecimal.valueOf(10_0001L));
        walletRepository.findById(_) >> Optional.of(new Wallet(walletId, 1L, BigDecimal.ZERO, null, null))

        when:
        walletService.addBalance(request);

        then:
        def ex = thrown(IllegalStateException)
        ex.getMessage() == "한도를 초과했습니다"
    }

    def "잔액을 충전한다 - 성공한 경우 응답을 반환한다" () {
        given:
        def walletId = 1L
        def amount = 100_000L
        def request = new AddBalanceWalletRequest(walletId, BigDecimal.valueOf(amount));
        walletRepository.findById(_) >> Optional.of(new Wallet(walletId, 1L, BigDecimal.ZERO, null, null))

        when:
        def result = walletService.addBalance(request);

        then:
        result.id() == walletId
        result.balance() == amount
    }
}
