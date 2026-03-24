import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ProductService } from '../../../core/services/product';
import { MediaService } from '../../../core/services/media';
import { Product } from '../../../core/models/product.model';
import { CartService } from '../../../core/services/cart';
import { CartItem } from '../../../core/models/cart.model';
import { Auth } from '../../../core/services/auth';

@Component({
  selector: 'app-product-detail',
  imports: [CommonModule, RouterLink],
  templateUrl: './product-detail.html',
  styleUrl: './product-detail.css',
})
export class ProductDetail implements OnInit {
  product = signal<Product | null>(null);
  isLoading = signal(true);
  errorMessage = signal<string | null>(null);
  selectedImageIndex = signal(0);

  addToCartLoading = signal(false);
  addToCartMessage = signal<string | null>(null);
  cartItems = signal<CartItem[]>([]);

  constructor(
    private productService: ProductService,
    private mediaService: MediaService,
    private route: ActivatedRoute,
    private cartService: CartService,
    public authService: Auth,
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadProduct(id);
    }
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

  get cartQuantity(): number {
    const p = this.product();
    if (!p) return 0;
    return this.cartItems().find(i => i.productId === p.id)?.quantity ?? 0;
  }

  get isCartFull(): boolean {
    const p = this.product();
    return !!p && this.cartQuantity >= p.quantity;
  }

  loadProduct(id: string): void {
    this.isLoading.set(true);
    this.productService.getProductById(id).subscribe({
      next: (product) => {
        this.product.set(product);
        this.isLoading.set(false);
      },
      error: () => {
        this.errorMessage.set('Failed to load product details');
        this.isLoading.set(false);
      }
    });
  }

  addToCart(): void {
    const p = this.product();
    if (!p) return;

    if (this.cartQuantity >= p.quantity) {
      this.addToCartMessage.set('Not enough stock');
      setTimeout(() => this.addToCartMessage.set(null), 2500);
      return;
    }

    this.addToCartLoading.set(true);
    this.addToCartMessage.set(null);

    this.cartService.addItem(p.id, 1).subscribe({
      next: (cart) => {
        this.cartItems.set(cart.items);
        this.addToCartLoading.set(false);
        this.addToCartMessage.set('Added to cart');
        setTimeout(() => this.addToCartMessage.set(null), 2000);
      },
      error: () => {
        this.addToCartLoading.set(false);
        this.addToCartMessage.set('Login required to add to cart');
        setTimeout(() => this.addToCartMessage.set(null), 2500);
      }
    });
  }

  getImageUrl(imageId: string): string {
    return this.mediaService.getImageUrl(imageId);
  }

  selectImage(index: number): void {
    this.selectedImageIndex.set(index);
  }
}

