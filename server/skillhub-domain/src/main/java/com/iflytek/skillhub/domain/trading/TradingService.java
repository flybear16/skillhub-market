package com.iflytek.skillhub.domain.trading;

import com.iflytek.skillhub.domain.skill.Skill;
import com.iflytek.skillhub.domain.skill.SkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
public class TradingService {

    private static final BigDecimal PLATFORM_FEE_RATE = new BigDecimal("0.10"); // 10% 平台抽成

    private final SkillRepository skillRepository;
    private final SkillOrderRepository orderRepository;
    private final SkillPurchaseRepository purchaseRepository;
    private final WalletRepository walletRepository;

    public TradingService(SkillRepository skillRepository,
                          SkillOrderRepository orderRepository,
                          SkillPurchaseRepository purchaseRepository,
                          WalletRepository walletRepository) {
        this.skillRepository = skillRepository;
        this.orderRepository = orderRepository;
        this.purchaseRepository = purchaseRepository;
        this.walletRepository = walletRepository;
    }

    /**
     * 购买技能（余额支付）
     */
    @Transactional
    public SkillOrder purchaseSkill(String buyerId, Long skillId) {
        // 1. 检查是否已购买
        if (purchaseRepository.existsByUserIdAndSkillId(buyerId, skillId)) {
            throw new IllegalStateException("您已购买该技能");
        }

        // 2. 获取技能信息
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new IllegalArgumentException("技能不存在"));

        // 3. 检查是否免费
        if (skill.getPrice().compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalStateException("免费技能无需购买");
        }

        // 4. 不能买自己的
        if (skill.getOwnerId().equals(buyerId)) {
            throw new IllegalStateException("不能购买自己的技能");
        }

        // 5. 扣除买家余额
        Wallet buyerWallet = walletRepository.findByUserId(buyerId)
                .orElseThrow(() -> new IllegalStateException("请先开通钱包"));
        buyerWallet.deduct(skill.getPrice());

        // 6. 创建订单
        String orderNo = "ORD" + Instant.now().toEpochMilli() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        SkillOrder order = new SkillOrder(orderNo, buyerId, skill.getOwnerId(),
                skillId, skill.getDisplayName(), skill.getPrice(), PLATFORM_FEE_RATE);
        order.markPaid("WALLET");
        order = orderRepository.save(order);

        // 7. 卖家收入
        Wallet sellerWallet = walletRepository.findByUserId(skill.getOwnerId())
                .orElseGet(() -> walletRepository.save(new Wallet(skill.getOwnerId())));
        sellerWallet.addIncome(order.getSellerIncome());

        // 8. 完成订单
        order.complete();
        order = orderRepository.save(order);

        // 9. 记录购买
        SkillPurchase purchase = new SkillPurchase(buyerId, skillId, order.getId());
        purchaseRepository.save(purchase);

        return order;
    }

    /**
     * 检查用户是否已购买某技能
     */
    public boolean hasPurchased(String userId, Long skillId) {
        return purchaseRepository.existsByUserIdAndSkillId(userId, skillId);
    }

    /**
     * 获取或创建钱包
     */
    @Transactional
    public Wallet getOrCreateWallet(String userId) {
        return walletRepository.findByUserId(userId)
                .orElseGet(() -> walletRepository.save(new Wallet(userId)));
    }

    /**
     * 退款（7天内）
     */
    @Transactional
    public void refundOrder(String buyerId, String orderNo) {
        SkillOrder order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));

        if (!order.getBuyerId().equals(buyerId)) {
            throw new IllegalStateException("只能退款自己的订单");
        }

        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new IllegalStateException("仅已完成的订单可退款");
        }

        // 7天退款期
        if (order.getCompletedAt().plusSeconds(7 * 24 * 3600).isBefore(Instant.now())) {
            throw new IllegalStateException("超过7天退款期");
        }

        // 退款
        order.refund();
        orderRepository.save(order);

        // 买家加回余额
        Wallet buyerWallet = walletRepository.findByUserId(buyerId)
                .orElseThrow(() -> new IllegalStateException("钱包不存在"));
        buyerWallet.addIncome(order.getAmount());

        // 卖家扣除收入
        walletRepository.findByUserId(order.getSellerId()).ifPresent(sellerWallet -> {
            sellerWallet.deduct(order.getSellerIncome());
        });

        // 删除购买记录
        purchaseRepository.findByUserIdAndSkillId(buyerId, order.getSkillId())
                .ifPresent(purchaseRepository::delete);
    }
}
