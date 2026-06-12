package com.iflytek.skillhub.infra.jpa;

import com.iflytek.skillhub.domain.trading.SkillPurchase;
import com.iflytek.skillhub.domain.trading.SkillPurchaseRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SkillPurchaseJpaRepository extends JpaRepository<SkillPurchase, Long>, SkillPurchaseRepository {
    Optional<SkillPurchase> findByUserIdAndSkillId(String userId, Long skillId);
    boolean existsByUserIdAndSkillId(String userId, Long skillId);
}
