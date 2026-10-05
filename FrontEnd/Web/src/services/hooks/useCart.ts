// ============================================
// Cart Hooks - TanStack Query hooks cho cart
// ============================================

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import * as cartApi from '../api/cart';
import { queryKeys } from './queryClient';
import type { AddToCartRequest, UpdateCartItemRequest } from '../../types';

export function useCart() {
  return useQuery({
    queryKey: queryKeys.cart,
    queryFn: cartApi.getCart,
    retry: false, // Cart cần auth, không retry 401
  });
}

export function useAddToCart() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: AddToCartRequest) => cartApi.addToCart(data),
    onSuccess: (cart) => {
      queryClient.setQueryData(queryKeys.cart, cart);
    },
  });
}

export function useUpdateCartItem() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ itemId, data }: { itemId: string; data: UpdateCartItemRequest }) =>
      cartApi.updateCartItem(itemId, data),
    onSuccess: (cart) => {
      queryClient.setQueryData(queryKeys.cart, cart);
    },
  });
}

export function useRemoveFromCart() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (itemId: string) => cartApi.removeFromCart(itemId),
    onSuccess: (cart) => {
      queryClient.setQueryData(queryKeys.cart, cart);
    },
  });
}

export function useClearCart() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: () => cartApi.clearCart(),
    onSuccess: () => {
      queryClient.setQueryData(queryKeys.cart, null);
    },
  });
}

export function useCheckout() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: () => cartApi.checkoutCart(),
    onSuccess: () => {
      queryClient.setQueryData(queryKeys.cart, null);
      // Invalidate orders
      queryClient.invalidateQueries({ queryKey: ['orders'] });
    },
  });
}
