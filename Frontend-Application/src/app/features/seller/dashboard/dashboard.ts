import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ProductService } from '../../../core/services/product';
import { Product } from '../../../core/models/product.model';
import { MediaService } from '../../../core/services/media';

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard implements OnInit {
  products = signal<Product[]>([]);
  isLoading = signal(true);
  errorMessage = signal<string | null>(null);
  deleteLoading = signal<string | null>(null);

  constructor(
    private productService: ProductService,
    private mediaService: MediaService
  ) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    this.isLoading.set(true);
    this.productService.getMyProducts().subscribe({
      next: (products) => {
        this.products.set(products);
        this.isLoading.set(false);
      },
      error: (error) => {
        const errorCode = error.error?.error;
        if (errorCode === 'UNAUTHORIZED') {
          this.errorMessage.set('Please login to view products');
        } else {
          this.errorMessage.set('Failed to load products');
        }
        this.isLoading.set(false);
      }
    });
  }

  deleteProduct(id: string): void {
    if (!confirm('Are you sure you want to delete this product?')) {
      return;
    }

    this.deleteLoading.set(id);
    this.productService.deleteProduct(id).subscribe({
      next: () => {
        this.products.set(this.products().filter(p => p.id !== id));
        this.deleteLoading.set(null);
      },
      error: (error) => {
        const errorCode = error.error?.error;
        if (errorCode === 'FORBIDDEN') {
          this.errorMessage.set('You do not have permission to delete this product');
        } else if (errorCode === 'NOT_FOUND') {
          this.errorMessage.set('Product not found');
        } else {
          this.errorMessage.set('Failed to delete product');
        }
        this.deleteLoading.set(null);
      }
    });
  }

  getImageUrl(imageId: string): string {
    return this.mediaService.getImageUrl(imageId);
  }
}

