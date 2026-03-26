import { Injectable, signal } from '@angular/core';
import { ApiBase } from './api-base';
import type { Movie } from '../models/movie';

@Injectable({ providedIn: 'root' })
export class MovieApi extends ApiBase {
  readonly movies = signal<Movie[] | null>(null);
  readonly selectedMovie = signal<Movie | null>(null);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  loadAll() {
    this.loading.set(true);
    this.error.set(null);

    this.http.get<Movie[]>(`${this.apiUrl}/movie`).subscribe({
      next: (data) => {
        this.movies.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err?.error?.message ?? 'Failed to load movies');
        this.loading.set(false);
      }
    });
  }

  search(query: string) {
    this.loading.set(true);
    this.error.set(null);

    this.http.get<Movie[]>(`${this.apiUrl}/movie/search`, { params: { q: query } }).subscribe({
      next: (data) => {
        this.movies.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err?.error?.message ?? 'Search failed');
        this.loading.set(false);
      }
    });
  }

  loadById(id: string) {
    this.loading.set(true);
    this.error.set(null);

    this.http.get<Movie>(`${this.apiUrl}/movie/${encodeURIComponent(id)}`).subscribe({
      next: (data) => {
        this.selectedMovie.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err?.error?.message ?? 'Failed to load movie');
        this.loading.set(false);
      }
    });
  }

  loadDetailsById(id: string) {
    this.loading.set(true);
    this.error.set(null);

    this.http.get<Movie>(`${this.apiUrl}/movie/${encodeURIComponent(id)}/details`).subscribe({
      next: (data) => {
        this.selectedMovie.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err?.error?.message ?? 'Failed to load movie details');
        this.loading.set(false);
      }
    });
  }
}
