import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { OrderService } from '../../../core/services/order';
import { SellerOrderLineView, OrderStatus } from '../../../core/models/order.model';

@Component({
  selector: 'app-seller-orders-page',
  imports: [CommonModule, FormsModule],
  templateUrl: './orders-page.html',
  styleUrl: './orders-page.css',
})
export class SellerOrdersPage implements OnInit {
  rows = signal<SellerOrderLineView[]>([]);
  isLoading = signal(true);
  errorMessage = signal<string | null>(null);

  q = '';
  status = '';

  updating = signal<string | null>(null);

  constructor(private orderService: OrderService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.isLoading.set(true);
    this.orderService.listSellerOrders(this.q || undefined, this.status || undefined).subscribe({
      next: (rows) => {
        this.rows.set(rows);
        this.isLoading.set(false);
      },
      error: () => {
        this.errorMessage.set('Failed to load seller orders');
        this.isLoading.set(false);
      }
    });
  }

  search(): void {
    this.load();
  }

  setStatus(orderId: string, status: OrderStatus): void {
    this.updating.set(orderId);
    this.orderService.updateSellerOrderStatus(orderId, status).subscribe({
      next: () => {
        this.updating.set(null);
        this.load();
      },
      error: () => {
        this.updating.set(null);
        this.errorMessage.set('Failed to update status');
      }
    });
  }
}

