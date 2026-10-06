'use client';

import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { useAuthStore } from '@/stores/authStore';
import { useCartStore } from '@/stores/cartStore';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { ShoppingBag, Home, Receipt, User, Plus, Minus, X, Coffee } from 'lucide-react';
import { useState } from 'react';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 1000 * 60 * 5,
      retry: 1,
    },
  },
});

function CartDrawer() {
  const { items, subtotal, isOpen, setCartOpen, updateItemQuantity, removeItem } = useCartStore();

  if (!isOpen) return null;

  return (
    <>
      <div className="fixed inset-0 bg-black/60 z-40" onClick={() => setCartOpen(false)} />
      <div className="fixed right-0 top-0 bottom-0 w-full max-w-md bg-[#1E293B] z-50 flex flex-col animate-slide-in">
        <div className="flex items-center justify-between p-4 border-b border-[#334155]">
          <h2 className="text-lg font-semibold text-white">Your Cart</h2>
          <button onClick={() => setCartOpen(false)} className="p-2 hover:bg-[#334155] rounded-full transition-colors">
            <X className="w-5 h-5 text-gray-400" />
          </button>
        </div>
        <div className="flex-1 overflow-y-auto p-4 space-y-4">
          {items.length === 0 ? (
            <div className="flex flex-col items-center justify-center h-full text-center">
              <ShoppingBag className="w-12 h-12 text-gray-600 mb-3" />
              <p className="text-gray-400">Your cart is empty</p>
              <p className="text-gray-500 text-sm mt-1">Add some delicious items!</p>
            </div>
          ) : (
            items.map((item) => (
              <div key={item.id} className="flex gap-3 bg-[#0B0F19] rounded-xl p-3">
                <div className="w-16 h-16 bg-[#334155] rounded-lg flex items-center justify-center">
                  <Coffee className="w-8 h-8 text-[#5A6BFF]" />
                </div>
                <div className="flex-1">
                  <h3 className="text-white font-medium text-sm">{item.product?.name || 'Product'}</h3>
                  <p className="text-[#5A6BFF] font-semibold">
                    {new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(item.line_total)}
                  </p>
                  <div className="flex items-center gap-2 mt-2">
                    <button onClick={() => updateItemQuantity(item.id, item.quantity - 1)} className="w-7 h-7 bg-[#334155] rounded-full flex items-center justify-center">
                      <Minus className="w-3 h-3 text-white" />
                    </button>
                    <span className="text-white font-medium w-6 text-center">{item.quantity}</span>
                    <button onClick={() => updateItemQuantity(item.id, item.quantity + 1)} className="w-7 h-7 bg-[#5A6BFF] rounded-full flex items-center justify-center">
                      <Plus className="w-3 h-3 text-white" />
                    </button>
                  </div>
                </div>
                <button onClick={() => removeItem(item.id)} className="p-1 hover:bg-[#334155] rounded-full self-start">
                  <X className="w-4 h-4 text-gray-400" />
                </button>
              </div>
            ))
          )}
        </div>
        {items.length > 0 && (
          <div className="p-4 border-t border-[#334155] space-y-4">
            <div className="flex justify-between text-white">
              <span className="font-medium">Total</span>
              <span className="font-bold text-lg">
                {new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(subtotal)}
              </span>
            </div>
            <Link href="/cart" onClick={() => setCartOpen(false)} className="block w-full bg-[#5A6BFF] hover:bg-[#4855e8] text-white font-semibold py-4 rounded-xl transition-colors text-center">
              View Cart
            </Link>
          </div>
        )}
      </div>
    </>
  );
}

function BottomNav() {
  const pathname = usePathname();
  const { itemCount, setCartOpen } = useCartStore();

  const tabs = [
    { href: '/', icon: Home, label: 'Home' },
    { href: '/orders', icon: Receipt, label: 'Orders' },
    { href: '/profile', icon: User, label: 'Profile' },
  ];

  return (
    <nav className="fixed bottom-0 left-0 right-0 bg-[#1E293B] border-t border-[#334155] z-30">
      <div className="max-w-md mx-auto flex items-center justify-around py-2">
        {tabs.map((tab) => {
          const isActive = pathname === tab.href;
          return (
            <Link key={tab.href} href={tab.href} className={`flex flex-col items-center gap-1 px-4 py-2 rounded-xl transition-colors ${isActive ? 'text-[#5A6BFF]' : 'text-gray-400'}`}>
              <tab.icon className="w-6 h-6" />
              <span className="text-xs font-medium">{tab.label}</span>
            </Link>
          );
        })}
        <button onClick={() => setCartOpen(true)} className="flex flex-col items-center gap-1 px-4 py-2 rounded-xl text-gray-400 hover:text-[#5A6BFF] transition-colors relative">
          <ShoppingBag className="w-6 h-6" />
          <span className="text-xs font-medium">Cart</span>
          {itemCount > 0 && (
            <span className="absolute -top-1 -right-1 w-5 h-5 bg-[#EF4444] text-white text-xs font-bold rounded-full flex items-center justify-center">
              {itemCount}
            </span>
          )}
        </button>
      </div>
    </nav>
  );
}

function AppShell({ children }: { children: React.ReactNode }) {
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated);
  const { itemCount, setCartOpen } = useCartStore();

  if (!isAuthenticated) {
    return <div className="min-h-screen bg-[#0B0F19]">{children}</div>;
  }

  return (
    <div className="min-h-screen bg-[#0B0F19] pb-20">
      <div className="sticky top-0 z-20 bg-[#0B0F19]/90 backdrop-blur-lg border-b border-[#1E293B]">
        <div className="max-w-md mx-auto px-4 py-3 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-gradient-to-br from-[#5A6BFF] to-[#8B5CF6] rounded-xl flex items-center justify-center">
              <Coffee className="w-5 h-5 text-white" />
            </div>
            <div>
              <h1 className="text-white font-semibold">AI Café</h1>
              <p className="text-gray-400 text-xs">Order with AI</p>
            </div>
          </div>
          <button onClick={() => setCartOpen(true)} className="relative p-2 bg-[#1E293B] rounded-xl">
            <ShoppingBag className="w-5 h-5 text-white" />
            {itemCount > 0 && (
              <span className="absolute -top-1 -right-1 w-4 h-4 bg-[#EF4444] text-white text-[10px] font-bold rounded-full flex items-center justify-center">
                {itemCount}
              </span>
            )}
          </button>
        </div>
      </div>
      <main className="max-w-md mx-auto">{children}</main>
      <BottomNav />
      <CartDrawer />
    </div>
  );
}

export function Providers({ children }: { children: React.ReactNode }) {
  return (
    <QueryClientProvider client={queryClient}>
      <AppShell>{children}</AppShell>
    </QueryClientProvider>
  );
}
