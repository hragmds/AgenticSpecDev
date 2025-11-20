import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../shared/api.service';
import { Transaction } from './transaction.model';

/**
 * Transaction service for managing user transactions.
 * Communicates with backend /api/transactions endpoint.
 */
@Injectable({
  providedIn: 'root'
})
export class TransactionService {

  constructor(private apiService: ApiService) { }

  /**
   * Get recent transactions for user.
   * @param limit Maximum number of transactions to return (default 10)
   */
  getRecentTransactions(limit: number = 10): Observable<Transaction[]> {
    return this.apiService.get<Transaction[]>(`/transactions?limit=${limit}`);
  }

  /**
   * Get transactions for specific account.
   * @param accountId The account ID
   * @param limit Maximum number of transactions to return (default 20)
   */
  getAccountTransactions(accountId: number, limit: number = 20): Observable<Transaction[]> {
    return this.apiService.get<Transaction[]>(`/accounts/${accountId}/transactions?limit=${limit}`);
  }
}
