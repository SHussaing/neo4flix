import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Cart } from '../models/cart.model';

@Injectable({ providedIn: 'root' })
export class CartService {
  private readonly API_URL = `${environment.apiUrl}/cart`;

  constructor(private http: HttpClient) {}

  getMyCart(): Observable<Cart> {
    return this.http.get<Cart>(this.API_URL);
  }

  addItem(productId: string, quantity = 1): Observable<Cart> {
    return this.http.post<Cart>(`${this.API_URL}/items`, { productId, quantity });
  }

  updateQuantity(productId: string, quantity: number): Observable<Cart> {
    return this.http.patch<Cart>(`${this.API_URL}/items/${productId}`, { quantity });
  }

  removeItem(productId: string): Observable<Cart> {
    return this.http.delete<Cart>(`${this.API_URL}/items/${productId}`);
  }

  clear(): Observable<void> {
    return this.http.delete<void>(this.API_URL);
  }
}

