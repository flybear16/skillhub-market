package com.iflytek.skillhub.domain.trading;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SkillOrderRepository extends JpaRepository<SkillOrder, Long> {
    Optional<SkillOrder> findByOrderNo(String orderNo);
}
