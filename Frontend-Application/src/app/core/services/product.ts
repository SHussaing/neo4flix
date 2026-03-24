import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Product, CreateProductRequest, UpdateProductRequest } from '../models/product.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  private readonly API_URL = `${environment.apiUrl}/products`;

  constructor(private http: HttpClient) {}

  getAllProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(this.API_URL);
  }

  searchProducts(filters: {
    q?: string;
    minPrice?: number;
    maxPrice?: number;
    inStock?: boolean;
    sellerId?: string;
    sort?: string;
    page?: number;
    size?: number;
  }): Observable<Product[]> {
    let params = new HttpParams();
    Object.entries(filters || {}).forEach(([key, value]) => {
      if (value === undefined || value === null || value === '') return;
      params = params.set(key, String(value));
    });
    return this.http.get<Product[]>(this.API_URL, { params });
  }

  getProductById(id: string): Observable<Product> {
    return this.http.get<Product>(`${this.API_URL}/${id}`);
  }

  getMyProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.API_URL}/my-products`);
  }

  createProduct(data: CreateProductRequest): Observable<Product> {
    return this.http.post<Product>(this.API_URL, data);
  }

  updateProduct(id: string, data: UpdateProductRequest): Observable<Product> {
    return this.http.put<Product>(`${this.API_URL}/${id}`, data);
  }

  deleteProduct(id: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }
}
