package com.leetchi.wallet_service.repository;

import com.leetchi.wallet_service.entity.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, UUID> {

}
