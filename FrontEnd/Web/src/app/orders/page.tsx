'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useAuthStore } from '@/stores/authStore';
import { getOrders } from '@/services/api/endpoints/order';
import { useQuery } from '@tanstack/react-query';
import { Clock, CheckCircle, XCircle, ChefHat, Bike, Store } from 'lucide-react';
import type { LucideIcon } from 'lucide-react';
import type { Order } from '@/types';

const statusConfig: Record<string, { label: string; color: string; bgColor: string; icon: LucideIcon }> = {
  pending: { label: 'Pending', color: 'text-yellow-400', bgColor: 'bg-yellow-400/10', icon: Clock },
  confirmed: { label: 'Confirmed', color: 'text-blue-400', bgColor: 'bg-blue-400/10', icon: CheckCircle },
  preparing: { label: 'Preparing', color: 'text-orange-400', bgColor: 'bg-orange-400/10', icon: ChefHat },
  ready: { label: 'Ready', color: 'text-green-400', bgColor: 'bg-green-400/10', icon: CheckCircle },
  delivering: { label: 'Delivering', color: 'text-purple-400', bgColor: 'bg-purple-400/10', icon: Bike },
  completed: { label: 'Completed', color: 'text-green-400', bgColor: 'bg-green-400/10', icon: CheckCircle },
  cancelled: { label: 'Cancelled', color: 'text-red-400', bgColor: 'bg-red-400/10', icon: XCircle },
};

function OrderCard({ order }: { order: Order }) {
  const status = statusConfig[order.status] || statusConfig.pending;
  const StatusIcon = status.icon;

  const formatDate = (dateStr: string) => {
    const date = new Date(dateStr);
    return date.toLocaleDateString('vi-VN', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  return (
    <div className="bg-[#1E293B] rounded-2xl p-4 mb-3">
      <div className="flex items-center justify-between mb-3">
        <div>
          <span className="text-white font-semibold">#{order.order_number}</span>
          <span className="text-gray-400 text-sm ml-2">
            {order.order_type === 'pickup' ? '🛍️ Pickup' : order.order_type === 'delivery' ? '🚚 Delivery' : '🍽️ Dine In'}
          </span>
        </div>
        <div className={`flex items-center gap-1.5 px-3 py-1 rounded-full ${status.bgColor}`}>
          <StatusIcon className={`w-4 h-4 ${status.color}`} />
          <span className={`text-xs font-medium ${status.color}`}>{status.label}</span>
        </div>
      </div>

      <div className="flex items-center gap-2 text-gray-400 text-sm mb-3">
        <Store className="w-4 h-4" />
        <span>AI Café Downtown</span>
      </div>

      <div className="border-t border-[#334155] pt-3">
        <div className="flex justify-between text-sm">
          <span className="text-gray-400">
            {order.items?.length || 0} item{order.items?.length !== 1 ? 's' : ''}
          </span>
          <span className="text-white font-semibold">
            {new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(order.total_amount)}
          </span>
        </div>
        <p className="text-gray-500 text-xs mt-1">{formatDate(order.created_at)}</p>
      </div>
    </div>
  );
}

function OrdersSkeleton() {
  return (
    <div className="space-y-3">
      {[1, 2, 3].map((i) => (
        <div key={i} className="bg-[#1E293B] rounded-2xl p-4 animate-pulse">
          <div className="flex justify-between mb-3">
            <div className="h-5 bg-[#334155] rounded w-24" />
            <div className="h-6 bg-[#334155] rounded-full w-20" />
          </div>
          <div className="h-4 bg-[#334155] rounded w-32 mb-3" />
          <div className="border-t border-[#334155] pt-3">
            <div className="flex justify-between">
              <div className="h-4 bg-[#334155] rounded w-16" />
              <div className="h-4 bg-[#334155] rounded w-24" />
            </div>
          </div>
        </div>
      ))}
    </div>
  );
}

export default function OrdersPage() {
  const router = useRouter();
  const { isAuthenticated } = useAuthStore();

  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/login');
    }
  }, [isAuthenticated, router]);

  const { data: ordersData, isLoading } = useQuery({
    queryKey: ['orders'],
    queryFn: () => getOrders({ limit: 20 }),
    enabled: isAuthenticated,
  });

  if (!isAuthenticated) {
    return null;
  }

  return (
    <div className="px-4 py-4">
      <h1 className="text-2xl font-bold text-white mb-6">My Orders</h1>

      {isLoading ? (
        <OrdersSkeleton />
      ) : ordersData?.data && ordersData.data.length > 0 ? (
        <div>
          {ordersData.data.map((order: Order) => (
            <OrderCard key={order.id} order={order} />
          ))}
        </div>
      ) : (
        <div className="text-center py-12">
          <div className="w-16 h-16 bg-[#1E293B] rounded-full mx-auto mb-4 flex items-center justify-center">
            <Store className="w-8 h-8 text-gray-500" />
          </div>
          <h3 className="text-white font-semibold mb-2">No orders yet</h3>
          <p className="text-gray-400 text-sm mb-6">Start ordering to see your order history</p>
          <button
            onClick={() => router.push('/')}
            className="bg-[#5A6BFF] hover:bg-[#4855e8] text-white font-semibold px-6 py-3 rounded-xl transition-colors"
          >
            Browse Menu
          </button>
        </div>
      )}
    </div>
  );
}
