package com.leetchi.wallet_service.service;

import com.leetchi.wallet_service.dto.BalanceOperationRequest;
import com.leetchi.wallet_service.dto.WalletRequest;
import com.leetchi.wallet_service.dto.WalletTransactionEvent;
import com.leetchi.wallet_service.entity.Wallet;
import com.leetchi.wallet_service.entity.WalletTransaction;
import com.leetchi.wallet_service.enums.TransactionType;
import com.leetchi.wallet_service.enums.WalletStatus;
import com.leetchi.wallet_service.repository.WalletRepository;
import com.leetchi.wallet_service.repository.WalletTransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;

    // inject kafka tool (it's the tool who communicate with kfaka) send interface
    private final KafkaTemplate<String, WalletTransactionEvent> kafkaTemplate;

    //get name of object from application.propreties
    @Value("${application.kafka.topic.wallet-transaction}")
    private String topicName;


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
        WalletTransaction tx = saveTransaction(wallet, request.amount(), TransactionType.CREDIT, request.referenceId());
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

        // 1 local debit
        wallet.setBalance(wallet.getBalance().subtract(request.amount()));
        walletRepository.save(wallet);

        //2 save historic
        WalletTransaction tx = saveTransaction(wallet, request.amount(), TransactionType.DEBIT, request.referenceId());
        //3 send KafkaEvent if the cagnotte (
        if(request.referenceId() != null){
            WalletTransactionEvent event = new WalletTransactionEvent(
                    tx.getId(),
                    request.referenceId(),
                    request.amount(),
                    "DEBIT"
            );

        // Send msg in topic x
        kafkaTemplate.send(topicName, event);
        System.out.println("msg Kafka send for cagnotte :" + request.referenceId());
        }
        return wallet;
        }

        //
    private WalletTransaction saveTransaction(Wallet wallet, BigDecimal amount, TransactionType type, UUID referenceId){
        WalletTransaction transaction = WalletTransaction.builder()
                .wallet(wallet)
                .amount(amount)
                .type(type)
                .referenceId(referenceId)
                .description(type == TransactionType.CREDIT ? "loading" : "participation Cagnotte")
                .build();
      return  transactionRepository.save(transaction);
    }

}
