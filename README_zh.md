<div align="center">
  <img src="./skillhub-logo.svg" alt="SkillMarket Logo" width="120" height="120" />
  <h1>SkillMarket 🛒</h1>
  <p>基于 iFlytek SkillHub 打造的 <strong>Skill 私有交易平台</strong> — 发布、定价、交易 AI Agent 技能包</p>
</div>

---

## 🎯 项目定位

SkillMarket 是在 [iFlytek SkillHub](https://github.com/iflytek/skillhub) 基础上改造的 **Skill 私有交易平台**，核心新增：

- 💰 **技能定价** - 作者可为技能设置价格（免费/付费/免费增值/订阅）
- 🛒 **在线交易** - 买家通过钱包余额一键购买，即时交付
- 💼 **钱包系统** - 充值、余额支付、收入提现
- 📊 **收益分成** - 自动计算平台抽成与作者收入
- 🔄 **7天退款** - 支持无理由退款，保障买家权益
- 📋 **订单管理** - 完整的订单生命周期管理

## 🏗️ 技术架构

基于 SkillHub 原有架构，新增交易模块：

```
┌─────────────────────────────────────────┐
│           Frontend (React 19)           │
│  ┌─────────┐ ┌──────────┐ ┌──────────┐ │
│  │技能浏览  │ │ 购买/支付 │ │ 钱包管理  │ │
│  └─────────┘ └──────────┘ └──────────┘ │
└────────────────┬────────────────────────┘
                 │ REST API
┌────────────────┴────────────────────────┐
│        Backend (Spring Boot 3)          │
│  ┌──────────┐ ┌──────────┐ ┌─────────┐ │
│  │ Trading   │ │  Wallet   │ │  Order  │ │
│  │ Service   │ │  Service  │ │ Service │ │
│  └──────────┘ └──────────┘ └─────────┘ │
│  ┌────────────────────────────────────┐ │
│  │       PostgreSQL + Redis          │ │
│  └────────────────────────────────────┘ │
└─────────────────────────────────────────┘
```

## 🆕 新增模块

### 数据库 (V39 migration)
- `skill.price` / `skill.pricing_model` / `skill.currency` - 技能定价字段
- `wallet` - 用户钱包表
- `wallet_transaction` - 钱包流水
- `skill_order` - 交易订单
- `skill_purchase` - 购买记录

### 后端 (Java)
- `com.iflytek.skillhub.domain.trading` - 交易领域模型
  - `Wallet` / `SkillOrder` / `SkillPurchase` - 实体
  - `TradingService` - 核心交易逻辑
  - `TradingController` - REST API

### 前端 (React/TypeScript)
- `@/api/marketplace` - 交易 API 客户端
- `@/features/marketplace/purchase-button` - 购买按钮 + 价格标签组件
- i18n 中文翻译支持

## 📡 新增 API

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/v1/marketplace/purchase/{skillId}` | 购买技能 |
| GET | `/api/v1/marketplace/purchased/{skillId}` | 检查是否已购买 |
| GET | `/api/v1/marketplace/wallet` | 查看钱包 |
| POST | `/api/v1/marketplace/wallet/deposit` | 充值 |
| POST | `/api/v1/marketplace/refund/{orderNo}` | 申请退款 |

## 🚀 快速开始

### 本地开发

```bash
# 启动完整服务栈
make dev-all

# 或使用 Docker
docker compose up -d
```

### 配置

在 `.env.local` 中设置：

```env
# 平台抽成比例（默认10%）
MARKETPLACE_PLATFORM_FEE_RATE=0.10
# 默认货币
MARKETPLACE_DEFAULT_CURRENCY=CNY
```

## 📦 定价模型

| 模型 | 说明 |
|------|------|
| `FREE` | 完全免费 |
| `PAID` | 一次性付费 |
| `FREEMIUM` | 基础功能免费，高级功能收费 |
| `SUBSCRIPTION` | 订阅制（按月/年） |

## 🔄 与原版 SkillHub 的关系

本项目 Fork 自 [iflytek/skillhub](https://github.com/iflytek/skillhub)，保留了原有的：
- ✅ 技能发布与版本管理
- ✅ 命名空间与权限管理
- ✅ 审核与治理
- ✅ 搜索发现
- ✅ 社交功能（Star / Rating）
- ✅ CLI 支持
- ✅ SSO / OAuth 认证

新增交易功能完全兼容原有架构，不破坏任何现有功能。

## 📄 License

Apache 2.0 (继承自 SkillHub)
