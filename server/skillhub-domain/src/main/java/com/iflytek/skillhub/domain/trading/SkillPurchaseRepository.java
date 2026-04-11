package com.iflytek.skillhub.domain.trading;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SkillPurchaseRepository extends JpaRepository<SkillPurchase, Long> {
    Optional<SkillPurchase> findByUserIdAndSkillId(String userId, Long skillId);
    boolean existsByUserIdAndSkillId(String userId, Long skillId);
}
