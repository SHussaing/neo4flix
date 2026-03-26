import { Injectable, signal } from '@angular/core';
import { ApiBase } from './api-base';
import type { Movie } from '../models/movie';

@Injectable({ providedIn: 'root' })
export class WatchlistApi extends ApiBase {
  readonly watchlist = signal<Movie[] | null>(null);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  loadMine() {
    this.loading.set(true);
    this.error.set(null);

    this.http.get<Movie[]>(`${this.apiUrl}/rating/watchlist/me`).subscribe({
      next: (data) => {
        this.watchlist.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err?.error?.message ?? 'Failed to load watchlist');
        this.loading.set(false);
      }
    });
  }

  remove(movieId: string) {
    this.http.delete(`${this.apiUrl}/rating/watchlist/${encodeURIComponent(movieId)}`).subscribe({
      next: () => this.loadMine(),
      error: (err) => {
        this.error.set(err?.error?.message ?? 'Failed to remove from watchlist');
      }
    });
  }
}

