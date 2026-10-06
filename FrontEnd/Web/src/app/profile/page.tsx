'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useAuthStore } from '@/stores/authStore';
import { useLogout } from '@/services/hooks/useAuth';
import { User, Phone, Mail, Award, ChevronRight, LogOut, Settings, Bell, HelpCircle, Shield } from 'lucide-react';
import type { LucideIcon } from 'lucide-react';

const tierColors: Record<string, { bg: string; text: string; border: string }> = {
  bronze: { bg: 'bg-amber-900/30', text: 'text-amber-400', border: 'border-amber-600' },
  silver: { bg: 'bg-slate-400/20', text: 'text-slate-300', border: 'border-slate-400' },
  gold: { bg: 'bg-yellow-500/20', text: 'text-yellow-400', border: 'border-yellow-500' },
  platinum: { bg: 'bg-purple-500/20', text: 'text-purple-400', border: 'border-purple-500' },
};

const tierIcons: Record<string, string> = {
  bronze: '🥉',
  silver: '🥈',
  gold: '🥇',
  platinum: '💎',
};

function MenuItem({
  icon: Icon,
  label,
  value,
  onClick,
}: {
  icon: LucideIcon;
  label: string;
  value?: string;
  onClick?: () => void;
}) {
  return (
    <button
      onClick={onClick}
      className="w-full flex items-center gap-4 p-4 bg-[#1E293B] rounded-xl hover:bg-[#334155] transition-colors"
    >
      <div className="w-10 h-10 bg-[#334155] rounded-xl flex items-center justify-center">
        <Icon className="w-5 h-5 text-[#5A6BFF]" />
      </div>
      <div className="flex-1 text-left">
        <p className="text-white font-medium">{label}</p>
        {value && <p className="text-gray-400 text-sm">{value}</p>}
      </div>
      <ChevronRight className="w-5 h-5 text-gray-500" />
    </button>
  );
}

export default function ProfilePage() {
  const router = useRouter();
  const { isAuthenticated, user, logout } = useAuthStore();
  const logoutMutation = useLogout();

  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/login');
    }
  }, [isAuthenticated, router]);

  const handleLogout = () => {
    logoutMutation();
    router.push('/login');
  };

  if (!isAuthenticated || !user) {
    return null;
  }

  const tierStyle = tierColors[user.tier] || tierColors.bronze;
  const tierIcon = tierIcons[user.tier] || '🥉';

  return (
    <div className="px-4 py-4">
      <h1 className="text-2xl font-bold text-white mb-6">Profile</h1>

      {/* User Card */}
      <div className="bg-[#1E293B] rounded-2xl p-5 mb-6">
        <div className="flex items-center gap-4 mb-4">
          <div className="w-16 h-16 bg-gradient-to-br from-[#5A6BFF] to-[#8B5CF6] rounded-2xl flex items-center justify-center">
            <span className="text-3xl">👤</span>
          </div>
          <div className="flex-1">
            <h2 className="text-xl font-bold text-white">
              {user.name || user.phone || 'User'}
            </h2>
            <p className="text-gray-400 text-sm">{user.email}</p>
          </div>
          <div className={`px-3 py-1.5 rounded-full border ${tierStyle.bg} ${tierStyle.text} ${tierStyle.border}`}>
            <span className="text-sm">{tierIcon} {user.tier.charAt(0).toUpperCase() + user.tier.slice(1)}</span>
          </div>
        </div>

        {/* Stats */}
        <div className="grid grid-cols-3 gap-3 pt-4 border-t border-[#334155]">
          <div className="text-center">
            <p className="text-2xl font-bold text-white">12</p>
            <p className="text-gray-400 text-xs">Orders</p>
          </div>
          <div className="text-center border-x border-[#334155]">
            <p className="text-2xl font-bold text-white">5</p>
            <p className="text-gray-400 text-xs">Favorites</p>
          </div>
          <div className="text-center">
            <p className="text-2xl font-bold text-white">150K</p>
            <p className="text-gray-400 text-xs">Points</p>
          </div>
        </div>
      </div>

      {/* Account Info */}
      <div className="mb-6">
        <h3 className="text-gray-400 text-sm font-medium mb-3 uppercase tracking-wider">Account</h3>
        <div className="space-y-2">
          <MenuItem icon={Phone} label="Phone Number" value={user.phone} />
          <MenuItem icon={Mail} label="Email" value={user.email} />
          <MenuItem icon={Award} label="Membership" value={`${tierIcon} ${user.tier}`} />
        </div>
      </div>

      {/* Settings */}
      <div className="mb-6">
        <h3 className="text-gray-400 text-sm font-medium mb-3 uppercase tracking-wider">Settings</h3>
        <div className="space-y-2">
          <MenuItem icon={Bell} label="Notifications" />
          <MenuItem icon={Shield} label="Privacy & Security" />
          <MenuItem icon={Settings} label="App Settings" />
          <MenuItem icon={HelpCircle} label="Help & Support" />
        </div>
      </div>

      {/* Logout */}
      <button
        onClick={handleLogout}
        className="w-full flex items-center justify-center gap-2 p-4 bg-red-500/10 border border-red-500/30 rounded-xl text-red-400 hover:bg-red-500/20 transition-colors"
      >
        <LogOut className="w-5 h-5" />
        <span className="font-medium">Log Out</span>
      </button>

      {/* Version */}
      <p className="text-center text-gray-600 text-xs mt-6">AI Café v1.0.0</p>
    </div>
  );
}
