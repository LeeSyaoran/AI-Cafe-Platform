'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { useAuthStore } from '@/stores/authStore';

export default function AdminDashboard() {
  const router = useRouter();
  const { user, isAuthenticated } = useAuthStore();

  useEffect(() => {
    if (!isAuthenticated || user?.role !== 'admin') {
      router.push('/login');
    }
  }, [isAuthenticated, user, router]);

  if (!isAuthenticated || user?.role !== 'admin') {
    return (
      <div className="min-h-screen bg-[#0B0F19] flex items-center justify-center">
        <div className="text-white">Đang chuyển hướng...</div>
      </div>
    );
  }

  const menuItems = [
    {
      title: 'Quản lý quán',
      icon: '🏪',
      href: '/admin/cafes',
      description: 'Thêm, sửa, xóa thông tin quán',
      color: 'from-blue-500 to-blue-600',
    },
    {
      title: 'Quản lý nhân viên',
      icon: '👥',
      href: '/admin/staff',
      description: 'Quản lý tài khoản nhân viên',
      color: 'from-green-500 to-green-600',
    },
    {
      title: 'Quản lý sản phẩm',
      icon: '☕',
      href: '/admin/products',
      description: 'Quản lý menu và sản phẩm',
      color: 'from-amber-500 to-amber-600',
    },
    {
      title: 'Báo cáo doanh thu',
      icon: '📊',
      href: '/admin/reports',
      description: 'Xem báo cáo và thống kê',
      color: 'from-purple-500 to-purple-600',
    },
    {
      title: 'Cài đặt hệ thống',
      icon: '⚙️',
      href: '/admin/settings',
      description: 'Cấu hình hệ thống',
      color: 'from-gray-500 to-gray-600',
    },
    {
      title: 'Quản lý khuyến mãi',
      icon: '🎁',
      href: '/admin/promotions',
      description: 'Tạo và quản lý khuyến mãi',
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
              <h1 className="text-xl font-bold text-white">Admin Dashboard</h1>
            </div>
            <div className="flex items-center gap-4">
              <span className="text-gray-400 text-sm">
                Xin chào, <span className="text-white font-medium">{user?.name || 'Admin'}</span>
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
            <div className="text-gray-400 text-sm mb-1">Tổng quán</div>
            <div className="text-3xl font-bold text-white">12</div>
          </div>
          <div className="bg-[#1E293B] rounded-xl p-6 border border-[#334155]">
            <div className="text-gray-400 text-sm mb-1">Nhân viên</div>
            <div className="text-3xl font-bold text-white">48</div>
          </div>
          <div className="bg-[#1E293B] rounded-xl p-6 border border-[#334155]">
            <div className="text-gray-400 text-sm mb-1">Đơn hàng hôm nay</div>
            <div className="text-3xl font-bold text-white">156</div>
          </div>
          <div className="bg-[#1E293B] rounded-xl p-6 border border-[#334155]">
            <div className="text-gray-400 text-sm mb-1">Doanh thu tháng</div>
            <div className="text-3xl font-bold text-green-400">₫125M</div>
          </div>
        </div>

        {/* Menu Grid */}
        <h2 className="text-lg font-semibold text-white mb-4">Quản trị hệ thống</h2>
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
