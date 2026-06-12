package com.iflytek.skillhub.infra.jpa;

import com.iflytek.skillhub.domain.trading.SkillOrder;
import com.iflytek.skillhub.domain.trading.SkillOrderRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SkillOrderJpaRepository extends JpaRepository<SkillOrder, Long>, SkillOrderRepository {
    Optional<SkillOrder> findByOrderNo(String orderNo);
}
