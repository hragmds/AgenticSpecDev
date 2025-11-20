import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../shared/api.service';
import { Account } from './account.model';

/**
 * Account service for managing user accounts.
 * Communicates with backend /api/accounts endpoint.
 */
@Injectable({
  providedIn: 'root'
})
export class AccountService {

  constructor(private apiService: ApiService) { }

  /**
   * Get all accounts for authenticated user.
   */
  getUserAccounts(): Observable<Account[]> {
    return this.apiService.get<Account[]>('/accounts');
  }

  /**
   * Get specific account by ID.
   */
  getAccountById(accountId: number): Observable<Account> {
    return this.apiService.get<Account>(`/accounts/${accountId}`);
  }
}
