import { Injectable } from '@angular/core';
import { Observable, BehaviorSubject, throwError } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { ApiService } from '../shared/api.service';

/**
 * Authentication service for login/logout and session management.
 * 
 * Features:
 * - Login with demo credentials
 * - Session token storage
 * - Authentication state management
 * - Route protection support
 */
@Injectable({
  providedIn: 'root'
})
export class AuthService {
  
  private readonly TOKEN_KEY = 'banking_session_token';
  private readonly USERNAME_KEY = 'banking_username';
  
  // Authentication state observable
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(this.hasValidSession());
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();
  
  // Current user observable
  private currentUserSubject = new BehaviorSubject<string | null>(this.getCurrentUsername());
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private apiService: ApiService) {}

  /**
   * Login with username and password.
   * For MVP: demo/password
   */
  login(username: string, password: string): Observable<LoginResponse> {
    const loginRequest = { username, password };
    
    return this.apiService.post<LoginResponse>('/auth/login', loginRequest)
      .pipe(
        map(response => {
          // Store session data
          this.storeSession(response.token, response.username);
          
          // Update authentication state
          this.isAuthenticatedSubject.next(true);
          this.currentUserSubject.next(response.username);
          
          return response;
        }),
        catchError(error => {
          console.error('Login failed:', error);
          return throwError(() => new Error(error.error?.message || 'Login failed'));
        })
      );
  }

  /**
   * Logout user and clear session.
   */
  logout(): void {
    this.clearSession();
    this.isAuthenticatedSubject.next(false);
    this.currentUserSubject.next(null);
  }

  /**
   * Check if user is currently authenticated.
   */
  isAuthenticated(): boolean {
    return this.hasValidSession();
  }

  /**
   * Get current session token.
   */
  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  /**
   * Get current username.
   */
  getCurrentUsername(): string | null {
    return localStorage.getItem(this.USERNAME_KEY);
  }

  /**
   * Store session data in localStorage.
   */
  private storeSession(token: string, username: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
    localStorage.setItem(this.USERNAME_KEY, username);
  }

  /**
   * Clear session data from localStorage.
   */
  private clearSession(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USERNAME_KEY);
  }

  /**
   * Check if there's a valid session.
   */
  private hasValidSession(): boolean {
    const token = this.getToken();
    const username = this.getCurrentUsername();
    return !!(token && username);
  }
}

/**
 * Login request interface.
 */
export interface LoginRequest {
  username: string;
  password: string;
}

/**
 * Login response interface.
 */
export interface LoginResponse {
  token: string;
  username: string;
  message: string;
}