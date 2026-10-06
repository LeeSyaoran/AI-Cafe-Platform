import { api } from '../client';
import type { ApiResponse } from '@/types';
import type { Wallet, CreditBalance } from '@/types';

// Get wallet balance
export async function getWalletBalance(): Promise<ApiResponse<Wallet>> {
  return api.get<ApiResponse<Wallet>>('/v1/wallet', true);
}

// Get credit balance
export async function getCreditBalance(): Promise<ApiResponse<CreditBalance>> {
  return api.get<ApiResponse<CreditBalance>>('/v1/credits/balance', true);
}
