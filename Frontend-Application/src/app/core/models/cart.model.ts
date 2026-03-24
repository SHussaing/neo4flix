export interface CartItem {
  productId: string;
  sellerId?: string | null;
  name: string;
  unitPrice: number;
  imageUrl?: string | null;
  quantity: number;
  lineTotal: number;

  // Maximum available stock for this product (from server).
  availableQuantity?: number | null;
}

export interface Cart {
  id: string;
  items: CartItem[];
  totalItems: number;
  subtotal: number;
}
