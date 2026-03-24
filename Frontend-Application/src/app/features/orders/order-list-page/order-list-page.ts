import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { OrderService } from '../../../core/services/order';
import { Order } from '../../../core/models/order.model';

@Component({
  selector: 'app-order-list-page',
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './order-list-page.html',
  styleUrl: './order-list-page.css',
})
export class OrderListPage implements OnInit {
  orders = signal<Order[]>([]);
  isLoading = signal(true);
  errorMessage = signal<string | null>(null);

  q = '';
  status = '';

  constructor(private orderService: OrderService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.isLoading.set(true);
    this.orderService.listMyOrders(this.q || undefined, this.status || undefined).subscribe({
      next: (orders) => {
        this.orders.set(orders);
        this.isLoading.set(false);
      },
      error: () => {
        this.errorMessage.set('Failed to load orders');
        this.isLoading.set(false);
      }
    });
  }

  search(): void {
    this.load();
  }
}

