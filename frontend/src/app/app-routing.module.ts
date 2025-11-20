import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

/**
 * Main routing configuration for the banking application.
 * 
 * Routes:
 * - /login: Authentication page
 * - /dashboard: Main account overview (protected)
 * - /transfer: Money transfer page (protected)
 * - Default: Redirect to dashboard
 */
const routes: Routes = [
  {
    path: '',
    redirectTo: '/dashboard',
    pathMatch: 'full'
  },
  {
    path: 'login',
    loadChildren: () => import('./auth/auth.module').then(m => m.AuthModule)
  },
  {
    path: 'dashboard',
    loadChildren: () => import('./dashboard/dashboard.module').then(m => m.DashboardModule),
    // canActivate: [AuthGuard] // TODO: Add when AuthGuard is implemented
  },
  {
    path: 'transfer',
    loadChildren: () => import('./transfer/transfer.module').then(m => m.TransferModule),
    // canActivate: [AuthGuard] // TODO: Add when AuthGuard is implemented
  },
  {
    path: '**',
    redirectTo: '/dashboard'
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