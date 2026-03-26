import { Injectable, signal } from '@angular/core';
import { ApiBase } from './api-base';
import type { Recommendation } from '../models/recommendation';

export type RecommendationFilters = {
  genre?: string;
  yearFrom?: number;
  yearTo?: number;
  limit?: number;
};

@Injectable({ providedIn: 'root' })
export class RecommendationApi extends ApiBase {
  readonly recommendations = signal<Recommendation[] | null>(null);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  loadForMe(filters: RecommendationFilters = {}) {
    this.loading.set(true);
    this.error.set(null);

    const params: Record<string, string> = {};
    if (filters.limit != null) params['limit'] = String(filters.limit);
    if (filters.genre) params['genre'] = filters.genre;
    if (filters.yearFrom != null) params['yearFrom'] = String(filters.yearFrom);
    if (filters.yearTo != null) params['yearTo'] = String(filters.yearTo);

    this.http.get<Recommendation[]>(`${this.apiUrl}/recommendation/me`, { params }).subscribe({
      next: (data) => {
        this.recommendations.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err?.error?.message ?? 'Failed to load recommendations');
        this.loading.set(false);
      }
    });
  }
}
