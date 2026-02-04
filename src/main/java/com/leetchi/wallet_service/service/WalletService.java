package com.leetchi.wallet_service.service;


import com.leetchi.wallet_service.dto.BalanceOperationRequest;
import com.leetchi.wallet_service.dto.WalletRequest;
import com.leetchi.wallet_service.entity.Wallet;
import com.leetchi.wallet_service.entity.WalletTransaction;
import com.leetchi.wallet_service.enums.TransactionType;
import com.leetchi.wallet_service.enums.WalletStatus;
import com.leetchi.wallet_service.repository.WalletRepository;
import com.leetchi.wallet_service.repository.WalletTransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;

    //1 create a wallet when user Register
    public Wallet createWallet(WalletRequest request){
        Wallet wallet = Wallet.builder()
                .userId(request.userId())
                .balance(BigDecimal.ZERO) // start with 0
                .currency(request.currency() !=null ? request.currency() :"MAD")
                .status(WalletStatus.ACTIVE)
                .build();
        return walletRepository.save(wallet);
    }
    //2 charge account (credit)
    @Transactional
    public Wallet creditWallet(BalanceOperationRequest request) {
        Wallet wallet = walletRepository.findByUserId(request.userId())
                .orElseThrow(()-> new RuntimeException("Wallet not found !"));
                // update balance
        wallet.setBalance(wallet.getBalance().add(request.amount()));
        walletRepository.save(wallet);
        // save historic
        saveTransaction(wallet, request.amount(), TransactionType.CREDIT, request.referenceId());
        return wallet;
    }

    // 3 payment (Debit)
    @Transactional
    public Wallet debitWallet(BalanceOperationRequest request){
        Wallet wallet= walletRepository.findByUserId(request.userId())
                .orElseThrow(()-> new RuntimeException("Wallet not found !"));

        // cheek balance
        if (wallet.getBalance().compareTo(request.amount()) < 0){
            throw new RuntimeException("insufficient balance!");
        }
        //debit
        wallet.setBalance(wallet.getBalance().subtract(request.amount()));
        walletRepository.save(wallet);

        //save historic
        saveTransaction(wallet, request.amount(), TransactionType.DEBIT, request.referenceId());

        // reserve it for kafka (cagnotte) notification

        return wallet;
        }

        //
    private void saveTransaction(Wallet wallet, BigDecimal amount, TransactionType type, UUID referenceId){
        WalletTransaction transaction = WalletTransaction.builder()
                .wallet(wallet)
                .amount(amount)
                .type(type)
                .referenceId(referenceId)
                .description(type == TransactionType.CREDIT ? "loading" : "participation Cagnotte")
                .build();
        transactionRepository.save(transaction);
    }

}
