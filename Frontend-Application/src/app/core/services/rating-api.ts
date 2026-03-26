import { Injectable, signal } from '@angular/core';
import { ApiBase } from './api-base';

export type MyMovieRating = {
  movieId: string;
  stars?: number;
  createdAt?: string;

  // Enriched fields (from /rating/me)
  title?: string;
  releaseYear?: number;
  genre?: string;
};

@Injectable({ providedIn: 'root' })
export class RatingApi extends ApiBase {
  readonly myRating = signal<MyMovieRating | null>(null);
  readonly myRatings = signal<MyMovieRating[] | null>(null);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  loadMyRatings() {
    this.loading.set(true);
    this.error.set(null);

    this.http.get<MyMovieRating[]>(`${this.apiUrl}/rating/me`).subscribe({
      next: (data) => {
        this.myRatings.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err?.error?.message ?? 'Failed to load your ratings');
        this.loading.set(false);
      }
    });
  }

  loadMyRating(movieId: string) {
    this.loading.set(true);
    this.error.set(null);

    this.http.get<MyMovieRating>(`${this.apiUrl}/rating/me/${encodeURIComponent(movieId)}`).subscribe({
      next: (data) => {
        this.myRating.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        if (err?.status === 404) {
          this.myRating.set(null);
          this.loading.set(false);
          return;
        }
        this.error.set(err?.error?.message ?? 'Failed to load your rating');
        this.loading.set(false);
      }
    });
  }

  rate(movieId: string, stars: number) {
    this.loading.set(true);
    this.error.set(null);

    this.http.put<void>(`${this.apiUrl}/rating/${encodeURIComponent(movieId)}`, { stars }).subscribe({
      next: () => {
        this.loading.set(false);
        this.loadMyRating(movieId);
      },
      error: (err) => {
        this.error.set(err?.error?.message ?? 'Failed to save rating');
        this.loading.set(false);
      }
    });
  }

  addToWatchlist(movieId: string) {
    return this.http.put<void>(`${this.apiUrl}/rating/watchlist/${encodeURIComponent(movieId)}`, {});
  }
}
