import { Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { WatchlistApi } from '../../../core/services/watchlist-api';

@Component({
  standalone: true,
  selector: 'app-watchlist-page',
  imports: [RouterLink],
  templateUrl: './watchlist-page.html',
  styleUrl: './watchlist-page.css'
})
export class WatchlistPage {
  private readonly watchlistApi = inject(WatchlistApi);

  readonly items = this.watchlistApi.watchlist;
  readonly loading = this.watchlistApi.loading;
  readonly error = this.watchlistApi.error;

  readonly hasItems = computed(() => (this.items()?.length ?? 0) > 0);

  constructor() {
    this.watchlistApi.loadMine();
  }

  remove(movieId: string) {
    this.watchlistApi.remove(movieId);
  }
}

