package com.example.simplepayment.wallet.presentation;

import com.example.simplepayment.wallet.application.WalletService;
import com.example.simplepayment.wallet.presentation.request.AddBalanceWalletRequest;
import com.example.simplepayment.wallet.presentation.request.CreateWalletRequest;
import com.example.simplepayment.wallet.presentation.response.AddBalanceWalletResponse;
import com.example.simplepayment.wallet.presentation.response.CreateWalletResponse;
import com.example.simplepayment.wallet.presentation.response.SearchWalletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @PostMapping("/api/wallet")
    public CreateWalletResponse createWallet(@RequestBody CreateWalletRequest request) {
        return walletService.create(request);
    }

    @GetMapping("/api/{userId}/wallet")
    public SearchWalletResponse findWalletByUserId(@PathVariable("userId") Long userId) {
        return walletService.findWalletByUserId(userId);
    }
}
