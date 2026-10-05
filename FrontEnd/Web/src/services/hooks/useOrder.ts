// ============================================
// Order Hooks - TanStack Query hooks cho orders
// ============================================

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import * as orderApi from '../api/order';
import { queryKeys } from './queryClient';
import type { CreateOrderRequest } from '../../types';

export function useOrders(params?: { limit?: number; offset?: number }) {
  return useQuery({
    queryKey: queryKeys.orders(params),
    queryFn: () => orderApi.listOrders(params),
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
    mutationFn: (data: CreateOrderRequest) => orderApi.createOrder(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['orders'] });
      queryClient.invalidateQueries({ queryKey: queryKeys.cart });
    },
  });
}

export function useCancelOrder() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: string) => orderApi.cancelOrder(id),
    onSuccess: (_, orderId) => {
      queryClient.invalidateQueries({ queryKey: ['orders'] });
      queryClient.setQueryData(queryKeys.order(orderId), (old: any) => ({
        ...old,
        status: 'cancelled',
      }));
    },
  });
}
