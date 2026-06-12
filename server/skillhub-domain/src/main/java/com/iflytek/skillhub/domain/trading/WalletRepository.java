package com.iflytek.skillhub.domain.trading;

import java.util.Optional;

public interface WalletRepository {
    Optional<Wallet> findByUserId(String userId);
    Wallet save(Wallet wallet);
}
