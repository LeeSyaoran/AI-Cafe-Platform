import { create } from 'zustand';
import type { CartItem, Product } from '@/types';

interface CartState {
  items: CartItem[];
  itemCount: number;
  subtotal: number;
  isOpen: boolean;

  // Actions
  setItems: (items: CartItem[]) => void;
  addItem: (product: Product, quantity?: number) => void;
  updateItemQuantity: (itemId: string, quantity: number) => void;
  removeItem: (itemId: string) => void;
  clearCart: () => void;
  toggleCart: () => void;
  setCartOpen: (open: boolean) => void;
}

export const useCartStore = create<CartState>((set) => ({
  items: [],
  itemCount: 0,
  subtotal: 0,
  isOpen: false,

  setItems: (items) =>
    set({
      items,
      itemCount: items.reduce((sum, item) => sum + item.quantity, 0),
      subtotal: items.reduce((sum, item) => sum + item.line_total, 0),
    }),

  addItem: (product, quantity = 1) =>
    set((state) => {
      const existingIndex = state.items.findIndex(
        (item) => item.product_id === product.id
      );

      let newItems: CartItem[];
      if (existingIndex >= 0) {
        newItems = state.items.map((item, index) =>
          index === existingIndex
            ? {
                ...item,
                quantity: item.quantity + quantity,
                line_total: (item.quantity + quantity) * item.unit_price,
              }
            : item
        );
      } else {
        const newItem: CartItem = {
          id: `temp-${Date.now()}`,
          product_id: product.id,
          product,
          quantity,
          unit_price: product.price,
          line_total: product.price * quantity,
        };
        newItems = [...state.items, newItem];
      }

      return {
        items: newItems,
        itemCount: newItems.reduce((sum, item) => sum + item.quantity, 0),
        subtotal: newItems.reduce((sum, item) => sum + item.line_total, 0),
      };
    }),

  updateItemQuantity: (itemId, quantity) =>
    set((state) => {
      if (quantity <= 0) {
        const newItems = state.items.filter((item) => item.id !== itemId);
        return {
          items: newItems,
          itemCount: newItems.reduce((sum, item) => sum + item.quantity, 0),
          subtotal: newItems.reduce((sum, item) => sum + item.line_total, 0),
        };
      }

      const newItems = state.items.map((item) =>
        item.id === itemId
          ? { ...item, quantity, line_total: item.unit_price * quantity }
          : item
      );
      return {
        items: newItems,
        itemCount: newItems.reduce((sum, item) => sum + item.quantity, 0),
        subtotal: newItems.reduce((sum, item) => sum + item.line_total, 0),
      };
    }),

  removeItem: (itemId) =>
    set((state) => {
      const newItems = state.items.filter((item) => item.id !== itemId);
      return {
        items: newItems,
        itemCount: newItems.reduce((sum, item) => sum + item.quantity, 0),
        subtotal: newItems.reduce((sum, item) => sum + item.line_total, 0),
      };
    }),

  clearCart: () =>
    set({
      items: [],
      itemCount: 0,
      subtotal: 0,
    }),

  toggleCart: () => set((state) => ({ isOpen: !state.isOpen })),
  setCartOpen: (open) => set({ isOpen: open }),
}));
