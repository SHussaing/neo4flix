import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { BuyerAnalytics, Order, SellerAnalytics, SellerOrderLineView } from '../models/order.model';

@Injectable({ providedIn: 'root' })
export class OrderService {
  private readonly API_URL = `${environment.apiUrl}`;

  constructor(private http: HttpClient) {}

  listMyOrders(q?: string, status?: string): Observable<Order[]> {
    const params: any = {};
    if (q) params.q = q;
    if (status) params.status = status;
    return this.http.get<Order[]>(`${this.API_URL}/orders`, { params });
  }

  getOrder(id: string): Observable<Order> {
    return this.http.get<Order>(`${this.API_URL}/orders/${id}`);
  }

  checkout(shippingAddress: string): Observable<Order> {
    return this.http.post<Order>(`${this.API_URL}/orders/checkout`, {
      shippingAddress,
      paymentMethod: 'PAY_ON_DELIVERY',
    });
  }

  cancelOrder(id: string): Observable<Order> {
    return this.http.post<Order>(`${this.API_URL}/orders/${id}/cancel`, {});
  }

  redoOrder(id: string): Observable<Order> {
    return this.http.post<Order>(`${this.API_URL}/orders/${id}/redo`, {});
  }

  listSellerOrders(q?: string, status?: string): Observable<SellerOrderLineView[]> {
    const params: any = {};
    if (q) params.q = q;
    if (status) params.status = status;
    return this.http.get<SellerOrderLineView[]>(`${this.API_URL}/seller/orders`, { params });
  }

  updateSellerOrderStatus(orderId: string, status: string): Observable<Order> {
    return this.http.patch<Order>(`${this.API_URL}/seller/orders/${orderId}/status`, { status });
  }

  buyerAnalytics(): Observable<BuyerAnalytics> {
    return this.http.get<BuyerAnalytics>(`${this.API_URL}/analytics/buyer`);
  }

  sellerAnalytics(): Observable<SellerAnalytics> {
    return this.http.get<SellerAnalytics>(`${this.API_URL}/analytics/seller`);
  }
}

