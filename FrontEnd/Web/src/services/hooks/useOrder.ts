// ============================================
// Order Hooks - TanStack Query hooks cho orders
// ============================================

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import * as orderApi from '../api/endpoints/order';
import { queryKeys } from './queryClient';

export function useOrders(params?: { limit?: number; offset?: number }) {
  return useQuery({
    queryKey: queryKeys.orders(params),
    queryFn: () => orderApi.getOrders(params),
  });
}

export function useOrder(id: string) {
  return useQuery({
    queryKey: queryKeys.order(id),
    queryFn: () => orderApi.getOrder(id),
    enabled: !!id,
  });
}

export function useCreateOrder() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: orderApi.createOrder,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['orders'] });
      queryClient.invalidateQueries({ queryKey: queryKeys.cart });
    },
  });
}

export function useCancelOrder() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: orderApi.cancelOrder,
    onSuccess: (_, orderId) => {
      queryClient.invalidateQueries({ queryKey: ['orders'] });
      queryClient.setQueryData(queryKeys.order(orderId), (old: any) => ({
        ...old,
        status: 'cancelled',
      }));
    },
  });
}
