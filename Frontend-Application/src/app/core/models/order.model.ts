export type OrderStatus = 'PENDING' | 'CONFIRMED' | 'SHIPPED' | 'COMPLETED' | 'CANCELLED';

export interface OrderLine {
  productId: string;
  sellerId?: string | null;
  name: string;
  unitPrice: number;
  quantity: number;
  lineTotal: number;
}

export interface Order {
  id: string;
  buyerId: string;
  status: OrderStatus;
  lines: OrderLine[];
  totalAmount: number;
  shippingAddress: string;
  paymentMethod: string;
  createdAt: string;
  updatedAt: string;
}

export interface SellerOrderLineView {
  orderId: string;
  status: OrderStatus;
  createdAt: string;
  buyerId: string;
  productId: string;
  name: string;
  unitPrice: number;
  quantity: number;
  lineTotal: number;
}

export interface BuyerAnalytics {
  totalOrders: number;
  totalSpent: number;
  topProducts: { productId: string; name: string; units: number; spent: number }[];
}

export interface SellerAnalytics {
  totalOrders: number;
  totalRevenue: number;
  topProducts: { productId: string; name: string; units: number; revenue: number }[];
}

