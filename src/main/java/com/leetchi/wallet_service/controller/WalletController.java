package com.leetchi.wallet_service.controller;


import com.leetchi.wallet_service.dto.BalanceOperationRequest;
import com.leetchi.wallet_service.dto.WalletRequest;
import com.leetchi.wallet_service.entity.Wallet;
import com.leetchi.wallet_service.repository.WalletRepository;
import com.leetchi.wallet_service.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @PostMapping
    public ResponseEntity<Wallet> createWallet(@RequestBody WalletRequest request){
        return ResponseEntity.ok(walletService.createWallet(request));
    }
    @PostMapping("/credit")
    public ResponseEntity<Wallet> credit(@RequestBody BalanceOperationRequest request){
        return ResponseEntity.ok(walletService.creditWallet(request));
    }
    @PostMapping("/debit")
    public ResponseEntity<Wallet> debit(@RequestBody BalanceOperationRequest request){
        return ResponseEntity.ok(walletService.debitWallet(request));
    }
}
