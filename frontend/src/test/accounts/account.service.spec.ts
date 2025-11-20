import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AccountService } from '../../../app/accounts/account.service';
import { ApiService } from '../../../app/shared/api.service';
import { Account } from '../../../app/accounts/account.model';

describe('AccountService', () => {
  let service: AccountService;
  let httpMock: HttpTestingController;
  let apiService: jasmine.SpyObj<ApiService>;

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

  const mockAccount: Account = {
    id: 1,
    userId: 1,
    accountType: 'CHECKING',
    accountName: 'Main Checking',
    balance: 1500.00,
    createdAt: '2025-11-19T00:00:00'
  };

  beforeEach(() => {
    const apiServiceSpy = jasmine.createSpyObj('ApiService', ['get', 'post', 'put']);

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        AccountService,
        { provide: ApiService, useValue: apiServiceSpy }
      ]
    });

    service = TestBed.inject(AccountService);
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

    it('should have getUserAccounts method', () => {
      expect(typeof service.getUserAccounts).toBe('function');
    });

    it('should have getAccountById method', () => {
      expect(typeof service.getAccountById).toBe('function');
    });
  });

  describe('getUserAccounts', () => {
    it('should fetch all user accounts from /api/accounts', () => {
      const mockResponse = mockAccounts;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getUserAccounts().subscribe((accounts: Account[]) => {
        expect(accounts).toEqual(mockResponse);
        expect(accounts.length).toBe(2);
      });

      expect(apiService.get).toHaveBeenCalledWith('/accounts');
    });

    it('should return Observable of Account array', (done) => {
      const mockResponse = mockAccounts;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getUserAccounts().subscribe((accounts: Account[]) => {
        expect(Array.isArray(accounts)).toBeTrue();
        expect(accounts[0]).toBeTruthy();
        done();
      });
    });

    it('should return accounts with all required fields', (done) => {
      const mockResponse = mockAccounts;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getUserAccounts().subscribe((accounts: Account[]) => {
        accounts.forEach(account => {
          expect(account.id).toBeDefined();
          expect(account.userId).toBeDefined();
          expect(account.accountType).toBeDefined();
          expect(account.accountName).toBeDefined();
          expect(account.balance).toBeDefined();
        });
        done();
      });
    });

    it('should return empty array when user has no accounts', (done) => {
      const mockResponse: Account[] = [];
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getUserAccounts().subscribe((accounts: Account[]) => {
        expect(accounts.length).toBe(0);
        done();
      });
    });

    it('should handle accounts with different types', (done) => {
      const mockResponse = mockAccounts;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getUserAccounts().subscribe((accounts: Account[]) => {
        const hasChecking = accounts.some(a => a.accountType === 'CHECKING');
        const hasSavings = accounts.some(a => a.accountType === 'SAVINGS');
        expect(hasChecking).toBeTrue();
        expect(hasSavings).toBeTrue();
        done();
      });
    });

    it('should preserve account balance values', (done) => {
      const mockResponse = mockAccounts;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getUserAccounts().subscribe((accounts: Account[]) => {
        expect(accounts[0].balance).toBe(1500.00);
        expect(accounts[1].balance).toBe(5000.00);
        done();
      });
    });

    it('should handle service error gracefully', (done) => {
      const mockError = new Error('Network error');
      apiService.get.and.returnValue(Promise.reject(mockError));

      service.getUserAccounts().subscribe(
        () => fail('should have failed'),
        (error: any) => {
          expect(error).toBeTruthy();
          done();
        }
      );
    });

    it('should not pass any parameters to API call', () => {
      const mockResponse = mockAccounts;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getUserAccounts().subscribe();

      expect(apiService.get).toHaveBeenCalledWith('/accounts');
      expect(apiService.get).toHaveBeenCalledTimes(1);
    });
  });

  describe('getAccountById', () => {
    const accountId = 1;

    it('should fetch account by ID from /api/accounts/{id}', () => {
      const mockResponse = mockAccount;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getAccountById(accountId).subscribe((account: Account) => {
        expect(account).toEqual(mockResponse);
        expect(account.id).toBe(accountId);
      });

      expect(apiService.get).toHaveBeenCalledWith(`/accounts/${accountId}`);
    });

    it('should return Observable of single Account', (done) => {
      const mockResponse = mockAccount;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getAccountById(accountId).subscribe((account: Account) => {
        expect(account).toBeTruthy();
        expect(account.id).toBe(accountId);
        done();
      });
    });

    it('should return account with all required fields', (done) => {
      const mockResponse = mockAccount;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getAccountById(accountId).subscribe((account: Account) => {
        expect(account.id).toBeDefined();
        expect(account.userId).toBeDefined();
        expect(account.accountType).toBeDefined();
        expect(account.accountName).toBeDefined();
        expect(account.balance).toBeDefined();
        done();
      });
    });

    it('should include account name in response', (done) => {
      const mockResponse = mockAccount;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getAccountById(accountId).subscribe((account: Account) => {
        expect(account.accountName).toBe('Main Checking');
        done();
      });
    });

    it('should include account balance in response', (done) => {
      const mockResponse = mockAccount;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getAccountById(accountId).subscribe((account: Account) => {
        expect(account.balance).toBe(1500.00);
        done();
      });
    });

    it('should include account type in response', (done) => {
      const mockResponse = mockAccount;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getAccountById(accountId).subscribe((account: Account) => {
        expect(account.accountType).toBe('CHECKING');
        done();
      });
    });

    it('should handle different account IDs', () => {
      const mockResponse = mockAccount;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getAccountById(2).subscribe();
      expect(apiService.get).toHaveBeenCalledWith('/accounts/2');

      service.getAccountById(3).subscribe();
      expect(apiService.get).toHaveBeenCalledWith('/accounts/3');
    });

    it('should handle account not found error', (done) => {
      const mockError = new Error('Account not found');
      apiService.get.and.returnValue(Promise.reject(mockError));

      service.getAccountById(999).subscribe(
        () => fail('should have failed'),
        (error: any) => {
          expect(error).toBeTruthy();
          done();
        }
      );
    });

    it('should include userId in response', (done) => {
      const mockResponse = mockAccount;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getAccountById(accountId).subscribe((account: Account) => {
        expect(account.userId).toBe(1);
        done();
      });
    });

    it('should preserve createdAt timestamp', (done) => {
      const mockResponse = mockAccount;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getAccountById(accountId).subscribe((account: Account) => {
        expect(account.createdAt).toBe('2025-11-19T00:00:00');
        done();
      });
    });
  });

  describe('Data Type Validation', () => {
    it('should return accounts with numeric ID', (done) => {
      const mockResponse = mockAccounts;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getUserAccounts().subscribe((accounts: Account[]) => {
        accounts.forEach(account => {
          expect(typeof account.id).toBe('number');
        });
        done();
      });
    });

    it('should return accounts with numeric balance', (done) => {
      const mockResponse = mockAccounts;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getUserAccounts().subscribe((accounts: Account[]) => {
        accounts.forEach(account => {
          expect(typeof account.balance).toBe('number');
        });
        done();
      });
    });

    it('should return accounts with string accountName', (done) => {
      const mockResponse = mockAccounts;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getUserAccounts().subscribe((accounts: Account[]) => {
        accounts.forEach(account => {
          expect(typeof account.accountName).toBe('string');
        });
        done();
      });
    });

    it('should return accounts with valid account types', (done) => {
      const mockResponse = mockAccounts;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getUserAccounts().subscribe((accounts: Account[]) => {
        const validTypes = ['CHECKING', 'SAVINGS'];
        accounts.forEach(account => {
          expect(validTypes).toContain(account.accountType);
        });
        done();
      });
    });
  });

  describe('Integration with ApiService', () => {
    it('should use ApiService for HTTP calls', () => {
      const mockResponse = mockAccounts;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getUserAccounts().subscribe();

      expect(apiService.get).toHaveBeenCalled();
    });

    it('should call ApiService with correct endpoint', () => {
      const mockResponse = mockAccount;
      apiService.get.and.returnValue(Promise.resolve(mockResponse));

      service.getAccountById(1).subscribe();

      expect(apiService.get).toHaveBeenCalledWith('/accounts/1');
    });

    it('should propagate ApiService errors to subscribers', (done) => {
      const mockError = new Error('API error');
      apiService.get.and.returnValue(Promise.reject(mockError));

      service.getUserAccounts().subscribe(
        () => fail('should have failed'),
        (error: any) => {
          expect(error).toBe(mockError);
          done();
        }
      );
    });
  });
});
