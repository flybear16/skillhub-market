package com.iflytek.skillhub.domain.trading;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "skill_order")
public class SkillOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_no", nullable = false, unique = true, length = 64)
    private String orderNo;

    @Column(name = "buyer_id", nullable = false, length = 128)
    private String buyerId;

    @Column(name = "seller_id", nullable = false, length = 128)
    private String sellerId;

    @Column(name = "skill_id", nullable = false)
    private Long skillId;

    @Column(name = "skill_version", length = 50)
    private String skillVersion;

    @Column(name = "skill_name", length = 200)
    private String skillName;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "platform_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal platformFee = BigDecimal.ZERO;

    @Column(name = "seller_income", nullable = false, precision = 10, scale = 2)
    private BigDecimal sellerIncome = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "payment_method", length = 30)
    private String paymentMethod;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "refunded_at")
    private Instant refundedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected SkillOrder() {}

    public SkillOrder(String orderNo, String buyerId, String sellerId,
                      Long skillId, String skillName, BigDecimal amount,
                      BigDecimal platformFeeRate) {
        this.orderNo = orderNo;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.skillId = skillId;
        this.skillName = skillName;
        this.amount = amount;
        this.platformFee = amount.multiply(platformFeeRate)
                .setScale(2, BigDecimal.ROUND_HALF_UP);
        this.sellerIncome = amount.subtract(this.platformFee);
    }

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    public void markPaid(String paymentMethod) {
        this.status = OrderStatus.PAID;
        this.paymentMethod = paymentMethod;
        this.paidAt = Instant.now();
    }

    public void complete() {
        this.status = OrderStatus.COMPLETED;
        this.completedAt = Instant.now();
    }

    public void cancel() {
        if (this.status == OrderStatus.PAID || this.status == OrderStatus.COMPLETED) {
            throw new IllegalStateException("已支付/已完成的订单无法取消");
        }
        this.status = OrderStatus.CANCELLED;
        this.cancelledAt = Instant.now();
    }

    public void refund() {
        if (this.status != OrderStatus.PAID && this.status != OrderStatus.COMPLETED) {
            throw new IllegalStateException("仅已支付/已完成的订单可退款");
        }
        this.status = OrderStatus.REFUNDED;
        this.refundedAt = Instant.now();
    }

    // Getters
    public Long getId() { return id; }
    public String getOrderNo() { return orderNo; }
    public String getBuyerId() { return buyerId; }
    public String getSellerId() { return sellerId; }
    public Long getSkillId() { return skillId; }
    public String getSkillVersion() { return skillVersion; }
    public String getSkillName() { return skillName; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getPlatformFee() { return platformFee; }
    public BigDecimal getSellerIncome() { return sellerIncome; }
    public OrderStatus getStatus() { return status; }
    public String getPaymentMethod() { return paymentMethod; }
    public Instant getPaidAt() { return paidAt; }
    public Instant getCompletedAt() { return completedAt; }
    public Instant getCancelledAt() { return cancelledAt; }
    public Instant getRefundedAt() { return refundedAt; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
