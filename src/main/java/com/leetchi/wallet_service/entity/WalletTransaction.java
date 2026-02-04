package com.leetchi.wallet_service.entity;


import com.leetchi.wallet_service.enums.TransactionType;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name ="wallet_transactions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class WalletTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;  //debit or credit

    @Column(nullable = false)
    private LocalDateTime timestamp;

    private UUID referenceId; // id cagnotte or another detination

    private String description;

    @PrePersist // for sur if timestamp it's work after insertion
    public void prePersist(){

        this.timestamp = LocalDateTime.now();
    }
}
