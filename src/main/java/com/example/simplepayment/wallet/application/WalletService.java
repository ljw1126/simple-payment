package com.example.simplepayment.wallet.application;

import com.example.simplepayment.wallet.domain.Wallet;
import com.example.simplepayment.wallet.domain.WalletRepository;
import com.example.simplepayment.wallet.presentation.request.AddBalanceWalletRequest;
import com.example.simplepayment.wallet.presentation.request.CreateWalletRequest;
import com.example.simplepayment.wallet.presentation.response.AddBalanceWalletResponse;
import com.example.simplepayment.wallet.presentation.response.CreateWalletResponse;
import com.example.simplepayment.wallet.presentation.response.SearchWalletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class WalletService {
    private final WalletRepository walletRepository;

    @Transactional
    public CreateWalletResponse create(CreateWalletRequest request) {
        boolean isPresent = walletRepository.findWalletByUserId(request.userId()).isPresent();
        if(isPresent) {
            throw new IllegalStateException("이미 지갑이 있습니다");
        }

        final Wallet saved = walletRepository.save(new Wallet(request.userId()));
        return new CreateWalletResponse(saved.getId(), saved.getUserId(), saved.getBalance());
    }

    @Transactional(readOnly = true)
    public SearchWalletResponse findWalletByUserId(Long userId) {
        return walletRepository.findWalletByUserId(userId)
                .map(w -> new SearchWalletResponse(w.getId(), w.getUserId(), w.getBalance(), w.getCreatedAt(), w.getUpdatedAt()))
                .orElseThrow(() -> new IllegalStateException("사용자의 지갑이 존재하지 않습니다"));
    }

    @Transactional(readOnly = true)
    public SearchWalletResponse findWalletByWalletId(Long walletId) {
        return walletRepository.findById(walletId)
                .map(w -> new SearchWalletResponse(w.getId(), w.getUserId(), w.getBalance(), w.getCreatedAt(), w.getUpdatedAt()))
                .orElseThrow(() -> new IllegalStateException("사용자의 지갑이 존재하지 않습니다"));
    }

    @Transactional
    public AddBalanceWalletResponse addBalance(AddBalanceWalletRequest request) {
        final Wallet wallet = walletRepository.findById(request.walletId())
                .orElseThrow(() -> new IllegalStateException("지갑이 없습니다"));

        wallet.addBalance(request.amount(), LocalDateTime.now());
        walletRepository.save(wallet);

        return new AddBalanceWalletResponse(
                wallet.getId(),
                wallet.getUserId(),
                wallet.getBalance(),
                wallet.getCreatedAt(),
                wallet.getUpdatedAt()
        );
    }
}
