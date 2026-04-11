import { buildApiUrl, WEB_API_PREFIX } from '@/api/client'

export interface WalletInfo {
  balance: number
  frozenBalance: number
  totalIncome: number
  totalSpent: number
}

export interface PurchaseResult {
  orderNo: string
  amount: number
  status: string
}

export async function getWallet(): Promise<WalletInfo> {
  const res = await fetch(`${WEB_API_PREFIX}/marketplace/wallet`, { credentials: 'include' })
  const json = await res.json()
  return json.data
}

export async function deposit(amount: number): Promise<WalletInfo> {
  const res = await fetch(`${WEB_API_PREFIX}/marketplace/wallet/deposit`, {
    method: 'POST',
    credentials: 'include',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ amount }),
  })
  const json = await res.json()
  return json.data
}

export async function purchaseSkill(skillId: number): Promise<PurchaseResult> {
  const res = await fetch(`${WEB_API_PREFIX}/marketplace/purchase/${skillId}`, {
    method: 'POST',
    credentials: 'include',
  })
  const json = await res.json()
  return json.data
}

export async function checkPurchased(skillId: number): Promise<boolean> {
  const res = await fetch(`${WEB_API_PREFIX}/marketplace/purchased/${skillId}`, {
    credentials: 'include',
  })
  const json = await res.json()
  return json.data?.purchased ?? false
}

export async function refundOrder(orderNo: string): Promise<void> {
  await fetch(`${WEB_API_PREFIX}/marketplace/refund/${orderNo}`, {
    method: 'POST',
    credentials: 'include',
  })
}
