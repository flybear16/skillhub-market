import { useState } from 'react'
import { useTranslation } from 'react-i18next'
import { ShoppingCart, Check, Lock, Wallet } from 'lucide-react'
import { purchaseSkill, checkPurchased } from '@/api/marketplace'
import { useAuth } from '@/features/auth/use-auth'
import { Button } from '@/shared/ui/button'
import { toast } from '@/shared/lib/toast'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'

interface PurchaseButtonProps {
  skillId: number
  price: number
  currency?: string
  ownerId?: string
  onPurchased?: () => void
}

export function PurchaseButton({ skillId, price, currency = 'CNY', ownerId, onPurchased }: PurchaseButtonProps) {
  const { t } = useTranslation()
  const { user } = useAuth()
  const queryClient = useQueryClient()

  // Free skill - no button needed
  if (price === 0 || price === undefined) {
    return null
  }

  // Own skill
  if (user && ownerId && user.id === ownerId) {
    return null
  }

  const { data: purchased, isLoading: checkingPurchase } = useQuery({
    queryKey: ['purchased', skillId],
    queryFn: () => checkPurchased(skillId),
    enabled: !!user,
  })

  const purchaseMutation = useMutation({
    mutationFn: () => purchaseSkill(skillId),
    onSuccess: () => {
      toast.success(t('marketplace.purchase.success'))
      queryClient.invalidateQueries({ queryKey: ['purchased', skillId] })
      onPurchased?.()
    },
    onError: (err: Error) => {
      toast.error(err.message || t('marketplace.purchase.failed'))
    },
  })

  if (purchased) {
    return (
      <div className="flex items-center gap-2 text-green-600">
        <Check className="h-5 w-5" />
        <span className="text-sm font-medium">{t('marketplace.purchased')}</span>
      </div>
    )
  }

  const currencySymbol = currency === 'CNY' ? '¥' : '$'

  return (
    <div className="flex flex-col gap-3">
      <div className="flex items-baseline gap-2">
        <span className="text-3xl font-bold text-primary">{currencySymbol}{price.toFixed(2)}</span>
        <span className="text-sm text-muted-foreground">{t('marketplace.oneTime')}</span>
      </div>
      <Button
        size="lg"
        className="gap-2"
        onClick={() => purchaseMutation.mutate()}
        disabled={purchaseMutation.isPending || !user}
      >
        {purchaseMutation.isPending ? (
          <span className="animate-spin">⏳</span>
        ) : (
          <ShoppingCart className="h-5 w-5" />
        )}
        {user ? t('marketplace.buyNow') : t('marketplace.loginToBuy')}
      </Button>
      <p className="text-xs text-muted-foreground flex items-center gap-1">
        <Lock className="h-3 w-3" />
        {t('marketplace.securePayment')}
      </p>
    </div>
  )
}

export function PriceBadge({ price, currency = 'CNY' }: { price: number; currency?: string }) {
  if (!price || price === 0) {
    return (
      <span className="inline-flex items-center rounded-full bg-green-100 px-2.5 py-0.5 text-xs font-medium text-green-800">
        免费
      </span>
    )
  }
  const symbol = currency === 'CNY' ? '¥' : '$'
  return (
    <span className="inline-flex items-center rounded-full bg-blue-100 px-2.5 py-0.5 text-xs font-semibold text-blue-800">
      {symbol}{price.toFixed(2)}
    </span>
  )
}
