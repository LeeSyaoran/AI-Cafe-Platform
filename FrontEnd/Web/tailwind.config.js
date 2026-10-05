/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    './src/pages/**/*.{js,ts,jsx,tsx,mdx}',
    './src/components/**/*.{js,ts,jsx,tsx,mdx}',
    './src/app/**/*.{js,ts,jsx,tsx,mdx}',
  ],
  theme: {
    extend: {
      colors: {
        // Dark theme backgrounds
        'cafe-bg': '#0B0F19',
        'cafe-bg-secondary': '#0F172A',
        'cafe-card': '#1E293B',
        'cafe-border': '#334155',

        // Primary gradient colors
        'cafe-primary': '#5A6BFF',
        'cafe-primary-dark': '#6366F1',
        'cafe-secondary': '#8B5CF6',

        // Status colors
        'cafe-success': '#10B981',
        'cafe-warning': '#F59E0B',
        'cafe-danger': '#EF4444',

        // Text colors
        'cafe-text': '#FFFFFF',
        'cafe-text-secondary': '#94A3B8',
        'cafe-text-muted': '#64748B',

        // AI Credits
        'cafe-credits': '#818CF8',
        'cafe-credits-bg': '#1E1B4B',

        // Workspace
        'cafe-workspace': '#34D399',
        'cafe-workspace-bg': '#064E3B',

        // Category pills
        'cafe-pill-active': '#5A6BFF',
        'cafe-pill-inactive': '#1E293B',
      },
      fontFamily: {
        sans: ['Inter', '-apple-system', 'BlinkMacSystemFont', 'Segoe UI', 'Roboto', 'sans-serif'],
        mono: ['JetBrains Mono', 'monospace'],
      },
      boxShadow: {
        'cafe-card': '0 8px 12px rgba(0, 0, 0, 0.35)',
        'cafe-kanban': '0 4px 8px rgba(0, 0, 0, 0.3)',
      },
      borderRadius: {
        'cafe': '12px',
        'cafe-lg': '16px',
        'cafe-xl': '20px',
      },
    },
  },
  plugins: [],
}
