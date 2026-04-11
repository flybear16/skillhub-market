-- ============================================================
-- V39: Skill Marketplace - 定价、订单、钱包
-- ============================================================

-- 1. Skill 表增加定价字段
ALTER TABLE skill
  ADD COLUMN IF NOT EXISTS price NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
  ADD COLUMN IF NOT EXISTS pricing_model VARCHAR(20) NOT NULL DEFAULT 'FREE',
  ADD COLUMN IF NOT EXISTS currency VARCHAR(10) NOT NULL DEFAULT 'CNY';

COMMENT ON COLUMN skill.price IS '技能价格，0 表示免费';
COMMENT ON COLUMN skill.pricing_model IS '定价模型: FREE / PAID / FREEMIUM / SUBSCRIPTION';
COMMENT ON COLUMN skill.currency IS '货币单位: CNY / USD';

-- 2. 用户钱包
CREATE TABLE IF NOT EXISTS wallet (
  id              BIGSERIAL PRIMARY KEY,
  user_id         VARCHAR(128) NOT NULL UNIQUE,
  balance         NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
  frozen_balance  NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
  total_income    NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
  total_spent     NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
  created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

COMMENT ON TABLE wallet IS '用户钱包';

-- 3. 钱包流水
CREATE TABLE IF NOT EXISTS wallet_transaction (
  id              BIGSERIAL PRIMARY KEY,
  wallet_id       BIGINT NOT NULL REFERENCES wallet(id),
  type            VARCHAR(20) NOT NULL,
  amount          NUMERIC(12, 2) NOT NULL,
  balance_after   NUMERIC(12, 2) NOT NULL,
  order_id        BIGINT,
  description     VARCHAR(500),
  created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

COMMENT ON TABLE wallet_transaction IS '钱包流水';
COMMENT ON COLUMN wallet_transaction.type IS 'DEPOSIT / WITHDRAW / PURCHASE / INCOME / REFUND';

CREATE INDEX idx_wallet_tx_wallet_id ON wallet_transaction(wallet_id);
CREATE INDEX idx_wallet_tx_created_at ON wallet_transaction(created_at);

-- 4. 订单表
CREATE TABLE IF NOT EXISTS skill_order (
  id              BIGSERIAL PRIMARY KEY,
  order_no        VARCHAR(64) NOT NULL UNIQUE,
  buyer_id        VARCHAR(128) NOT NULL,
  seller_id       VARCHAR(128) NOT NULL,
  skill_id        BIGINT NOT NULL,
  skill_version   VARCHAR(50),
  skill_name      VARCHAR(200),
  amount          NUMERIC(10, 2) NOT NULL,
  platform_fee    NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
  seller_income   NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
  status          VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  payment_method  VARCHAR(30),
  paid_at         TIMESTAMPTZ,
  completed_at    TIMESTAMPTZ,
  cancelled_at    TIMESTAMPTZ,
  refunded_at     TIMESTAMPTZ,
  created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

COMMENT ON TABLE skill_order IS '技能交易订单';
COMMENT ON COLUMN skill_order.status IS 'PENDING / PAID / COMPLETED / CANCELLED / REFUNDED';

CREATE INDEX idx_order_buyer ON skill_order(buyer_id);
CREATE INDEX idx_order_seller ON skill_order(seller_id);
CREATE INDEX idx_order_skill ON skill_order(skill_id);
CREATE INDEX idx_order_status ON skill_order(status);

-- 5. 已购买记录（购买后永久可用）
CREATE TABLE IF NOT EXISTS skill_purchase (
  id              BIGSERIAL PRIMARY KEY,
  user_id         VARCHAR(128) NOT NULL,
  skill_id        BIGINT NOT NULL,
  order_id        BIGINT NOT NULL REFERENCES skill_order(id),
  purchased_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE(user_id, skill_id)
);

COMMENT ON TABLE skill_purchase IS '已购买技能记录';

CREATE INDEX idx_purchase_user ON skill_purchase(user_id);
