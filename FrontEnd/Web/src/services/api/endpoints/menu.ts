// ============================================
// Menu & Products API - Public endpoints
// ============================================

import { api } from './client';
import type {
  Cafe,
  Category,
  Product,
  ProductListResponse,
  MenuCategory,
  PaginationParams,
} from '../../types';

// ===== CAFES =====

// List all cafes
export async function listCafes(): Promise<Cafe[]> {
  return api.get<Cafe[]>('/v1/cafes');
}

// Get cafe by ID
export async function getCafe(id: string): Promise<Cafe> {
  return api.get<Cafe>(`/v1/cafes/${id}`);
}

// ===== CATEGORIES =====

// List all categories
export async function listCategories(): Promise<Category[]> {
  return api.get<Category[]>('/v1/categories');
}

// Get category with products
export async function getCategory(id: string): Promise<Category & { products: Product[] }> {
  return api.get<Category & { products: Product[] }>(`/v1/categories/${id}`);
}

// ===== PRODUCTS =====

// List products with filters
export async function listProducts(
  params?: PaginationParams & { categoryId?: string }
): Promise<ProductListResponse> {
  const searchParams = new URLSearchParams();
  if (params?.categoryId) searchParams.set('categoryId', params.categoryId);
  if (params?.limit) searchParams.set('limit', String(params.limit));
  if (params?.offset) searchParams.set('offset', String(params.offset));

  const query = searchParams.toString();
  return api.get<ProductListResponse>(`/v1/products${query ? `?${query}` : ''}`);
}

// Get featured products
export async function getFeaturedProducts(): Promise<Product[]> {
  return api.get<Product[]>('/v1/products/featured');
}

// Get best sellers
export async function getBestSellers(): Promise<Product[]> {
  return api.get<Product[]>('/v1/products/best-sellers');
}

// Get product by ID
export async function getProduct(id: string): Promise<Product> {
  return api.get<Product>(`/v1/products/${id}`);
}

// ===== MENU =====

// Get full menu (categories + products nested)
export async function getMenu(): Promise<MenuCategory[]> {
  return api.get<MenuCategory[]>('/v1/menu');
}
