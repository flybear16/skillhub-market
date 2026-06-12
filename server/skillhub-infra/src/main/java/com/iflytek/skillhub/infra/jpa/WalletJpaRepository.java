package com.iflytek.skillhub.infra.jpa;

import com.iflytek.skillhub.domain.trading.Wallet;
import com.iflytek.skillhub.domain.trading.WalletRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletJpaRepository extends JpaRepository<Wallet, Long>, WalletRepository {
    Optional<Wallet> findByUserId(String userId);
}
