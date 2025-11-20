import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';
import { AccountService } from '../accounts/account.service';
import { TransactionService } from '../transactions/transaction.service';
import { Account } from '../accounts/account.model';
import { Transaction } from '../transactions/transaction.model';
import { Observable, BehaviorSubject } from 'rxjs';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  
  currentUser$ = this.authService.currentUser$;
  
  // Account and transaction data
  accounts$ = new BehaviorSubject<Account[]>([]);
  transactions$ = new BehaviorSubject<Transaction[]>([]);
  
  // Loading and error states
  loadingAccounts = true;
  loadingTransactions = true;
  accountsError: string | null = null;
  transactionsError: string | null = null;

  constructor(
    private authService: AuthService,
    private accountService: AccountService,
    private transactionService: TransactionService,
    private router: Router
  ) {}

  ngOnInit(): void {
    console.log('Dashboard loaded for user:', this.authService.getCurrentUsername());
    this.loadDashboardData();
  }

  /**
   * Load accounts and transactions for dashboard.
   */
  loadDashboardData(): void {
    this.loadAccounts();
    this.loadTransactions();
  }

  /**
   * Load user accounts.
   */
  loadAccounts(): void {
    this.loadingAccounts = true;
    this.accountsError = null;
    
    this.accountService.getUserAccounts().subscribe({
      next: (accounts) => {
        this.accounts$.next(accounts);
        this.loadingAccounts = false;
      },
      error: (error) => {
        console.error('Failed to load accounts:', error);
        this.accountsError = 'Failed to load accounts. Please try again.';
        this.loadingAccounts = false;
      }
    });
  }

  /**
   * Load recent transactions.
   */
  loadTransactions(): void {
    this.loadingTransactions = true;
    this.transactionsError = null;
    
    // Load last 5 transactions (FR-006)
    this.transactionService.getRecentTransactions(5).subscribe({
      next: (transactions) => {
        this.transactions$.next(transactions);
        this.loadingTransactions = false;
      },
      error: (error) => {
        console.error('Failed to load transactions:', error);
        this.transactionsError = 'Failed to load transactions. Please try again.';
        this.loadingTransactions = false;
      }
    });
  }

  /**
   * Logout user and redirect to login page.
   */
  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}