package com.leetchi.wallet_service.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record BalanceOperationRequest(UUID userId, BigDecimal amount, UUID referenceId) {
}
