import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { OrderService } from '../../../core/services/order';
import { SellerAnalytics } from '../../../core/models/order.model';

@Component({
  selector: 'app-seller-analytics-page',
  imports: [CommonModule, RouterLink],
  templateUrl: './analytics-page.html',
  styleUrl: './analytics-page.css',
})
export class SellerAnalyticsPage implements OnInit {
  data = signal<SellerAnalytics | null>(null);
  isLoading = signal(true);
  errorMessage = signal<string | null>(null);

  constructor(private orderService: OrderService) {}

  ngOnInit(): void {
    this.orderService.sellerAnalytics().subscribe({
      next: (data) => {
        this.data.set(data);
        this.isLoading.set(false);
      },
      error: () => {
        this.errorMessage.set('Failed to load analytics');
        this.isLoading.set(false);
      }
    });
  }
}

