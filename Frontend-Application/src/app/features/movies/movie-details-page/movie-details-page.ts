import { Component, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MovieApi } from '../../../core/services/movie-api';
import { RatingApi } from '../../../core/services/rating-api';
import { Auth } from '../../../core/services/auth';

@Component({
  standalone: true,
  selector: 'app-movie-details-page',
  imports: [CommonModule, RouterLink],
  templateUrl: './movie-details-page.html',
  styleUrl: './movie-details-page.css'
})
export class MovieDetailsPage {
  private readonly route = inject(ActivatedRoute);
  private readonly movieApi = inject(MovieApi);
  private readonly ratingApi = inject(RatingApi);
  readonly auth = inject(Auth);

  readonly movieId = computed(() => this.route.snapshot.paramMap.get('id') ?? '');
  readonly movie = this.movieApi.selectedMovie;
  readonly loading = computed(() => this.movieApi.loading() || this.ratingApi.loading());
  readonly error = computed(() => this.movieApi.error() || this.ratingApi.error());

  readonly myRating = this.ratingApi.myRating;
  readonly stars = signal<number>(0);
  readonly savedMessage = signal<string | null>(null);

  constructor() {
    const id = this.movieId();
    if (id) {
      this.movieApi.loadDetailsById(id);
      if (this.auth.isAuthenticated()) {
        this.ratingApi.loadMyRating(id);
      }
    }
  }

  setStars(n: number) {
    this.stars.set(n);
    this.savedMessage.set(null);
  }

  submitRating() {
    const id = this.movieId();
    const stars = this.stars();
    if (!id || stars < 1 || stars > 10) return;

    this.ratingApi.rate(id, stars);
    this.savedMessage.set('Rating saved');
  }

  addToWatchlist() {
    const id = this.movieId();
    if (!id) return;

    this.ratingApi.addToWatchlist(id).subscribe({
      next: () => this.savedMessage.set('Added to watchlist'),
      error: () => this.savedMessage.set('Failed to add to watchlist')
    });
  }
}
