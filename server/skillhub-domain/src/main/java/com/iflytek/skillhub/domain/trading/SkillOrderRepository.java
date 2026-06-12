package com.iflytek.skillhub.domain.trading;

import java.util.Optional;

public interface SkillOrderRepository {
    Optional<SkillOrder> findByOrderNo(String orderNo);
    SkillOrder save(SkillOrder order);
}
