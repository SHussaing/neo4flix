import { Component, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { RecommendationApi } from '../../../core/services/recommendation-api';
import { Auth } from '../../../core/services/auth';

@Component({
  standalone: true,
  selector: 'app-recommendations-page',
  imports: [CommonModule, RouterLink],
  templateUrl: './recommendations-page.html',
  styleUrl: './recommendations-page.css'
})
export class RecommendationsPage {
  private readonly recommendationApi = inject(RecommendationApi);
  private readonly auth = inject(Auth);

  readonly items = this.recommendationApi.recommendations;
  readonly loading = this.recommendationApi.loading;
  readonly error = this.recommendationApi.error;

  readonly hasItems = computed(() => (this.items()?.length ?? 0) > 0);

  readonly genre = signal('');
  readonly yearFrom = signal<string>('');
  readonly yearTo = signal<string>('');
  readonly shareMessage = signal<string | null>(null);

  constructor() {
    this.reload();
  }

  reload() {
    const yf = parseInt(this.yearFrom().trim(), 10);
    const yt = parseInt(this.yearTo().trim(), 10);

    this.recommendationApi.loadForMe({
      genre: this.genre().trim() || undefined,
      yearFrom: Number.isFinite(yf) ? yf : undefined,
      yearTo: Number.isFinite(yt) ? yt : undefined,
      limit: 20
    });
  }

  async copyShareText() {
    const items = this.items() ?? [];
    if (!items.length) return;

    const userName = this.auth.currentUser()?.name?.trim() || 'a Neo4flix user';

    const lines = items.map((m, i) => `${i + 1}. ${m.title} (${m.releaseYear ?? ''}) - ${m.genre ?? ''}`);
    const text = `Neo4flix recommendations for ${userName}:\n${lines.join('\n')}`;

    try {
      await navigator.clipboard.writeText(text);
      this.shareMessage.set('Copied recommendations to clipboard');
      setTimeout(() => this.shareMessage.set(null), 2500);
    } catch {
      this.shareMessage.set('Failed to copy. Your browser may block clipboard access.');
      setTimeout(() => this.shareMessage.set(null), 3500);
    }
  }
}
