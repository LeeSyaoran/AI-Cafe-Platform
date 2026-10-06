'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useAuthStore } from '@/stores/authStore';
import { useCartStore } from '@/stores/cartStore';
import { Plus, Minus, X, Coffee, ShoppingBag, ArrowLeft, Trash2 } from 'lucide-react';

export default function CartPage() {
  const router = useRouter();
  const { isAuthenticated } = useAuthStore();
  const { items, subtotal, updateItemQuantity, removeItem, clearCart } = useCartStore();

  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/login');
    }
  }, [isAuthenticated, router]);

  if (!isAuthenticated) {
    return null;
  }

  return (
    <div className="min-h-screen bg-[#0B0F19]">
      {/* Header */}
      <div className="sticky top-0 z-20 bg-[#0B0F19]/90 backdrop-blur-lg border-b border-[#1E293B]">
        <div className="max-w-md mx-auto px-4 py-4">
          <div className="flex items-center gap-4">
            <button
              onClick={() => router.back()}
              className="p-2 hover:bg-[#1E293B] rounded-xl transition-colors"
            >
              <ArrowLeft className="w-6 h-6 text-white" />
            </button>
            <div className="flex-1">
              <h1 className="text-xl font-bold text-white">Cart</h1>
              <p className="text-gray-400 text-sm">{items.length} item{items.length !== 1 ? 's' : ''}</p>
            </div>
            {items.length > 0 && (
              <button
                onClick={clearCart}
                className="p-2 hover:bg-red-500/10 rounded-xl transition-colors"
              >
                <Trash2 className="w-5 h-5 text-red-400" />
              </button>
            )}
          </div>
        </div>
      </div>

      {/* Content */}
      <div className="max-w-md mx-auto px-4 py-4">
        {items.length === 0 ? (
          <div className="text-center py-20">
            <div className="w-20 h-20 bg-[#1E293B] rounded-full mx-auto mb-6 flex items-center justify-center">
              <ShoppingBag className="w-10 h-10 text-gray-500" />
            </div>
            <h2 className="text-xl font-bold text-white mb-2">Your cart is empty</h2>
            <p className="text-gray-400 mb-6">Add some delicious items from the menu!</p>
            <button
              onClick={() => router.push('/')}
              className="bg-[#5A6BFF] hover:bg-[#4855e8] text-white font-semibold px-6 py-3 rounded-xl transition-colors"
            >
              Browse Menu
            </button>
          </div>
        ) : (
          <div className="space-y-3">
            {items.map((item) => (
              <div key={item.id} className="bg-[#1E293B] rounded-2xl p-4">
                <div className="flex gap-4">
                  <div className="w-20 h-20 bg-[#334155] rounded-xl flex items-center justify-center flex-shrink-0">
                    <Coffee className="w-10 h-10 text-[#5A6BFF]" />
                  </div>
                  <div className="flex-1 min-w-0">
                    <div className="flex justify-between items-start">
                      <div className="flex-1 min-w-0">
                        <h3 className="text-white font-semibold truncate">
                          {item.product?.name || 'Product'}
                        </h3>
                        {item.product?.short_description && (
                          <p className="text-gray-400 text-sm truncate">
                            {item.product.short_description}
                          </p>
                        )}
                      </div>
                      <button
                        onClick={() => removeItem(item.id)}
                        className="p-1.5 hover:bg-[#334155] rounded-lg transition-colors"
                      >
                        <X className="w-4 h-4 text-gray-400" />
                      </button>
                    </div>
                    <div className="flex justify-between items-end mt-3">
                      <span className="text-[#5A6BFF] font-bold">
                        {new Intl.NumberFormat('vi-VN', {
                          style: 'currency',
                          currency: 'VND',
                        }).format(item.line_total)}
                      </span>
                      <div className="flex items-center gap-2 bg-[#334155] rounded-full px-2 py-1">
                        <button
                          onClick={() => updateItemQuantity(item.id, item.quantity - 1)}
                          className="w-7 h-7 flex items-center justify-center rounded-full hover:bg-[#4B5563] transition-colors"
                        >
                          <Minus className="w-4 h-4 text-white" />
                        </button>
                        <span className="text-white font-semibold w-6 text-center">
                          {item.quantity}
                        </span>
                        <button
                          onClick={() => updateItemQuantity(item.id, item.quantity + 1)}
                          className="w-7 h-7 flex items-center justify-center rounded-full bg-[#5A6BFF] hover:bg-[#4855e8] transition-colors"
                        >
                          <Plus className="w-4 h-4 text-white" />
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Checkout Footer */}
      {items.length > 0 && (
        <div className="fixed bottom-0 left-0 right-0 bg-[#1E293B] border-t border-[#334155] p-4">
          <div className="max-w-md mx-auto">
            <div className="flex justify-between items-center mb-4">
              <span className="text-gray-400">Subtotal</span>
              <span className="text-2xl font-bold text-white">
                {new Intl.NumberFormat('vi-VN', {
                  style: 'currency',
                  currency: 'VND',
                }).format(subtotal)}
              </span>
            </div>
            <button className="w-full bg-[#5A6BFF] hover:bg-[#4855e8] text-white font-bold py-4 rounded-xl transition-colors flex items-center justify-center gap-2">
              <ShoppingBag className="w-5 h-5" />
              Checkout
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
