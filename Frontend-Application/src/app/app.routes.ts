import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth-guard';
import { Login } from './features/auth/login/login';
import { Register } from './features/auth/register/register';

import { HomePage } from './features/movies/home-page/home-page';
import { MovieDetailsPage } from './features/movies/movie-details-page/movie-details-page';
import { RecommendationsPage } from './features/recommendations/recommendations-page/recommendations-page';
import { WatchlistPage } from './features/watchlist/watchlist-page/watchlist-page';
import { MyRatingsPage } from './features/ratings/my-ratings-page/my-ratings-page';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'movies' },

  // Auth
  { path: 'login', component: Login },
  { path: 'register', component: Register },

  // Movies
  { path: 'movies', component: HomePage },
  { path: 'movies/:id', component: MovieDetailsPage },

  // Personalized
  { path: 'recommendations', component: RecommendationsPage, canActivate: [authGuard] },
  { path: 'watchlist', component: WatchlistPage, canActivate: [authGuard] },
  { path: 'ratings', component: MyRatingsPage, canActivate: [authGuard] },

  { path: '**', redirectTo: 'movies' }
];
