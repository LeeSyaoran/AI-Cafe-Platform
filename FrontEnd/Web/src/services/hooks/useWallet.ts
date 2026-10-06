// ============================================
// Wallet Hooks - TanStack Query hooks cho wallet & credits
// ============================================

import { useQuery } from '@tanstack/react-query';
import * as walletApi from '../api/endpoints/wallet';
import { queryKeys } from './queryClient';

export function useWallet() {
  return useQuery({
    queryKey: queryKeys.wallet,
    queryFn: walletApi.getWalletBalance,
  });
}

export function useCreditBalance() {
  return useQuery({
    queryKey: queryKeys.credits,
    queryFn: walletApi.getCreditBalance,
  });
}
