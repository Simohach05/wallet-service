package com.leetchi.wallet_service.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record WalletTransactionEvent(
        UUID transactionId,
        UUID referenceId, // id cagnotte
        BigDecimal amount,
        String type // "DEBIT" or "CREDIT"
) {}
