import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { OrderService } from '../../../core/services/order';
import { Order } from '../../../core/models/order.model';

@Component({
  selector: 'app-order-detail-page',
  imports: [CommonModule, RouterLink],
  templateUrl: './order-detail-page.html',
  styleUrl: './order-detail-page.css',
})
export class OrderDetailPage implements OnInit {
  order = signal<Order | null>(null);
  isLoading = signal(true);
  errorMessage = signal<string | null>(null);
  cancelling = signal(false);
  redoing = signal(false);

  constructor(private route: ActivatedRoute, private orderService: OrderService) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id')!;
    this.orderService.getOrder(id).subscribe({
      next: (order) => {
        this.order.set(order);
        this.isLoading.set(false);
      },
      error: () => {
        this.errorMessage.set('Failed to load order');
        this.isLoading.set(false);
      }
    });
  }

  cancel(): void {
    const o = this.order();
    if (!o) return;
    if (!confirm('Cancel this order?')) return;

    this.cancelling.set(true);
    this.orderService.cancelOrder(o.id).subscribe({
      next: (updated) => {
        this.order.set(updated);
        this.cancelling.set(false);
      },
      error: () => {
        this.errorMessage.set('Failed to cancel order');
        this.cancelling.set(false);
      }
    });
  }

  redo(): void {
    const o = this.order();
    if (!o) return;
    if (!confirm('Redo this order? A new order will be created.')) return;

    this.redoing.set(true);
    this.orderService.redoOrder(o.id).subscribe({
      next: (created) => {
        // Keep user on the page, but swap to the newly created order.
        this.order.set(created);
        this.redoing.set(false);
      },
      error: () => {
        this.errorMessage.set('Failed to redo order');
        this.redoing.set(false);
      },
    });
  }

  canCancel(): boolean {
    const s = this.order()?.status;
    return !!s && s !== 'CANCELLED' && s !== 'COMPLETED';
  }

  statusStep(status: string): number {
    switch (status) {
      case 'CONFIRMED': return 1;
      case 'SHIPPED': return 2;
      case 'COMPLETED': return 3;
      case 'CANCELLED': return -1;
      default: return 0;
    }
  }
}

