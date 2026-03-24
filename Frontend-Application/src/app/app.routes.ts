import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth-guard';
import { roleGuard } from './core/guards/role-guard';
import { Login } from './features/auth/login/login';
import { Register } from './features/auth/register/register';
import { ProductList } from './features/products/product-list/product-list';
import { ProductDetail } from './features/products/product-detail/product-detail';
import { Dashboard } from './features/seller/dashboard/dashboard';
import { ProductForm } from './features/seller/product-form/product-form';
import { Profile } from './features/profile/profile';

import { CartPage } from './features/cart/cart-page/cart-page';
import { CheckoutPage } from './features/orders/checkout-page/checkout-page';
import { OrderListPage } from './features/orders/order-list-page/order-list-page';
import { OrderDetailPage } from './features/orders/order-detail-page/order-detail-page';
import { SellerOrdersPage } from './features/seller/orders-page/orders-page';
import { SellerAnalyticsPage } from './features/seller/analytics-page/analytics-page';

export const routes: Routes = [
  { path: '', component: ProductList },
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'products/:id', component: ProductDetail },
  { path: 'profile', component: Profile, canActivate: [authGuard] },

  // CLIENT-only flows
  { path: 'cart', component: CartPage, canActivate: [authGuard, roleGuard], data: { role: 'CLIENT' } },
  { path: 'checkout', component: CheckoutPage, canActivate: [authGuard, roleGuard], data: { role: 'CLIENT' } },
  { path: 'orders', component: OrderListPage, canActivate: [authGuard, roleGuard], data: { role: 'CLIENT' } },
  { path: 'orders/:id', component: OrderDetailPage, canActivate: [authGuard, roleGuard], data: { role: 'CLIENT' } },

  {
    path: 'seller',
    canActivate: [authGuard, roleGuard],
    data: { role: 'SELLER' },
    children: [
      { path: 'dashboard', component: Dashboard },
      { path: 'orders', component: SellerOrdersPage },
      { path: 'analytics', component: SellerAnalyticsPage },
      { path: 'products/new', component: ProductForm },
      { path: 'products/:id/edit', component: ProductForm }
    ]
  },
  { path: '**', redirectTo: '' }
];
