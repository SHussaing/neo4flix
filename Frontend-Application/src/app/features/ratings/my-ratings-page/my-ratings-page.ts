import { Component, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { RatingApi } from '../../../core/services/rating-api';
@Component({
  standalone: true,
  selector: 'app-my-ratings-page',
  imports: [CommonModule, RouterLink],
  templateUrl: './my-ratings-page.html',
  styleUrl: './my-ratings-page.css'
})
export class MyRatingsPage {
  private readonly ratingApi = inject(RatingApi);
  readonly ratings = this.ratingApi.myRatings;
  readonly loading = this.ratingApi.loading;
  readonly error = this.ratingApi.error;
  readonly hasRatings = computed(() => (this.ratings()?.length ?? 0) > 0);
  constructor() {
    this.ratingApi.loadMyRatings();
  }
}