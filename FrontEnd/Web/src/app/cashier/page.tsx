'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { useAuthStore } from '@/stores/authStore';

export default function CashierDashboard() {
  const router = useRouter();
  const { user, isAuthenticated } = useAuthStore();

  useEffect(() => {
    if (!isAuthenticated || user?.role !== 'cashier') {
      router.push('/login');
    }
  }, [isAuthenticated, user, router]);

  if (!isAuthenticated || user?.role !== 'cashier') {
    return (
      <div className="min-h-screen bg-[#0B0F19] flex items-center justify-center">
        <div className="text-white">Đang chuyển hướng...</div>
      </div>
    );
  }

  const menuItems = [
    {
      title: 'Tạo đơn hàng',
      icon: '💳',
      href: '/cashier/new-order',
      description: 'Tạo đơn hàng mới tại quầy',
      color: 'from-blue-500 to-blue-600',
    },
    {
      title: 'Thanh toán',
      icon: '💰',
      href: '/cashier/payments',
      description: 'Xử lý thanh toán',
      color: 'from-green-500 to-green-600',
    },
    {
      title: 'Hóa đơn',
      icon: '🧾',
      href: '/cashier/invoices',
      description: 'Quản lý hóa đơn',
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
              <h1 className="text-xl font-bold text-white">Thu ngân</h1>
            </div>
            <div className="flex items-center gap-4">
              <span className="text-gray-400 text-sm">
                xin chào, <span className="text-white font-medium">{user?.name || 'Cashier'}</span>
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
        {/* Today's Summary */}
        <div className="bg-[#1E293B] rounded-xl p-6 border border-[#334155] mb-8">
          <h2 className="text-lg font-semibold text-white mb-4">Tổng kết hôm nay</h2>
          <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
            <div className="bg-[#0B0F19] rounded-lg p-4">
              <div className="text-gray-400 text-sm mb-1">Tổng đơn</div>
              <div className="text-2xl font-bold text-white">48</div>
            </div>
            <div className="bg-[#0B0F19] rounded-lg p-4">
              <div className="text-gray-400 text-sm mb-1">Đã thanh toán</div>
              <div className="text-2xl font-bold text-green-400">45</div>
            </div>
            <div className="bg-[#0B0F19] rounded-lg p-4">
              <div className="text-gray-400 text-sm mb-1">Chờ thanh toán</div>
              <div className="text-2xl font-bold text-amber-400">3</div>
            </div>
            <div className="bg-[#0B0F19] rounded-lg p-4">
              <div className="text-gray-400 text-sm mb-1">Doanh thu</div>
              <div className="text-2xl font-bold text-green-400">₫4.2M</div>
            </div>
          </div>
        </div>

        {/* Quick Actions */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-8">
          <Link
            href="/cashier/new-order"
            className="col-span-2 bg-gradient-to-r from-[#5A6BFF] to-[#8B5CF6] rounded-xl p-8 flex items-center justify-between group hover:scale-[1.02] transition-transform"
          >
            <div>
              <h3 className="text-white text-xl font-bold mb-2">Tạo đơn hàng mới</h3>
              <p className="text-white/70">Nhấn để bắt đầu tạo đơn hàng</p>
            </div>
            <div className="w-16 h-16 bg-white/20 rounded-2xl flex items-center justify-center text-3xl group-hover:scale-110 transition-transform">
              ➕
            </div>
          </Link>

          <div className="bg-[#1E293B] rounded-xl p-6 border border-[#334155]">
            <h3 className="text-white font-semibold mb-4">Thanh toán nhanh</h3>
            <div className="space-y-3">
              <button className="w-full py-3 bg-green-500 hover:bg-green-600 text-white rounded-lg font-medium transition-colors">
                💵 Tiền mặt
              </button>
              <button className="w-full py-3 bg-blue-500 hover:bg-blue-600 text-white rounded-lg font-medium transition-colors">
                📱 QR Code
              </button>
              <button className="w-full py-3 bg-purple-500 hover:bg-purple-600 text-white rounded-lg font-medium transition-colors">
                💳 Thẻ
              </button>
            </div>
          </div>
        </div>

        {/* Recent Transactions */}
        <div className="bg-[#1E293B] rounded-xl p-6 border border-[#334155] mb-8">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-lg font-semibold text-white">Giao dịch gần đây</h2>
            <Link href="/cashier/transactions" className="text-[#5A6BFF] text-sm hover:underline">
              Xem tất cả →
            </Link>
          </div>
          <div className="space-y-3">
            {[
              { id: 'TXN001', method: 'Tiền mặt', amount: '₫125,000', status: 'success', time: '2 phút trước' },
              { id: 'TXN002', method: 'QR Code', amount: '₫89,000', status: 'success', time: '5 phút trước' },
              { id: 'TXN003', method: 'Thẻ', amount: '₫210,000', status: 'pending', time: '8 phút trước' },
            ].map((tx) => (
              <div key={tx.id} className="flex items-center justify-between p-3 bg-[#0B0F19] rounded-lg">
                <div className="flex items-center gap-3">
                  <div className={`w-10 h-10 rounded-lg flex items-center justify-center ${
                    tx.method === 'Tiền mặt' ? 'bg-green-500/20 text-green-400' :
                    tx.method === 'QR Code' ? 'bg-blue-500/20 text-blue-400' :
                    'bg-purple-500/20 text-purple-400'
                  }`}>
                    {tx.method === 'Tiền mặt' ? '💵' : tx.method === 'QR Code' ? '📱' : '💳'}
                  </div>
                  <div>
                    <div className="text-white font-medium">{tx.id}</div>
                    <div className="text-gray-400 text-sm">{tx.method} • {tx.time}</div>
                  </div>
                </div>
                <div className="text-right">
                  <div className="text-white font-medium">{tx.amount}</div>
                  <div className={`text-sm ${tx.status === 'success' ? 'text-green-400' : 'text-amber-400'}`}>
                    {tx.status === 'success' ? '✅ Thành công' : '⏳ Đang xử lý'}
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Menu Grid */}
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
