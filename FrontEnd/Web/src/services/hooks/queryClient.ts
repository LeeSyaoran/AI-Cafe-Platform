// ============================================
// Query Client - TanStack Query setup
// ============================================

import { QueryClient } from '@tanstack/react-query';

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 1000 * 60 * 5, // 5 phút
      gcTime: 1000 * 60 * 30,   // 30 phút (cache)
      retry: 1,
      refetchOnWindowFocus: false,
    },
    mutations: {
      onError: (error: unknown) => {
        console.error('Mutation error:', error);
      },
    },
  },
});

// Query keys factory - giúp type-safe cache invalidation
export const queryKeys = {
  // Auth
  profile: ['profile'] as const,

  // Menu
  cafes: ['cafes'] as const,
  cafe: (id: string) => ['cafes', id] as const,
  categories: ['categories'] as const,
  category: (id: string) => ['categories', id] as const,
  products: (filters?: Record<string, string>) => ['products', filters] as const,
  product: (id: string) => ['products', id] as const,
  featured: ['products', 'featured'] as const,
  bestSellers: ['products', 'best-sellers'] as const,
  menu: ['menu'] as const,

  // Cart
  cart: ['cart'] as const,

  // Orders
  orders: (params?: { limit?: number; offset?: number }) => ['orders', params] as const,
  order: (id: string) => ['orders', id] as const,

  // Wallet
  wallet: ['wallet'] as const,
  credits: ['credits'] as const,
};
