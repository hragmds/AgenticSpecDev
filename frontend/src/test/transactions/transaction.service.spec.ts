import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TransactionService } from '../../../app/transactions/transaction.service';
import { ApiService } from '../../../app/shared/api.service';
import { Transaction } from '../../../app/transactions/transaction.model';

describe('TransactionService', () => {
  let service: TransactionService;
  let httpMock: HttpTestingController;
  let apiService: jasmine.SpyObj<ApiService>;

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
    },
    {
      id: 3,
      fromAccountId: 1,
      toAccountId: 2,
      amount: 250.00,
      description: 'Another transfer',
      transactionDate: '2025-11-17T09:15:00'
    }
  ];

  beforeEach(() => {
    const apiServiceSpy = jasmine.createSpyObj('ApiService', ['get', 'post', 'put']);

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        TransactionService,
        { provide: ApiService, useValue: apiServiceSpy }
      ]
    });

    service = TestBed.inject(TransactionService);
    apiService = TestBed.inject(ApiService) as jasmine.SpyObj<ApiService>;
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  describe('Service Creation', () => {
    it('should be created', () => {
      expect(service).toBeTruthy();
    });

    it('should have getRecentTransactions method', () => {
      expect(typeof service.getRecentTransactions).toBe('function');
    });

    it('should have getAccountTransactions method', () => {
      expect(typeof service.getAccountTransactions).toBe('function');
    });
  });

  describe('getRecentTransactions', () => {
    it('should fetch recent transactions from /api/transactions', () => {
      const mockResponse = mockTransactions;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getRecentTransactions().subscribe((transactions: Transaction[]) => {
        expect(transactions).toEqual(mockResponse);
      });

      expect(apiService.get).toHaveBeenCalledWith('/transactions?limit=10');
    });

    it('should use default limit of 10 when not specified', () => {
      const mockResponse = mockTransactions;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getRecentTransactions().subscribe();

      expect(apiService.get).toHaveBeenCalledWith('/transactions?limit=10');
    });

    it('should use custom limit when specified', () => {
      const mockResponse = mockTransactions;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getRecentTransactions(5).subscribe();

      expect(apiService.get).toHaveBeenCalledWith('/transactions?limit=5');
    });

    it('should return Observable of Transaction array', (done) => {
      const mockResponse = mockTransactions;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getRecentTransactions().subscribe((transactions: Transaction[]) => {
        expect(Array.isArray(transactions)).toBeTrue();
        done();
      });
    });

    it('should return transactions with all required fields', (done) => {
      const mockResponse = mockTransactions;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getRecentTransactions().subscribe((transactions: Transaction[]) => {
        transactions.forEach(transaction => {
          expect(transaction.id).toBeDefined();
          expect(transaction.fromAccountId).toBeDefined();
          expect(transaction.toAccountId).toBeDefined();
          expect(transaction.amount).toBeDefined();
          expect(transaction.transactionDate).toBeDefined();
        });
        done();
      });
    });

    it('should return empty array when no transactions exist', (done) => {
      const mockResponse: Transaction[] = [];
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getRecentTransactions().subscribe((transactions: Transaction[]) => {
        expect(transactions.length).toBe(0);
        done();
      });
    });

    it('should preserve transaction amounts', (done) => {
      const mockResponse = mockTransactions;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getRecentTransactions().subscribe((transactions: Transaction[]) => {
        expect(transactions[0].amount).toBe(500.00);
        expect(transactions[1].amount).toBe(100.00);
        expect(transactions[2].amount).toBe(250.00);
        done();
      });
    });

    it('should handle service error gracefully', (done) => {
      const mockError = new Error('Network error');
      apiService.get.and.returnValue(Promise.reject(mockError));

      service.getRecentTransactions().subscribe(
        () => fail('should have failed'),
        (error: any) => {
          expect(error).toBeTruthy();
          done();
        }
      );
    });

    it('should include transaction descriptions', (done) => {
      const mockResponse = mockTransactions;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getRecentTransactions().subscribe((transactions: Transaction[]) => {
        expect(transactions[0].description).toBe('Transfer to savings');
        expect(transactions[1].description).toBe('Transfer from savings');
        done();
      });
    });

    it('should respect multiple different limit values', () => {
      const mockResponse = mockTransactions;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getRecentTransactions(1).subscribe();
      expect(apiService.get).toHaveBeenCalledWith('/transactions?limit=1');

      service.getRecentTransactions(20).subscribe();
      expect(apiService.get).toHaveBeenCalledWith('/transactions?limit=20');

      service.getRecentTransactions(100).subscribe();
      expect(apiService.get).toHaveBeenCalledWith('/transactions?limit=100');
    });
  });

  describe('getAccountTransactions', () => {
    const accountId = 1;

    it('should fetch account transactions from /api/accounts/{id}/transactions', () => {
      const mockResponse = mockTransactions;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getAccountTransactions(accountId).subscribe((transactions: Transaction[]) => {
        expect(transactions).toEqual(mockResponse);
      });

      expect(apiService.get).toHaveBeenCalledWith(`/accounts/${accountId}/transactions?limit=20`);
    });

    it('should use default limit of 20 when not specified', () => {
      const mockResponse = mockTransactions;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getAccountTransactions(accountId).subscribe();

      expect(apiService.get).toHaveBeenCalledWith(`/accounts/${accountId}/transactions?limit=20`);
    });

    it('should use custom limit when specified', () => {
      const mockResponse = mockTransactions;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getAccountTransactions(accountId, 5).subscribe();

      expect(apiService.get).toHaveBeenCalledWith(`/accounts/${accountId}/transactions?limit=5`);
    });

    it('should include accountId in URL', () => {
      const mockResponse = mockTransactions;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getAccountTransactions(5).subscribe();

      expect(apiService.get).toHaveBeenCalledWith('/accounts/5/transactions?limit=20');
    });

    it('should return Observable of Transaction array', (done) => {
      const mockResponse = mockTransactions;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getAccountTransactions(accountId).subscribe((transactions: Transaction[]) => {
        expect(Array.isArray(transactions)).toBeTrue();
        done();
      });
    });

    it('should return transactions with all required fields', (done) => {
      const mockResponse = mockTransactions;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getAccountTransactions(accountId).subscribe((transactions: Transaction[]) => {
        transactions.forEach(transaction => {
          expect(transaction.id).toBeDefined();
          expect(transaction.fromAccountId).toBeDefined();
          expect(transaction.toAccountId).toBeDefined();
          expect(transaction.amount).toBeDefined();
          expect(transaction.transactionDate).toBeDefined();
        });
        done();
      });
    });

    it('should filter transactions for specific account', (done) => {
      const mockResponse = mockTransactions;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getAccountTransactions(1).subscribe((transactions: Transaction[]) => {
        // All should involve account 1
        transactions.forEach(transaction => {
          const involvesAccount1 = transaction.fromAccountId === 1 || transaction.toAccountId === 1;
          expect(involvesAccount1).toBeTrue();
        });
        done();
      });
    });

    it('should return empty array when account has no transactions', (done) => {
      const mockResponse: Transaction[] = [];
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getAccountTransactions(999).subscribe((transactions: Transaction[]) => {
        expect(transactions.length).toBe(0);
        done();
      });
    });

    it('should handle account not found error', (done) => {
      const mockError = new Error('Account not found');
      apiService.get.and.returnValue(Promise.reject(mockError));

      service.getAccountTransactions(999).subscribe(
        () => fail('should have failed'),
        (error: any) => {
          expect(error).toBeTruthy();
          done();
        }
      );
    });

    it('should preserve transaction amounts for account', (done) => {
      const mockResponse = mockTransactions;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getAccountTransactions(accountId).subscribe((transactions: Transaction[]) => {
        expect(transactions[0].amount).toBe(500.00);
        expect(transactions[1].amount).toBe(100.00);
        done();
      });
    });

    it('should support different account IDs', () => {
      const mockResponse = mockTransactions;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getAccountTransactions(1).subscribe();
      expect(apiService.get).toHaveBeenCalledWith('/accounts/1/transactions?limit=20');

      service.getAccountTransactions(2).subscribe();
      expect(apiService.get).toHaveBeenCalledWith('/accounts/2/transactions?limit=20');

      service.getAccountTransactions(3).subscribe();
      expect(apiService.get).toHaveBeenCalledWith('/accounts/3/transactions?limit=20');
    });

    it('should respect limit parameter with accountId', () => {
      const mockResponse = mockTransactions;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getAccountTransactions(1, 10).subscribe();
      expect(apiService.get).toHaveBeenCalledWith('/accounts/1/transactions?limit=10');

      service.getAccountTransactions(2, 50).subscribe();
      expect(apiService.get).toHaveBeenCalledWith('/accounts/2/transactions?limit=50');
    });
  });

  describe('Data Type Validation', () => {
    it('should return transactions with numeric ID', (done) => {
      const mockResponse = mockTransactions;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getRecentTransactions().subscribe((transactions: Transaction[]) => {
        transactions.forEach(transaction => {
          expect(typeof transaction.id).toBe('number');
        });
        done();
      });
    });

    it('should return transactions with numeric amount', (done) => {
      const mockResponse = mockTransactions;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getRecentTransactions().subscribe((transactions: Transaction[]) => {
        transactions.forEach(transaction => {
          expect(typeof transaction.amount).toBe('number');
        });
        done();
      });
    });

    it('should return transactions with numeric account IDs', (done) => {
      const mockResponse = mockTransactions;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getRecentTransactions().subscribe((transactions: Transaction[]) => {
        transactions.forEach(transaction => {
          expect(typeof transaction.fromAccountId).toBe('number');
          expect(typeof transaction.toAccountId).toBe('number');
        });
        done();
      });
    });

    it('should return transactions with string dates', (done) => {
      const mockResponse = mockTransactions;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getRecentTransactions().subscribe((transactions: Transaction[]) => {
        transactions.forEach(transaction => {
          expect(typeof transaction.transactionDate).toBe('string');
        });
        done();
      });
    });
  });

  describe('Integration with ApiService', () => {
    it('should use ApiService for HTTP calls', () => {
      const mockResponse = mockTransactions;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getRecentTransactions().subscribe();

      expect(apiService.get).toHaveBeenCalled();
    });

    it('should call ApiService with correct endpoint for recent transactions', () => {
      const mockResponse = mockTransactions;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getRecentTransactions(5).subscribe();

      expect(apiService.get).toHaveBeenCalledWith('/transactions?limit=5');
    });

    it('should call ApiService with correct endpoint for account transactions', () => {
      const mockResponse = mockTransactions;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getAccountTransactions(1, 10).subscribe();

      expect(apiService.get).toHaveBeenCalledWith('/accounts/1/transactions?limit=10');
    });

    it('should propagate ApiService errors to subscribers', (done) => {
      const mockError = new Error('API error');
      apiService.get.and.returnValue(Promise.reject(mockError));

      service.getRecentTransactions().subscribe(
        () => fail('should have failed'),
        (error: any) => {
          expect(error).toBe(mockError);
          done();
        }
      );
    });
  });

  describe('Transaction Sorting and Ordering', () => {
    it('should preserve transaction order from service', (done) => {
      const mockResponse = mockTransactions;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getRecentTransactions().subscribe((transactions: Transaction[]) => {
        expect(transactions[0].id).toBe(1);
        expect(transactions[1].id).toBe(2);
        expect(transactions[2].id).toBe(3);
        done();
      });
    });

    it('should include transaction descriptions in order', (done) => {
      const mockResponse = mockTransactions;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getRecentTransactions().subscribe((transactions: Transaction[]) => {
        expect(transactions[0].description).toBe('Transfer to savings');
        expect(transactions[1].description).toBe('Transfer from savings');
        expect(transactions[2].description).toBe('Another transfer');
        done();
      });
    });
  });

  describe('Edge Cases', () => {
    it('should handle zero amount transactions', (done) => {
      const mockResponse = [{
        id: 1,
        fromAccountId: 1,
        toAccountId: 2,
        amount: 0.00,
        description: 'Zero transfer',
        transactionDate: '2025-11-19T10:00:00'
      }];
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getRecentTransactions().subscribe((transactions: Transaction[]) => {
        expect(transactions[0].amount).toBe(0.00);
        done();
      });
    });

    it('should handle large amount transactions', (done) => {
      const mockResponse = [{
        id: 1,
        fromAccountId: 1,
        toAccountId: 2,
        amount: 999999.99,
        description: 'Large transfer',
        transactionDate: '2025-11-19T10:00:00'
      }];
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getRecentTransactions().subscribe((transactions: Transaction[]) => {
        expect(transactions[0].amount).toBe(999999.99);
        done();
      });
    });

    it('should handle missing optional description field', (done) => {
      const mockResponse = [{
        id: 1,
        fromAccountId: 1,
        toAccountId: 2,
        amount: 100.00,
        transactionDate: '2025-11-19T10:00:00'
      }];
      apiService.get.and.returnValue(Promise.resolve(mockResponse as any));

      service.getRecentTransactions().subscribe((transactions: Transaction[]) => {
        expect(transactions[0]).toBeTruthy();
        done();
      });
    });
  });
});
