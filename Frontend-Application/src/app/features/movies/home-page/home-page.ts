import { Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MovieApi } from '../../../core/services/movie-api';

@Component({
  standalone: true,
  selector: 'app-home-page',
  imports: [RouterLink],
  templateUrl: './home-page.html',
  styleUrl: './home-page.css'
})
export class HomePage {
  private readonly movieApi = inject(MovieApi);

  readonly movies = this.movieApi.movies;
  readonly loading = this.movieApi.loading;
  readonly error = this.movieApi.error;

  readonly hasMovies = computed(() => (this.movies()?.length ?? 0) > 0);

  constructor() {
    this.movieApi.loadAll();
  }
}
