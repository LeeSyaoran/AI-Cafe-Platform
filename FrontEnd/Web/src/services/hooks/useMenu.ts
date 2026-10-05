// ============================================
// Menu Hooks - TanStack Query hooks cho menu & products
// ============================================

import { useQuery } from '@tanstack/react-query';
import * as menuApi from '../api/menu';
import { queryKeys } from './queryClient';
import type { PaginationParams } from '../../types';

// ===== CAFES =====

export function useCafes() {
  return useQuery({
    queryKey: queryKeys.cafes,
    queryFn: menuApi.listCafes,
    staleTime: 1000 * 60 * 10, // 10 phút
  });
}

export function useCafe(id: string) {
  return useQuery({
    queryKey: queryKeys.cafe(id),
    queryFn: () => menuApi.getCafe(id),
    enabled: !!id,
  });
}

// ===== CATEGORIES =====

export function useCategories() {
  return useQuery({
    queryKey: queryKeys.categories,
    queryFn: menuApi.listCategories,
    staleTime: 1000 * 60 * 10,
  });
}

export function useCategory(id: string) {
  return useQuery({
    queryKey: queryKeys.category(id),
    queryFn: () => menuApi.getCategory(id),
    enabled: !!id,
  });
}

// ===== PRODUCTS =====

export function useProducts(params?: PaginationParams & { categoryId?: string }) {
  return useQuery({
    queryKey: queryKeys.products(params),
    queryFn: () => menuApi.listProducts(params),
  });
}

export function useProduct(id: string) {
  return useQuery({
    queryKey: queryKeys.product(id),
    queryFn: () => menuApi.getProduct(id),
    enabled: !!id,
  });
}

export function useFeaturedProducts() {
  return useQuery({
    queryKey: queryKeys.featured,
    queryFn: menuApi.getFeaturedProducts,
    staleTime: 1000 * 60 * 5,
  });
}

export function useBestSellers() {
  return useQuery({
    queryKey: queryKeys.bestSellers,
    queryFn: menuApi.getBestSellers,
    staleTime: 1000 * 60 * 5,
  });
}

// ===== MENU =====

export function useMenu() {
  return useQuery({
    queryKey: queryKeys.menu,
    queryFn: menuApi.getMenu,
    staleTime: 1000 * 60 * 5,
  });
}
