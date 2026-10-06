'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { useAuthStore } from '@/stores/authStore';
import { useCartStore } from '@/stores/cartStore';
import { getCategories, getProducts } from '@/services/api/endpoints/menu';
import { useQuery } from '@tanstack/react-query';
import { Plus, Star, Clock, Flame, Search, ChevronRight } from 'lucide-react';
import { useState } from 'react';
import type { Product, Category } from '@/types';

function ProductCard({ product }: { product: Product }) {
  const { addItem } = useCartStore();

  return (
    <div className="bg-[#1E293B] rounded-2xl overflow-hidden group">
      <div className="aspect-square bg-gradient-to-br from-[#334155] to-[#1E293B] relative">
        <div className="absolute inset-0 flex items-center justify-center">
          <span className="text-6xl">
            {product.is_best_seller ? '☕' : product.is_featured ? '🧊' : '☕'}
          </span>
        </div>
        {product.is_best_seller && (
          <div className="absolute top-2 left-2 bg-[#F59E0B] text-black text-[10px] font-bold px-2 py-1 rounded-full flex items-center gap-1">
            <Star className="w-3 h-3" /> Best Seller
          </div>
        )}
        <button
          onClick={() => addItem(product)}
          className="absolute bottom-2 right-2 w-8 h-8 bg-[#5A6BFF] rounded-full flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity"
        >
          <Plus className="w-4 h-4 text-white" />
        </button>
      </div>
      <div className="p-3">
        <h3 className="text-white font-semibold text-sm mb-1 line-clamp-1">{product.name}</h3>
        {product.short_description && (
          <p className="text-gray-400 text-xs mb-2 line-clamp-1">{product.short_description}</p>
        )}
        <div className="flex items-center justify-between">
          <span className="text-[#5A6BFF] font-bold">
            {new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(product.price)}
          </span>
          {product.preparation_time && (
            <span className="text-gray-500 text-xs flex items-center gap-1">
              <Clock className="w-3 h-3" /> {product.preparation_time}m
            </span>
          )}
        </div>
      </div>
    </div>
  );
}

function CategoryChip({ category, isActive, onClick }: { category: Category; isActive: boolean; onClick: () => void }) {
  return (
    <button
      onClick={onClick}
      className={`px-4 py-2 rounded-full text-sm font-medium whitespace-nowrap transition-colors ${
        isActive
          ? 'bg-[#5A6BFF] text-white'
          : 'bg-[#1E293B] text-gray-400 hover:bg-[#334155]'
      }`}
    >
      {category.name}
    </button>
  );
}

function MenuSkeleton() {
  return (
    <div className="grid grid-cols-2 gap-3">
      {[1, 2, 3, 4, 5, 6].map((i) => (
        <div key={i} className="bg-[#1E293B] rounded-2xl overflow-hidden animate-pulse">
          <div className="aspect-square bg-[#334155]" />
          <div className="p-3 space-y-2">
            <div className="h-4 bg-[#334155] rounded w-3/4" />
            <div className="h-3 bg-[#334155] rounded w-1/2" />
          </div>
        </div>
      ))}
    </div>
  );
}

export default function HomePage() {
  const router = useRouter();
  const { isAuthenticated, user } = useAuthStore();
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState<string | null>(null);

  // Redirect to login if not authenticated
  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/login');
    }
  }, [isAuthenticated, router]);

  // Fetch categories
  const { data: categoriesData } = useQuery({
    queryKey: ['categories'],
    queryFn: getCategories,
    enabled: isAuthenticated,
  });

  // Fetch products
  const { data: productsData, isLoading } = useQuery({
    queryKey: ['products', selectedCategory, searchQuery],
    queryFn: () => getProducts({
      category_id: selectedCategory || undefined,
      search: searchQuery || undefined,
      limit: 20,
    }),
    enabled: isAuthenticated,
  });

  if (!isAuthenticated) {
    return null;
  }

  return (
    <div className="px-4 py-4">
      {/* Welcome */}
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-white">
          Hello{user?.name ? `, ${user.name}` : ''}! 👋
        </h1>
        <p className="text-gray-400 text-sm mt-1">What would you like to order today?</p>
      </div>

      {/* Search */}
      <div className="relative mb-4">
        <Search className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
        <input
          type="text"
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          placeholder="Search for coffee, tea, or snacks..."
          className="w-full bg-[#1E293B] border border-[#334155] rounded-2xl pl-12 pr-4 py-4 text-white placeholder-gray-500 focus:outline-none focus:border-[#5A6BFF] transition-colors"
        />
      </div>

      {/* Categories */}
      <div className="flex gap-2 overflow-x-auto pb-4 -mx-4 px-4 scrollbar-hide">
        <button
          onClick={() => setSelectedCategory(null)}
          className={`px-4 py-2 rounded-full text-sm font-medium whitespace-nowrap transition-colors ${
            !selectedCategory
              ? 'bg-[#5A6BFF] text-white'
              : 'bg-[#1E293B] text-gray-400 hover:bg-[#334155]'
          }`}
        >
          All
        </button>
        {categoriesData?.data?.map((category: Category) => (
          <CategoryChip
            key={category.id}
            category={category}
            isActive={selectedCategory === category.id}
            onClick={() => setSelectedCategory(category.id)}
          />
        ))}
      </div>

      {/* Best Sellers Banner */}
      {!selectedCategory && !searchQuery && (
        <div className="bg-gradient-to-r from-[#5A6BFF] to-[#8B5CF6] rounded-2xl p-4 mb-4">
          <div className="flex items-center gap-3">
            <div className="w-12 h-12 bg-white/20 rounded-xl flex items-center justify-center">
              <Flame className="w-6 h-6 text-white" />
            </div>
            <div className="flex-1">
              <h3 className="text-white font-bold">Best Sellers 🔥</h3>
              <p className="text-white/80 text-sm">Try our most popular drinks!</p>
            </div>
            <ChevronRight className="w-5 h-5 text-white" />
          </div>
        </div>
      )}

      {/* Products Grid */}
      {isLoading ? (
        <MenuSkeleton />
      ) : productsData?.data && productsData.data.length > 0 ? (
        <div className="grid grid-cols-2 gap-3">
          {productsData.data.map((product: Product) => (
            <ProductCard key={product.id} product={product} />
          ))}
        </div>
      ) : (
        <div className="text-center py-12">
          <div className="w-16 h-16 bg-[#1E293B] rounded-full mx-auto mb-4 flex items-center justify-center">
            <span className="text-3xl">☕</span>
          </div>
          <h3 className="text-white font-semibold mb-2">No products found</h3>
          <p className="text-gray-400 text-sm">
            {searchQuery ? 'Try a different search term' : 'No menu items available'}
          </p>
        </div>
      )}
    </div>
  );
}
