// ============================================
// Wallet & Credits API - Protected endpoints
// ============================================

import { api } from './client';
import type { Wallet, CreditBalance } from '../../types';

// Get wallet balance
export async function getWallet(): Promise<Wallet> {
  return api.get<Wallet>('/v1/wallet', true);
}

// Get AI credits balance
export async function getCreditBalance(): Promise<CreditBalance> {
  return api.get<CreditBalance>('/v1/credits/balance', true);
}
