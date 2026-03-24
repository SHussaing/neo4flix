import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ProductService } from '../../../core/services/product';
import { Product } from '../../../core/models/product.model';
import { MediaService } from '../../../core/services/media';
import { FormsModule } from '@angular/forms';
import { CartService } from '../../../core/services/cart';
import { CartItem } from '../../../core/models/cart.model';
import { Auth } from '../../../core/services/auth';

@Component({
  selector: 'app-product-list',
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './product-list.html',
  styleUrl: './product-list.css',
})
export class ProductList implements OnInit {
  products = signal<Product[]>([]);
  isLoading = signal(true);
  errorMessage = signal<string | null>(null);

  q = '';
  inStock = false;
  sort = '';

  // ── Price slider ────────────────────────────────────────────
  readonly PRICE_MAX: number = 10000;
  readonly PRICE_STEP: number = 1;

  priceSliderMin: number = 0;
  priceSliderMax: number = 10000;

  getPriceMinPct(): number {
    return (this.priceSliderMin / this.PRICE_MAX) * 100;
  }

  getPriceMaxPct(): number {
    return (this.priceSliderMax / this.PRICE_MAX) * 100;
  }

  onSliderMinChange(event: Event): void {
    const val = Number((event.target as HTMLInputElement).value);
    this.priceSliderMin = Math.min(val, this.priceSliderMax - this.PRICE_STEP);
  }

  onSliderMaxChange(event: Event): void {
    const val = Number((event.target as HTMLInputElement).value);
    this.priceSliderMax = Math.max(val, this.priceSliderMin + this.PRICE_STEP);
  }
  // ────────────────────────────────────────────────────────────

  addToCartLoading = signal<string | null>(null);
  addToCartMessage = signal<string | null>(null);
  cartItems = signal<CartItem[]>([]);

  constructor(
    private productService: ProductService,
    private mediaService: MediaService,
    private cartService: CartService,
    public authService: Auth,
  ) {}

  ngOnInit(): void {
    this.loadProducts();
    if (!this.authService.hasRole('SELLER')) {
      this.loadCart();
    }
  }

  loadCart(): void {
    this.cartService.getMyCart().subscribe({
      next: (cart) => this.cartItems.set(cart.items),
      error: () => {},
    });
  }

  getCartQuantity(productId: string): number {
    return this.cartItems().find(i => i.productId === productId)?.quantity ?? 0;
  }

  loadProducts(): void {
    this.isLoading.set(true);
    this.productService.searchProducts({
      q: this.q || undefined,
      minPrice: this.priceSliderMin > 0 ? this.priceSliderMin : undefined,
      maxPrice: this.priceSliderMax < this.PRICE_MAX ? this.priceSliderMax : undefined,
      inStock: this.inStock ? true : undefined,
      sort: this.sort || undefined,
      page: 0,
      size: 50,
    }).subscribe({
      next: (products) => {
        this.products.set(products);
        this.isLoading.set(false);
      },
      error: () => {
        this.errorMessage.set('Failed to load products');
        this.isLoading.set(false);
      }
    });
  }

  onSearchSubmit(): void {
    this.loadProducts();
  }

  addToCart(productId: string): void {
    const product = this.products().find(p => p.id === productId);
    if (product && this.getCartQuantity(productId) >= product.quantity) {
      this.addToCartMessage.set('Not enough stock');
      setTimeout(() => this.addToCartMessage.set(null), 2500);
      return;
    }

    this.addToCartLoading.set(productId);
    this.addToCartMessage.set(null);
    this.cartService.addItem(productId, 1).subscribe({
      next: (cart) => {
        this.cartItems.set(cart.items);
        this.addToCartLoading.set(null);
        this.addToCartMessage.set('Added to cart');
        setTimeout(() => this.addToCartMessage.set(null), 2000);
      },
      error: () => {
        this.addToCartLoading.set(null);
        this.addToCartMessage.set('Login required to add to cart');
        setTimeout(() => this.addToCartMessage.set(null), 2500);
      }
    });
  }

  getImageUrl(imageId: string): string {
    return this.mediaService.getImageUrl(imageId);
  }
}
