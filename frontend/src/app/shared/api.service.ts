import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

/**
 * Shared API service for HTTP communications with backend.
 * 
 * Provides:
 * - Base URL configuration
 * - Common HTTP methods
 * - Header management (session tokens)
 * - Error handling setup
 */
@Injectable({
  providedIn: 'root'
})
export class ApiService {
  
  private readonly baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  /**
   * GET request with optional session token.
   */
  get<T>(endpoint: string, token?: string): Observable<T> {
    const headers = this.createHeaders(token);
    return this.http.get<T>(`${this.baseUrl}${endpoint}`, { headers });
  }

  /**
   * POST request with optional session token.
   */
  post<T>(endpoint: string, body: any, token?: string): Observable<T> {
    const headers = this.createHeaders(token);
    return this.http.post<T>(`${this.baseUrl}${endpoint}`, body, { headers });
  }

  /**
   * PUT request with optional session token.
   */
  put<T>(endpoint: string, body: any, token?: string): Observable<T> {
    const headers = this.createHeaders(token);
    return this.http.put<T>(`${this.baseUrl}${endpoint}`, body, { headers });
  }

  /**
   * DELETE request with optional session token.
   */
  delete<T>(endpoint: string, token?: string): Observable<T> {
    const headers = this.createHeaders(token);
    return this.http.delete<T>(`${this.baseUrl}${endpoint}`, { headers });
  }

  /**
   * Create HTTP headers with optional Authorization token.
   */
  private createHeaders(token?: string): HttpHeaders {
    let headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    if (token) {
      headers = headers.set('Authorization', `Bearer ${token}`);
    }

    return headers;
  }
}