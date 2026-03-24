import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { CartService } from '../../../core/services/cart';
import { Cart, CartItem } from '../../../core/models/cart.model';
import { MediaService } from '../../../core/services/media';

@Component({
  selector: 'app-cart-page',
  imports: [CommonModule, RouterLink],
  templateUrl: './cart-page.html',
  styleUrl: './cart-page.css',
})
export class CartPage implements OnInit {
  cart = signal<Cart | null>(null);
  isLoading = signal(true);
  errorMessage = signal<string | null>(null);

  qtyLoading = signal<string | null>(null);

  constructor(
    private cartService: CartService,
    private mediaService: MediaService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.cartService.getMyCart().subscribe({
      next: (cart) => {
        this.cart.set(cart);
        this.isLoading.set(false);
      },
      error: () => {
        this.errorMessage.set('Failed to load cart');
        this.isLoading.set(false);
      },
    });
  }

  inc(item: CartItem): void {
    if (!this.canInc(item)) return;
    this.update(item.productId, item.quantity + 1, item.availableQuantity ?? undefined);
  }

  dec(productId: string, current: number): void {
    this.update(productId, current - 1);
  }

  canInc(item: CartItem): boolean {
    const max = item.availableQuantity;
    if (max === null || max === undefined) return true;
    return item.quantity < max;
  }

  update(productId: string, quantity: number, max?: number): void {
    const safeMax = (max === null ? undefined : max);
    let q = quantity;
    if (safeMax !== undefined) {
      q = Math.min(q, safeMax);
    }

    this.qtyLoading.set(productId);
    this.errorMessage.set(null);

    this.cartService.updateQuantity(productId, q).subscribe({
      next: (cart) => {
        this.cart.set(cart);
        this.qtyLoading.set(null);
      },
      error: () => {
        this.errorMessage.set('Failed to update cart');
        this.qtyLoading.set(null);
      },
    });
  }

  remove(productId: string): void {
    this.qtyLoading.set(productId);
    this.errorMessage.set(null);

    this.cartService.removeItem(productId).subscribe({
      next: (cart) => {
        this.cart.set(cart);
        this.qtyLoading.set(null);
      },
      error: () => {
        this.errorMessage.set('Failed to remove item');
        this.qtyLoading.set(null);
      },
    });
  }

  clear(): void {
    if (!confirm('Clear cart?')) return;

    this.errorMessage.set(null);
    this.cartService.clear().subscribe({
      next: () => this.load(),
      error: () => this.errorMessage.set('Failed to clear cart'),
    });
  }

  checkout(): void {
    this.router.navigate(['/checkout']);
  }

  getImageUrl(imageIdOrUrl: string | null | undefined): string {
    if (!imageIdOrUrl) return '';
    // Cart stores imageUrl snapshot; this might be an id (media service) OR direct URL.
    return this.mediaService.getImageUrl(imageIdOrUrl);
  }
}
