package com.iflytek.skillhub.controller.portal;

import com.iflytek.skillhub.controller.BaseApiController;
import com.iflytek.skillhub.domain.trading.*;
import com.iflytek.skillhub.dto.ApiResponse;
import com.iflytek.skillhub.dto.ApiResponseFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/marketplace")
public class TradingController extends BaseApiController {

    private final TradingService tradingService;

    public TradingController(ApiResponseFactory responseFactory, TradingService tradingService) {
        super(responseFactory);
        this.tradingService = tradingService;
    }

    @PostMapping("/purchase/{skillId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> purchaseSkill(
            @PathVariable Long skillId,
            @AuthenticationPrincipal OAuth2User user) {
        String userId = resolveUserId(user);
        SkillOrder order = tradingService.purchaseSkill(userId, skillId);
        return ResponseEntity.ok(ok("marketplace.purchase.success", Map.of(
                "orderNo", order.getOrderNo(),
                "amount", order.getAmount(),
                "status", order.getStatus().name()
        )));
    }

    @GetMapping("/purchased/{skillId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkPurchased(
            @PathVariable Long skillId,
            @AuthenticationPrincipal OAuth2User user) {
        String userId = resolveUserId(user);
        boolean purchased = tradingService.hasPurchased(userId, skillId);
        return ResponseEntity.ok(ok("marketplace.purchase.check", Map.of("purchased", purchased)));
    }

    @GetMapping("/wallet")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getWallet(
            @AuthenticationPrincipal OAuth2User user) {
        String userId = resolveUserId(user);
        Wallet wallet = tradingService.getOrCreateWallet(userId);
        return ResponseEntity.ok(ok("marketplace.wallet.info", Map.of(
                "balance", wallet.getBalance(),
                "frozenBalance", wallet.getFrozenBalance(),
                "totalIncome", wallet.getTotalIncome(),
                "totalSpent", wallet.getTotalSpent()
        )));
    }

    @PostMapping("/wallet/deposit")
    public ResponseEntity<ApiResponse<Map<String, Object>>> deposit(
            @RequestBody Map<String, BigDecimal> body,
            @AuthenticationPrincipal OAuth2User user) {
        String userId = resolveUserId(user);
        Wallet wallet = tradingService.getOrCreateWallet(userId);
        wallet.deposit(body.get("amount"));
        return ResponseEntity.ok(ok("marketplace.wallet.deposit", Map.of(
                "balance", wallet.getBalance()
        )));
    }

    @PostMapping("/refund/{orderNo}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> refund(
            @PathVariable String orderNo,
            @AuthenticationPrincipal OAuth2User user) {
        String userId = resolveUserId(user);
        tradingService.refundOrder(userId, orderNo);
        return ResponseEntity.ok(ok("marketplace.refund.success", Map.of(
                "orderNo", orderNo
        )));
    }

    private String resolveUserId(OAuth2User user) {
        if (user == null) throw new IllegalStateException("请先登录");
        return user.getName();
    }
}
