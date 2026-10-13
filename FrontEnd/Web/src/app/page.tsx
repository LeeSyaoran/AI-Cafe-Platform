'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { useAuthStore } from '@/stores/authStore';
import { useCartStore } from '@/stores/cartStore';
import { getCategories, getProducts } from '@/services/api/endpoints/menu';
import { useQuery } from '@tanstack/react-query';
import { Plus, Star, Clock, Flame, Search, ChevronRight, User, LogOut, LayoutDashboard } from 'lucide-react';
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
  const { isAuthenticated, user, logout } = useAuthStore();
  const { items } = useCartStore();
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState<string | null>(null);
  const [showUserMenu, setShowUserMenu] = useState(false);

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

  // Get dashboard link based on role
  const getDashboardLink = () => {
    switch (user?.role) {
      case 'admin': return '/admin';
      case 'manager': return '/manager';
      case 'staff': return '/staff';
      case 'cashier': return '/cashier';
      default: return null;
    }
  };

  const handleLogout = () => {
    logout();
    router.push('/login');
  };

  if (!isAuthenticated) {
    return null;
  }

  const dashboardLink = getDashboardLink();

  return (
    <div className="min-h-screen bg-[#0B0F19] pb-20">
      {/* Header */}
      <header className="sticky top-0 bg-[#0B0F19]/95 backdrop-blur-lg border-b border-[#334155] z-50">
        <div className="px-4 py-3">
          <div className="flex items-center justify-between mb-4">
            {/* Logo */}
            <Link href="/" className="flex items-center gap-2">
              <div className="w-10 h-10 bg-gradient-to-br from-[#5A6BFF] to-[#8B5CF6] rounded-xl flex items-center justify-center">
                <span className="text-lg">☕</span>
              </div>
              <span className="text-white font-bold text-lg">AI Café</span>
            </Link>

            {/* Right Side */}
            <div className="flex items-center gap-3">
              {/* Cart */}
              <Link
                href="/cart"
                className="relative w-10 h-10 bg-[#1E293B] rounded-xl flex items-center justify-center"
              >
                <span className="text-lg">🛒</span>
                {items.length > 0 && (
                  <span className="absolute -top-1 -right-1 w-5 h-5 bg-[#5A6BFF] rounded-full text-white text-xs flex items-center justify-center font-bold">
                    {items.length}
                  </span>
                )}
              </Link>

              {/* User Menu */}
              <div className="relative">
                <button
                  onClick={() => setShowUserMenu(!showUserMenu)}
                  className="w-10 h-10 bg-[#1E293B] rounded-xl flex items-center justify-center"
                >
                  <User className="w-5 h-5 text-gray-400" />
                </button>

                {/* Dropdown */}
                {showUserMenu && (
                  <>
                    <div className="fixed inset-0 z-40" onClick={() => setShowUserMenu(false)} />
                    <div className="absolute right-0 top-12 w-64 bg-[#1E293B] rounded-xl border border-[#334155] shadow-xl z-50 overflow-hidden">
                      {/* User Info */}
                      <div className="p-4 border-b border-[#334155]">
                        <p className="text-white font-medium">{user?.name || 'User'}</p>
                        <p className="text-gray-400 text-sm">{user?.email}</p>
                        <span className={`inline-block mt-2 px-2 py-0.5 rounded text-xs font-medium ${
                          user?.role === 'admin' ? 'bg-purple-500/20 text-purple-400' :
                          user?.role === 'manager' ? 'bg-blue-500/20 text-blue-400' :
                          user?.role === 'staff' ? 'bg-green-500/20 text-green-400' :
                          user?.role === 'cashier' ? 'bg-amber-500/20 text-amber-400' :
                          'bg-gray-500/20 text-gray-400'
                        }`}>
                          {user?.role === 'admin' ? 'Quản trị viên' :
                           user?.role === 'manager' ? 'Quản lý' :
                           user?.role === 'staff' ? 'Nhân viên' :
                           user?.role === 'cashier' ? 'Thu ngân' :
                           'Khách hàng'}
                        </span>
                      </div>

                      {/* Menu Items */}
                      <div className="p-2">
                        {dashboardLink && (
                          <Link
                            href={dashboardLink}
                            className="flex items-center gap-3 px-3 py-2 text-gray-400 hover:text-white hover:bg-[#334155] rounded-lg transition-colors"
                            onClick={() => setShowUserMenu(false)}
                          >
                            <LayoutDashboard className="w-4 h-4" />
                            Dashboard
                          </Link>
                        )}
                        <Link
                          href="/profile"
                          className="flex items-center gap-3 px-3 py-2 text-gray-400 hover:text-white hover:bg-[#334155] rounded-lg transition-colors"
                          onClick={() => setShowUserMenu(false)}
                        >
                          <User className="w-4 h-4" />
                          Hồ sơ
                        </Link>
                        <button
                          onClick={handleLogout}
                          className="w-full flex items-center gap-3 px-3 py-2 text-red-400 hover:bg-red-500/10 rounded-lg transition-colors"
                        >
                          <LogOut className="w-4 h-4" />
                          Đăng xuất
                        </button>
                      </div>
                    </div>
                  </>
                )}
              </div>
            </div>
          </div>

          {/* Welcome */}
          <div className="mb-4">
            <h1 className="text-2xl font-bold text-white">
              Xin chào{user?.name ? `, ${user.name}` : ''}! 👋
            </h1>
            <p className="text-gray-400 text-sm mt-1">Bạn muốn đặt gì hôm nay?</p>
          </div>

          {/* Search */}
          <div className="relative">
            <Search className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Tìm kiếm cà phê, trà..."
              className="w-full bg-[#1E293B] border border-[#334155] rounded-xl pl-12 pr-4 py-3 text-white placeholder-gray-500 focus:outline-none focus:border-[#5A6BFF] transition-colors"
            />
          </div>
        </div>
      </header>

      {/* Content */}
      <div className="px-4 py-4">
        {/* Categories */}
        <div className="flex gap-2 overflow-x-auto pb-4 scrollbar-hide">
          <button
            onClick={() => setSelectedCategory(null)}
            className={`px-4 py-2 rounded-full text-sm font-medium whitespace-nowrap transition-colors ${
              !selectedCategory
                ? 'bg-[#5A6BFF] text-white'
                : 'bg-[#1E293B] text-gray-400 hover:bg-[#334155]'
            }`}
          >
            Tất cả
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
                <h3 className="text-white font-bold">Bán chạy nhất 🔥</h3>
                <p className="text-white/80 text-sm">Thử ngay những món được yêu thích nhất!</p>
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
            <h3 className="text-white font-semibold mb-2">Không tìm thấy sản phẩm</h3>
            <p className="text-gray-400 text-sm">
              {searchQuery ? 'Thử từ khóa khác' : 'Chưa có menu nào'}
            </p>
          </div>
        )}
      </div>
    </div>
  );
}
