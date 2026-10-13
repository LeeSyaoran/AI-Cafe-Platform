'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { useAuthStore } from '@/stores/authStore';

export default function StaffDashboard() {
  const router = useRouter();
  const { user, isAuthenticated } = useAuthStore();

  useEffect(() => {
    if (!isAuthenticated || user?.role !== 'staff') {
      router.push('/login');
    }
  }, [isAuthenticated, user, router]);

  if (!isAuthenticated || user?.role !== 'staff') {
    return (
      <div className="min-h-screen bg-[#0B0F19] flex items-center justify-center">
        <div className="text-white">Đang chuyển hướng...</div>
      </div>
    );
  }

  const menuItems = [
    {
      title: 'Đơn hàng',
      icon: '📦',
      href: '/staff/orders',
      description: 'Xem và xử lý đơn hàng',
      color: 'from-blue-500 to-blue-600',
    },
    {
      title: 'Chấm công',
      icon: '⏰',
      href: '/staff/attendance',
      description: 'Check in/out ca làm việc',
      color: 'from-green-500 to-green-600',
    },
    {
      title: 'Sản phẩm',
      icon: '☕',
      href: '/staff/products',
      description: 'Xem menu quán',
      color: 'from-amber-500 to-amber-600',
    },
  ];

  return (
    <div className="min-h-screen bg-[#0B0F19]">
      {/* Header */}
      <header className="bg-[#1E293B] border-b border-[#334155]">
        <div className="max-w-7xl mx-auto px-6 py-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-4">
              <Link href="/" className="text-gray-400 hover:text-white transition-colors">
                ← Về trang chủ
              </Link>
              <h1 className="text-xl font-bold text-white">Nhân viên</h1>
            </div>
            <div className="flex items-center gap-4">
              <span className="text-gray-400 text-sm">
                xin chào, <span className="text-white font-medium">{user?.name || 'Staff'}</span>
              </span>
              <button
                onClick={() => {
                  useAuthStore.getState().logout();
                  router.push('/login');
                }}
                className="px-4 py-2 bg-red-500/10 text-red-400 hover:bg-red-500/20 rounded-lg text-sm transition-colors"
              >
                Đăng xuất
              </button>
            </div>
          </div>
        </div>
      </header>

      {/* Content */}
      <main className="max-w-7xl mx-auto px-6 py-8">
        {/* Shift Status */}
        <div className="bg-[#1E293B] rounded-xl p-6 border border-[#334155] mb-8">
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-lg font-semibold text-white mb-1">Ca làm việc hiện tại</h2>
              <p className="text-gray-400 text-sm">Ca sáng • 08:00 - 16:00</p>
            </div>
            <div className="flex items-center gap-4">
              <div className="text-right">
                <div className="text-green-400 font-semibold">Đang trong ca</div>
                <div className="text-gray-400 text-sm">5 giờ 23 phút</div>
              </div>
              <div className="w-12 h-12 bg-green-500/20 rounded-full flex items-center justify-center">
                <span className="text-2xl">✅</span>
              </div>
            </div>
          </div>
        </div>

        {/* Stats */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-8">
          <div className="bg-[#1E293B] rounded-xl p-6 border border-[#334155]">
            <div className="text-gray-400 text-sm mb-1">Đơn đã xử lý hôm nay</div>
            <div className="text-3xl font-bold text-white">24</div>
          </div>
          <div className="bg-[#1E293B] rounded-xl p-6 border border-[#334155]">
            <div className="text-gray-400 text-sm mb-1">Đơn đang chờ</div>
            <div className="text-3xl font-bold text-amber-400">3</div>
          </div>
          <div className="bg-[#1E293B] rounded-xl p-6 border border-[#334155]">
            <div className="text-gray-400 text-sm mb-1">Xếp hạng hôm nay</div>
            <div className="text-3xl font-bold text-purple-400">#2</div>
          </div>
        </div>

        {/* Orders Queue */}
        <div className="bg-[#1E293B] rounded-xl p-6 border border-[#334155] mb-8">
          <h2 className="text-lg font-semibold text-white mb-4">Đơn hàng chờ xử lý</h2>
          <div className="space-y-3">
            {[
              { id: 'ORD001', items: 3, total: '₫125,000', time: '2 phút', priority: 'normal' },
              { id: 'ORD002', items: 1, total: '₫45,000', time: '5 phút', priority: 'urgent' },
              { id: 'ORD003', items: 5, total: '₫320,000', time: '8 phút', priority: 'normal' },
            ].map((order, index) => (
              <div
                key={order.id}
                className={`flex items-center justify-between p-4 rounded-lg ${
                  order.priority === 'urgent' ? 'bg-red-500/10 border border-red-500/30' : 'bg-[#0B0F19]'
                }`}
              >
                <div className="flex items-center gap-4">
                  <div className={`w-10 h-10 rounded-lg flex items-center justify-center text-white font-bold ${
                    index === 0 ? 'bg-[#5A6BFF]' : 'bg-[#334155]'
                  }`}>
                    {index + 1}
                  </div>
                  <div>
                    <div className="text-white font-medium">{order.id}</div>
                    <div className="text-gray-400 text-sm">{order.items} món • {order.time} trước</div>
                  </div>
                </div>
                <div className="flex items-center gap-4">
                  <div className="text-right">
                    <div className="text-white font-medium">{order.total}</div>
                    {order.priority === 'urgent' && (
                      <div className="text-red-400 text-xs">⚡ Ưu tiên</div>
                    )}
                  </div>
                  <button className="px-4 py-2 bg-[#5A6BFF] hover:bg-[#4855e8] text-white rounded-lg text-sm transition-colors">
                    Nhận
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Quick Actions */}
        <h2 className="text-lg font-semibold text-white mb-4">Thao tác</h2>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          {menuItems.map((item) => (
            <Link
              key={item.href}
              href={item.href}
              className="group bg-[#1E293B] rounded-xl p-6 border border-[#334155] hover:border-[#5A6BFF] transition-all hover:scale-[1.02]"
            >
              <div className={`w-12 h-12 rounded-xl bg-gradient-to-br ${item.color} flex items-center justify-center text-2xl mb-4`}>
                {item.icon}
              </div>
              <h3 className="text-white font-semibold mb-1 group-hover:text-[#5A6BFF] transition-colors">
                {item.title}
              </h3>
              <p className="text-gray-400 text-sm">{item.description}</p>
            </Link>
          ))}
        </div>
      </main>
    </div>
  );
}
