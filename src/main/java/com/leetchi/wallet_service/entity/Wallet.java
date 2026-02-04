package com.leetchi.wallet_service.entity;

import com.leetchi.wallet_service.enums.WalletStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "wallets")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, unique = true)
    private UUID userId; // link to microservice identity for user
    @Column(nullable = false)
    private BigDecimal balance; // sold
    @Column(length = 3)
    private String currency; //MAD or USD or EUR
    @Enumerated(EnumType.STRING)
    private WalletStatus status;
    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "wallet", fetch = FetchType.LAZY)
    private List<WalletTransaction> transactions;
}
