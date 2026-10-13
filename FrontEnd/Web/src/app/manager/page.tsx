'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { useAuthStore } from '@/stores/authStore';

export default function ManagerDashboard() {
  const router = useRouter();
  const { user, isAuthenticated } = useAuthStore();

  useEffect(() => {
    if (!isAuthenticated || user?.role !== 'manager') {
      router.push('/login');
    }
  }, [isAuthenticated, user, router]);

  if (!isAuthenticated || user?.role !== 'manager') {
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
      href: '/manager/orders',
      description: 'Quản lý đơn hàng',
      color: 'from-blue-500 to-blue-600',
    },
    {
      title: 'Nhân viên',
      icon: '👨‍💼',
      href: '/manager/staff',
      description: 'Phân ca và chấm công',
      color: 'from-green-500 to-green-600',
    },
    {
      title: 'Sản phẩm',
      icon: '☕',
      href: '/manager/products',
      description: 'Quản lý menu quán',
      color: 'from-amber-500 to-amber-600',
    },
    {
      title: 'Kho hàng',
      icon: '📦',
      href: '/manager/inventory',
      description: 'Quản lý nguyên liệu',
      color: 'from-orange-500 to-orange-600',
    },
    {
      title: 'Báo cáo',
      icon: '📊',
      href: '/manager/reports',
      description: 'Doanh thu và thống kê',
      color: 'from-purple-500 to-purple-600',
    },
    {
      title: 'Khuyến mãi',
      icon: '🎁',
      href: '/manager/promotions',
      description: 'Tạo khuyến mãi',
      color: 'from-pink-500 to-pink-600',
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
              <h1 className="text-xl font-bold text-white">Quản lý quán</h1>
            </div>
            <div className="flex items-center gap-4">
              <span className="text-gray-400 text-sm">
                xin chào, <span className="text-white font-medium">{user?.name || 'Manager'}</span>
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
        {/* Stats Overview */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-8">
          <div className="bg-[#1E293B] rounded-xl p-6 border border-[#334155]">
            <div className="text-gray-400 text-sm mb-1">Đơn hàng hôm nay</div>
            <div className="text-3xl font-bold text-white">48</div>
            <div className="text-green-400 text-sm mt-1">↑ 12% so với hôm qua</div>
          </div>
          <div className="bg-[#1E293B] rounded-xl p-6 border border-[#334155]">
            <div className="text-gray-400 text-sm mb-1">Đang chuẩn bị</div>
            <div className="text-3xl font-bold text-amber-400">8</div>
          </div>
          <div className="bg-[#1E293B] rounded-xl p-6 border border-[#334155]">
            <div className="text-gray-400 text-sm mb-1">Nhân viên online</div>
            <div className="text-3xl font-bold text-green-400">6/8</div>
          </div>
          <div className="bg-[#1E293B] rounded-xl p-6 border border-[#334155]">
            <div className="text-gray-400 text-sm mb-1">Doanh thu hôm nay</div>
            <div className="text-3xl font-bold text-green-400">₫4.5M</div>
          </div>
        </div>

        {/* Quick Actions */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-8">
          {/* Recent Orders */}
          <div className="lg:col-span-2 bg-[#1E293B] rounded-xl p-6 border border-[#334155]">
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-lg font-semibold text-white">Đơn hàng gần đây</h2>
              <Link href="/manager/orders" className="text-[#5A6BFF] text-sm hover:underline">
                Xem tất cả →
              </Link>
            </div>
            <div className="space-y-3">
              {[
                { id: 'ORD001', customer: 'Nguyễn Văn A', total: '₫125,000', status: 'preparing', time: '2 phút trước' },
                { id: 'ORD002', customer: 'Trần Thị B', total: '₫89,000', status: 'ready', time: '5 phút trước' },
                { id: 'ORD003', customer: 'Lê Văn C', total: '₫210,000', status: 'pending', time: '8 phút trước' },
              ].map((order) => (
                <div key={order.id} className="flex items-center justify-between p-3 bg-[#0B0F19] rounded-lg">
                  <div>
                    <div className="text-white font-medium">{order.id}</div>
                    <div className="text-gray-400 text-sm">{order.customer}</div>
                  </div>
                  <div className="text-right">
                    <div className="text-white font-medium">{order.total}</div>
                    <div className={`text-sm ${
                      order.status === 'ready' ? 'text-green-400' :
                      order.status === 'preparing' ? 'text-amber-400' : 'text-gray-400'
                    }`}>
                      {order.status === 'ready' ? '✅ Sẵn sàng' :
                       order.status === 'preparing' ? '🔄 Đang chuẩn bị' : '⏳ Chờ xác nhận'}
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Quick Actions */}
          <div className="bg-[#1E293B] rounded-xl p-6 border border-[#334155]">
            <h2 className="text-lg font-semibold text-white mb-4">Thao tác nhanh</h2>
            <div className="space-y-3">
              <Link
                href="/manager/orders/new"
                className="flex items-center gap-3 p-3 bg-[#0B0F19] rounded-lg hover:bg-[#1E293B] transition-colors"
              >
                <span className="text-2xl">📋</span>
                <span className="text-white">Tạo đơn hàng mới</span>
              </Link>
              <Link
                href="/manager/staff/schedule"
                className="flex items-center gap-3 p-3 bg-[#0B0F19] rounded-lg hover:bg-[#1E293B] transition-colors"
              >
                <span className="text-2xl">📅</span>
                <span className="text-white">Phân ca hôm nay</span>
              </Link>
              <Link
                href="/manager/inventory"
                className="flex items-center gap-3 p-3 bg-[#0B0F19] rounded-lg hover:bg-[#1E293B] transition-colors"
              >
                <span className="text-2xl">⚠️</span>
                <span className="text-white">Kiểm tra kho</span>
              </Link>
            </div>
          </div>
        </div>

        {/* Menu Grid */}
        <h2 className="text-lg font-semibold text-white mb-4">Quản lý quán</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
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
