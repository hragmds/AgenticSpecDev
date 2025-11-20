import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './auth/auth.guard';

/**
 * Main routing configuration for the banking application.
 * 
 * Routes:
 * - /login: Authentication page (redirects authenticated users)
 * - /dashboard: Main account overview (protected by AuthGuard)
 * - /transfer: Money transfer page (protected by AuthGuard)
 * - Default: Redirect to login for unauthenticated, dashboard for authenticated
 */
const routes: Routes = [
  {
    path: '',
    redirectTo: '/login',
    pathMatch: 'full'
  },
  {
    path: 'login',
    loadChildren: () => import('./auth/auth.module').then(m => m.AuthModule)
  },
  {
    path: 'dashboard',
    loadChildren: () => import('./dashboard/dashboard.module').then(m => m.DashboardModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'transfer',
    loadChildren: () => import('./transfer/transfer.module').then(m => m.TransferModule),
    canActivate: [AuthGuard]
  },
  {
    path: '**',
    redirectTo: '/login'
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {
    enableTracing: false, // Set to true for debugging
    useHash: false
  })],
  exports: [RouterModule]
})
export class AppRoutingModule { }