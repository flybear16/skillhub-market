package com.iflytek.skillhub.domain.trading;

import java.util.Optional;

public interface SkillPurchaseRepository {
    Optional<SkillPurchase> findByUserIdAndSkillId(String userId, Long skillId);
    boolean existsByUserIdAndSkillId(String userId, Long skillId);
    SkillPurchase save(SkillPurchase purchase);
    void delete(SkillPurchase purchase);
}
