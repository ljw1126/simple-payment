package com.example.simplepayment.controller;

import com.example.simplepayment.controller.request.AddBalanceWalletRequest;
import com.example.simplepayment.controller.request.CreateWalletRequest;
import com.example.simplepayment.controller.response.AddBalanceWalletResponse;
import com.example.simplepayment.controller.response.CreateWalletResponse;
import com.example.simplepayment.controller.response.SearchWalletResponse;
import com.example.simplepayment.service.WalletService;
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

    @PatchMapping("/api/wallet/balance")
    public AddBalanceWalletResponse addBalanceWallet(@RequestBody AddBalanceWalletRequest request) {
        return walletService.addBalance(request);
    }
}
