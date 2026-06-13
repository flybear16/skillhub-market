<div align="center">
  <img src="./skillhub-logo.svg" alt="SkillMarket Logo" width="120" height="120" />
  <h1>SkillMarket 🛒</h1>
  <p>A <strong>private skill trading platform</strong> built on top of <a href="https://github.com/iflytek/skillhub">iFlytek SkillHub</a> — publish, price, and trade AI agent skill packages.</p>
</div>

---

> **AI编程 × 智能硬件实践者** · 让想法长出骨头


## 🎯 What is SkillMarket?

SkillMarket extends the enterprise-grade SkillHub registry into a **marketplace** where skills can be bought and sold:

- 💰 **Skill Pricing** — Authors set prices (Free / Paid / Freemium / Subscription)
- 🛒 **One-Click Purchase** — Buyers purchase via wallet balance, instant delivery
- 💼 **Wallet System** — Deposit, balance payments, income tracking
- 📊 **Revenue Split** — Automatic platform fee + author income calculation
- 🔄 **7-Day Refund** — Buyer protection with hassle-free refunds
- 📋 **Order Management** — Full order lifecycle (pending → paid → completed / refunded)

## 🆕 What's New

### Database (V39 migration)
- `skill.price` / `pricing_model` / `currency` fields
- `wallet` — User wallets
- `wallet_transaction` — Transaction ledger
- `skill_order` — Purchase orders
- `skill_purchase` — Purchase records

### Backend (Java/Spring Boot)
- `domain.trading` — Trading domain models (`Wallet`, `SkillOrder`, `SkillPurchase`)
- `TradingService` — Core buy/refund logic with transactional safety
- `TradingController` — REST API endpoints

### Frontend (React/TypeScript)
- `@/api/marketplace` — API client
- `@/features/marketplace` — Purchase button & price badge components
- i18n support (zh/en)

## 📡 New API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/v1/marketplace/purchase/{skillId}` | Purchase a skill |
| GET | `/api/v1/marketplace/purchased/{skillId}` | Check if purchased |
| GET | `/api/v1/marketplace/wallet` | View wallet |
| POST | `/api/v1/marketplace/wallet/deposit` | Deposit funds |
| POST | `/api/v1/marketplace/refund/{orderNo}` | Request refund |

## 🚀 Quick Start

```bash
make dev-all
```

## 📦 Pricing Models

| Model | Description |
|-------|-------------|
| `FREE` | Completely free |
| `PAID` | One-time payment |
| `FREEMIUM` | Basic free, premium paid |
| `SUBSCRIPTION` | Monthly/yearly subscription |

## 🔄 Relationship with SkillHub

Forked from [iflytek/skillhub](https://github.com/iflytek/skillhub). All original features preserved:

- ✅ Skill publishing & versioning
- ✅ Namespace & RBAC governance
- ✅ Search & discovery
- ✅ Social features (Stars / Ratings)
- ✅ CLI support & ClawHub compatibility
- ✅ SSO / OAuth authentication

## 📄 License

Apache 2.0 (inherited from SkillHub)
