import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CartService } from '../../../core/services/cart';
import { OrderService } from '../../../core/services/order';
import { Cart } from '../../../core/models/cart.model';

@Component({
  selector: 'app-checkout-page',
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './checkout-page.html',
  styleUrl: './checkout-page.css',
})
export class CheckoutPage implements OnInit {
  cart = signal<Cart | null>(null);
  isLoading = signal(true);
  isSubmitting = signal(false);
  errorMessage = signal<string | null>(null);

  form;

  constructor(
    private fb: FormBuilder,
    private cartService: CartService,
    private orderService: OrderService,
    private router: Router
  ) {
    this.form = this.fb.group({
      shippingAddress: ['', [Validators.required, Validators.minLength(10)]],
    });
  }

  ngOnInit(): void {
    this.cartService.getMyCart().subscribe({
      next: (cart) => {
        this.cart.set(cart);
        this.isLoading.set(false);
      },
      error: () => {
        this.errorMessage.set('Failed to load cart');
        this.isLoading.set(false);
      }
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);
    this.errorMessage.set(null);

    this.orderService.checkout(this.form.value.shippingAddress!).subscribe({
      next: (order) => {
        this.isSubmitting.set(false);
        this.router.navigate(['/orders', order.id]);
      },
      error: (err) => {
        this.isSubmitting.set(false);
        const code = err?.error?.error;
        if (code === 'CART_EMPTY') this.errorMessage.set('Your cart is empty');
        else this.errorMessage.set('Checkout failed');
      }
    });
  }

  get shippingAddress() {
    return this.form.get('shippingAddress');
  }
}
