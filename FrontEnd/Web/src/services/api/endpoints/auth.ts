// ============================================
// Auth API - OTP-based authentication
// ============================================

import { api, setTokens, clearTokens } from './client';
import type {
  ApiResponse,
  AuthTokens,
  UserProfile,
  OTPRequest,
  OTPVerifyRequest,
} from '../../types';

// Request OTP (gửi mã về phone)
export async function sendOTP(data: OTPRequest): Promise<void> {
  await api.post<void>('/v1/auth/send-otp', data);
}

// Verify OTP và nhận tokens
export async function verifyOTP(data: OTPVerifyRequest): Promise<AuthTokens> {
  const tokens = await api.post<AuthTokens>('/v1/auth/verify-otp', data);
  setTokens(tokens.accessToken, tokens.refreshToken);
  return tokens;
}

// Refresh token
export async function refreshAuth(): Promise<AuthTokens> {
  const tokens = await api.post<AuthTokens>('/v1/auth/refresh');
  setTokens(tokens.accessToken, tokens.refreshToken);
  return tokens;
}

// Get current user profile
export async function getProfile(): Promise<UserProfile> {
  return api.get<UserProfile>('/v1/me', true);
}

// Update profile
export async function updateProfile(data: Partial<UserProfile>): Promise<UserProfile> {
  return api.put<UserProfile>('/v1/me', data, true);
}

// Logout
export async function logout(): Promise<void> {
  try {
    await api.post('/v1/auth/logout', undefined, true);
  } finally {
    clearTokens();
  }
}

// Quick auth check
export function isLoggedIn(): boolean {
  return !!localStorage.getItem('accessToken');
}
