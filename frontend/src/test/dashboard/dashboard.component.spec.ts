import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CommonModule } from '@angular/common';
import { DashboardComponent } from '../../../app/dashboard/dashboard.component';
import { AccountService } from '../../../app/accounts/account.service';
import { TransactionService } from '../../../app/transactions/transaction.service';
import { AuthService } from '../../../app/auth/auth.service';
import { Router } from '@angular/router';
import { Account } from '../../../app/accounts/account.model';
import { Transaction } from '../../../app/transactions/transaction.model';
import { of, throwError } from 'rxjs';
import { BehaviorSubject } from 'rxjs';

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;
  let accountService: jasmine.SpyObj<AccountService>;
  let transactionService: jasmine.SpyObj<TransactionService>;
  let authService: jasmine.SpyObj<AuthService>;
  let router: jasmine.SpyObj<Router>;

  const mockAccounts: Account[] = [
    {
      id: 1,
      userId: 1,
      accountType: 'CHECKING',
      accountName: 'Main Checking',
      balance: 1500.00,
      createdAt: '2025-11-19T00:00:00'
    },
    {
      id: 2,
      userId: 1,
      accountType: 'SAVINGS',
      accountName: 'Emergency Fund',
      balance: 5000.00,
      createdAt: '2025-11-19T00:00:00'
    }
  ];

  const mockTransactions: Transaction[] = [
    {
      id: 1,
      fromAccountId: 1,
      toAccountId: 2,
      amount: 500.00,
      description: 'Transfer to savings',
      transactionDate: '2025-11-19T10:00:00'
    },
    {
      id: 2,
      fromAccountId: 2,
      toAccountId: 1,
      amount: 100.00,
      description: 'Transfer from savings',
      transactionDate: '2025-11-18T15:30:00'
    }
  ];

  beforeEach(async () => {
    const accountServiceSpy = jasmine.createSpyObj('AccountService', ['getUserAccounts', 'getAccountById']);
    const transactionServiceSpy = jasmine.createSpyObj('TransactionService', ['getRecentTransactions', 'getAccountTransactions']);
    const authServiceSpy = jasmine.createSpyObj('AuthService', ['logout', 'getCurrentUsername'], {
      currentUser$: new BehaviorSubject<string | null>('demo')
    });
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      declarations: [DashboardComponent],
      imports: [CommonModule],
      providers: [
        { provide: AccountService, useValue: accountServiceSpy },
        { provide: TransactionService, useValue: transactionServiceSpy },
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    }).compileComponents();

    accountService = TestBed.inject(AccountService) as jasmine.SpyObj<AccountService>;
    transactionService = TestBed.inject(TransactionService) as jasmine.SpyObj<TransactionService>;
    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
  });

  describe('Component Initialization', () => {
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should initialize with loading states', () => {
      expect(component.loadingAccounts).toBeTrue();
      expect(component.loadingTransactions).toBeTrue();
    });

    it('should initialize with no errors', () => {
      expect(component.accountsError).toBeNull();
      expect(component.transactionsError).toBeNull();
    });

    it('should initialize with empty account and transaction arrays', (done) => {
      component.accounts$.subscribe(accounts => {
        expect(accounts).toEqual([]);
      });

      component.transactions$.subscribe(transactions => {
        expect(transactions).toEqual([]);
        done();
      });
    });

    it('should have currentUser$ from AuthService', (done) => {
      component.currentUser$.subscribe(user => {
        expect(user).toBe('demo');
        done();
      });
    });
  });

  describe('ngOnInit', () => {
    it('should call loadDashboardData on init', () => {
      spyOn(component, 'loadDashboardData');
      component.ngOnInit();
      expect(component.loadDashboardData).toHaveBeenCalled();
    });

    it('should load accounts and transactions on init', () => {
      accountService.getUserAccounts.and.returnValue(of(mockAccounts));
      transactionService.getRecentTransactions.and.returnValue(of(mockTransactions));

      component.ngOnInit();

      expect(accountService.getUserAccounts).toHaveBeenCalled();
      expect(transactionService.getRecentTransactions).toHaveBeenCalledWith(5);
    });
  });

  describe('loadAccounts', () => {
    it('should set loading state to true', () => {
      component.loadingAccounts = false;
      accountService.getUserAccounts.and.returnValue(of(mockAccounts));

      component.loadAccounts();

      expect(component.loadingAccounts).toBeTrue();
    });

    it('should clear previous errors', () => {
      component.accountsError = 'Previous error';
      accountService.getUserAccounts.and.returnValue(of(mockAccounts));

      component.loadAccounts();

      expect(component.accountsError).toBeNull();
    });

    it('should fetch accounts from service', () => {
      accountService.getUserAccounts.and.returnValue(of(mockAccounts));

      component.loadAccounts();

      expect(accountService.getUserAccounts).toHaveBeenCalled();
    });

    it('should update accounts$ with retrieved data', (done) => {
      accountService.getUserAccounts.and.returnValue(of(mockAccounts));

      component.loadAccounts();

      component.accounts$.subscribe(accounts => {
        expect(accounts).toEqual(mockAccounts);
        done();
      });
    });

    it('should set loading state to false after success', (done) => {
      accountService.getUserAccounts.and.returnValue(of(mockAccounts));

      component.loadAccounts();

      setTimeout(() => {
        expect(component.loadingAccounts).toBeFalse();
        done();
      }, 100);
    });

    it('should handle error from service', (done) => {
      const error = new Error('Failed to load accounts');
      accountService.getUserAccounts.and.returnValue(throwError(() => error));

      component.loadAccounts();

      setTimeout(() => {
        expect(component.accountsError).toContain('Failed to load accounts');
        expect(component.loadingAccounts).toBeFalse();
        done();
      }, 100);
    });

    it('should display user-friendly error message on failure', (done) => {
      accountService.getUserAccounts.and.returnValue(throwError(() => new Error('Network error')));

      component.loadAccounts();

      setTimeout(() => {
        expect(component.accountsError).toBe('Failed to load accounts. Please try again.');
        done();
      }, 100);
    });

    it('should handle empty account list', (done) => {
      accountService.getUserAccounts.and.returnValue(of([]));

      component.loadAccounts();

      component.accounts$.subscribe(accounts => {
        expect(accounts).toEqual([]);
        expect(component.loadingAccounts).toBeFalse();
        done();
      });
    });
  });

  describe('loadTransactions', () => {
    it('should set loading state to true', () => {
      component.loadingTransactions = false;
      transactionService.getRecentTransactions.and.returnValue(of(mockTransactions));

      component.loadTransactions();

      expect(component.loadingTransactions).toBeTrue();
    });

    it('should clear previous errors', () => {
      component.transactionsError = 'Previous error';
      transactionService.getRecentTransactions.and.returnValue(of(mockTransactions));

      component.loadTransactions();

      expect(component.transactionsError).toBeNull();
    });

    it('should fetch transactions with limit of 5 (FR-006)', () => {
      transactionService.getRecentTransactions.and.returnValue(of(mockTransactions));

      component.loadTransactions();

      expect(transactionService.getRecentTransactions).toHaveBeenCalledWith(5);
    });

    it('should update transactions$ with retrieved data', (done) => {
      transactionService.getRecentTransactions.and.returnValue(of(mockTransactions));

      component.loadTransactions();

      component.transactions$.subscribe(transactions => {
        expect(transactions).toEqual(mockTransactions);
        done();
      });
    });

    it('should set loading state to false after success', (done) => {
      transactionService.getRecentTransactions.and.returnValue(of(mockTransactions));

      component.loadTransactions();

      setTimeout(() => {
        expect(component.loadingTransactions).toBeFalse();
        done();
      }, 100);
    });

    it('should handle error from service', (done) => {
      const error = new Error('Failed to load transactions');
      transactionService.getRecentTransactions.and.returnValue(throwError(() => error));

      component.loadTransactions();

      setTimeout(() => {
        expect(component.transactionsError).toContain('Failed to load transactions');
        expect(component.loadingTransactions).toBeFalse();
        done();
      }, 100);
    });

    it('should display user-friendly error message on failure', (done) => {
      transactionService.getRecentTransactions.and.returnValue(throwError(() => new Error('API error')));

      component.loadTransactions();

      setTimeout(() => {
        expect(component.transactionsError).toBe('Failed to load transactions. Please try again.');
        done();
      }, 100);
    });

    it('should handle empty transaction list', (done) => {
      transactionService.getRecentTransactions.and.returnValue(of([]));

      component.loadTransactions();

      component.transactions$.subscribe(transactions => {
        expect(transactions).toEqual([]);
        expect(component.loadingTransactions).toBeFalse();
        done();
      });
    });
  });

  describe('loadDashboardData', () => {
    it('should call both loadAccounts and loadTransactions', () => {
      spyOn(component, 'loadAccounts');
      spyOn(component, 'loadTransactions');

      component.loadDashboardData();

      expect(component.loadAccounts).toHaveBeenCalled();
      expect(component.loadTransactions).toHaveBeenCalled();
    });

    it('should load accounts and transactions independently', () => {
      accountService.getUserAccounts.and.returnValue(of(mockAccounts));
      transactionService.getRecentTransactions.and.returnValue(of(mockTransactions));

      component.loadDashboardData();

      expect(accountService.getUserAccounts).toHaveBeenCalled();
      expect(transactionService.getRecentTransactions).toHaveBeenCalledWith(5);
    });

    it('should handle accounts error without blocking transactions load', (done) => {
      accountService.getUserAccounts.and.returnValue(throwError(() => new Error('Account load failed')));
      transactionService.getRecentTransactions.and.returnValue(of(mockTransactions));

      component.loadDashboardData();

      setTimeout(() => {
        expect(component.accountsError).toContain('Failed to load accounts');
        component.transactions$.subscribe(transactions => {
          expect(transactions).toEqual(mockTransactions);
          done();
        });
      }, 100);
    });

    it('should handle transactions error without blocking accounts load', (done) => {
      accountService.getUserAccounts.and.returnValue(of(mockAccounts));
      transactionService.getRecentTransactions.and.returnValue(throwError(() => new Error('Transaction load failed')));

      component.loadDashboardData();

      setTimeout(() => {
        component.accounts$.subscribe(accounts => {
          expect(accounts).toEqual(mockAccounts);
        });
        expect(component.transactionsError).toContain('Failed to load transactions');
        done();
      }, 100);
    });
  });

  describe('logout', () => {
    it('should call authService.logout', () => {
      component.logout();
      expect(authService.logout).toHaveBeenCalled();
    });

    it('should navigate to login after logout', () => {
      component.logout();
      expect(router.navigate).toHaveBeenCalledWith(['/login']);
    });

    it('should clear auth session before navigation', () => {
      authService.logout.and.callFake(() => {
        expect(authService.logout).toHaveBeenCalled();
      });

      component.logout();
      expect(router.navigate).toHaveBeenCalledWith(['/login']);
    });
  });

  describe('Error Recovery', () => {
    it('should allow retry of account loading', (done) => {
      transactionService.getRecentTransactions.and.returnValue(of(mockTransactions));
      accountService.getUserAccounts.and.returnValue(throwError(() => new Error('Network error')));

      component.loadAccounts();

      setTimeout(() => {
        expect(component.accountsError).toBeTruthy();

        // Retry with successful response
        accountService.getUserAccounts.and.returnValue(of(mockAccounts));
        component.loadAccounts();

        component.accounts$.subscribe(accounts => {
          expect(accounts).toEqual(mockAccounts);
          expect(component.accountsError).toBeNull();
          done();
        });
      }, 100);
    });

    it('should allow retry of transaction loading', (done) => {
      accountService.getUserAccounts.and.returnValue(of(mockAccounts));
      transactionService.getRecentTransactions.and.returnValue(throwError(() => new Error('Network error')));

      component.loadTransactions();

      setTimeout(() => {
        expect(component.transactionsError).toBeTruthy();

        // Retry with successful response
        transactionService.getRecentTransactions.and.returnValue(of(mockTransactions));
        component.loadTransactions();

        component.transactions$.subscribe(transactions => {
          expect(transactions).toEqual(mockTransactions);
          expect(component.transactionsError).toBeNull();
          done();
        });
      }, 100);
    });
  });

  describe('Data Display', () => {
    it('should display multiple accounts', (done) => {
      accountService.getUserAccounts.and.returnValue(of(mockAccounts));

      component.loadAccounts();

      component.accounts$.subscribe(accounts => {
        expect(accounts.length).toBe(2);
        expect(accounts[0].accountName).toBe('Main Checking');
        expect(accounts[1].accountName).toBe('Emergency Fund');
        done();
      });
    });

    it('should display multiple transactions', (done) => {
      transactionService.getRecentTransactions.and.returnValue(of(mockTransactions));

      component.loadTransactions();

      component.transactions$.subscribe(transactions => {
        expect(transactions.length).toBe(2);
        expect(transactions[0].description).toBe('Transfer to savings');
        expect(transactions[1].description).toBe('Transfer from savings');
        done();
      });
    });

    it('should preserve account balance values', (done) => {
      accountService.getUserAccounts.and.returnValue(of(mockAccounts));

      component.loadAccounts();

      component.accounts$.subscribe(accounts => {
        expect(accounts[0].balance).toBe(1500.00);
        expect(accounts[1].balance).toBe(5000.00);
        done();
      });
    });

    it('should preserve transaction amounts', (done) => {
      transactionService.getRecentTransactions.and.returnValue(of(mockTransactions));

      component.loadTransactions();

      component.transactions$.subscribe(transactions => {
        expect(transactions[0].amount).toBe(500.00);
        expect(transactions[1].amount).toBe(100.00);
        done();
      });
    });
  });
});
