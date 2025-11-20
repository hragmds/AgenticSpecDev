import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { map, take } from 'rxjs/operators';
import { AuthService } from './auth.service';

/**
 * Authentication guard for protecting routes.
 * 
 * Features:
 * - Blocks access to protected routes for unauthenticated users
 * - Redirects to login page when authentication is required
 * - Uses AuthService authentication state
 * - Supports both synchronous and asynchronous checks
 */
@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  /**
   * Determine if route can be activated.
   */
  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> | Promise<boolean> | boolean {
    
    return this.authService.isAuthenticated$
      .pipe(
        take(1),
        map(isAuthenticated => {
          if (isAuthenticated) {
            return true;
          } else {
            // Store the attempted URL for redirect after login
            this.router.navigate(['/login'], { 
              queryParams: { returnUrl: state.url }
            });
            return false;
          }
        })
      );
  }
}

/**
 * Reverse auth guard for login page.
 * Redirects authenticated users away from login page.
 */
@Injectable({
  providedIn: 'root'
})
export class LoginGuard implements CanActivate {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  /**
   * Determine if login route can be activated.
   * Redirects authenticated users to dashboard.
   */
  canActivate(): Observable<boolean> | Promise<boolean> | boolean {
    
    return this.authService.isAuthenticated$
      .pipe(
        take(1),
        map(isAuthenticated => {
          if (isAuthenticated) {
            // Redirect authenticated users to dashboard
            this.router.navigate(['/dashboard']);
            return false;
          } else {
            // Allow unauthenticated users to access login
            return true;
          }
        })
      );
  }
}