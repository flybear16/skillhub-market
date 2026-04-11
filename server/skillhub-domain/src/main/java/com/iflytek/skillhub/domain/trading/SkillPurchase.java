package com.iflytek.skillhub.domain.trading;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "skill_purchase")
public class SkillPurchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, length = 128)
    private String userId;

    @Column(name = "skill_id", nullable = false)
    private Long skillId;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "purchased_at", nullable = false, updatable = false)
    private Instant purchasedAt;

    protected SkillPurchase() {}

    public SkillPurchase(String userId, Long skillId, Long orderId) {
        this.userId = userId;
        this.skillId = skillId;
        this.orderId = orderId;
    }

    @PrePersist
    protected void onCreate() {
        purchasedAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getUserId() { return userId; }
    public Long getSkillId() { return skillId; }
    public Long getOrderId() { return orderId; }
    public Instant getPurchasedAt() { return purchasedAt; }
}
