package com.leetchi.wallet_service.dto;

import java.util.UUID;

public record WalletRequest(UUID userId, String currency) {
}
