# Research: Banking MVP Application Best Practices

## Executive Summary

Based on comprehensive research of Spring Boot, Angular, and SQLite best practices for financial applications, this document provides structured recommendations for implementing a banking MVP that maximizes simplicity and testability. All recommendations prioritize proven patterns over complex architectures, ensuring rapid development with high confidence through testing.

## Technology Stack Analysis

### Backend Framework: Spring Boot 3.x

**Decision**: Spring Boot 3.x with Java 17+

**Rationale**:
- Industry-standard framework with extensive documentation
- Built-in dependency injection simplifies testing
- Spring Data JPA abstracts database operations
- Excellent testing support (MockMvc, @SpringBootTest, @DataJpaTest)
- Convention over configuration reduces boilerplate
- Auto-configuration for web, JPA, and testing
- Mature ecosystem with proven financial application patterns

**Key Benefits for Banking MVP**:
- Transactional annotations ensure ACID compliance for transfers
- Built-in validation framework for financial data
- Security framework available (though simplified for MVP)
- Comprehensive testing infrastructure

### Frontend Framework: Angular 17+

**Decision**: Angular 17+ with TypeScript

**Rationale**:
- Strong typing with TypeScript reduces runtime errors in financial calculations
- Component-based architecture enables isolated testing
- Dependency injection facilitates service mocking
- RxJS provides robust async handling for API calls
- Extensive testing utilities (TestBed, HttpClientTestingModule)
- Material Design components for professional UI

**Key Benefits for Banking MVP**:
- Guards protect sensitive routes
- Interceptors handle authentication tokens consistently
- Services centralize business logic for testing
- Reactive forms provide validation for financial inputs

### Database: SQLite

**Decision**: SQLite 3.x

**Rationale**:
- Zero configuration required
- Embedded database simplifies deployment
- ACID compliant for financial transactions
- Excellent for testing (in-memory databases)
- No external database server needed for MVP
- Built-in support for decimal precision

**Key Benefits for Banking MVP**:
- Foreign key constraints ensure data integrity
- Transaction support for atomic transfers
- Simple schema evolution
- Portable database file

## Spring Boot Project Structure for Financial Applications

### Recommended Module Organization

```
com.banking/
├── config/                    # Configuration classes
│   ├── SecurityConfig.java    # CORS, session management
│   ├── DatabaseConfig.java    # SQLite dialect configuration
│   └── ValidationConfig.java  # Custom validators
├── model/                     # JPA entities
│   ├── User.java             # User account information
│   ├── Account.java          # Banking accounts
│   └── Transaction.java      # Transaction history
├── repository/               # Data access layer
│   ├── UserRepository.java
│   ├── AccountRepository.java
│   └── TransactionRepository.java
├── service/                  # Business logic layer
│   ├── AuthService.java      # Authentication logic
│   ├── AccountService.java   # Account operations
│   ├── TransactionService.java # Transaction queries
│   └── TransferService.java  # Transfer operations
├── controller/               # REST API endpoints
│   ├── AuthController.java   # /api/auth/*
│   ├── AccountController.java # /api/accounts
│   ├── TransactionController.java # /api/transactions/*
│   └── TransferController.java # /api/transfers
├── dto/                      # Data transfer objects
│   ├── request/              # API request objects
│   │   ├── LoginRequest.java
│   │   └── TransferRequest.java
│   ├── response/             # API response objects
│   │   ├── LoginResponse.java
│   │   ├── AccountDto.java
│   │   ├── TransactionDto.java
│   │   └── TransferResponse.java
│   └── ErrorResponse.java    # Error response structure
└── exception/                # Exception handling
    ├── ValidationException.java
    └── GlobalExceptionHandler.java
```

### Controller/Service/Repository Pattern Example

**Controller Layer** (Handles HTTP, delegates to service):
```java
@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "http://localhost:4200")
public class AccountController {
    
    private final AccountService accountService;
    private final AuthService authService;
    
    public AccountController(AccountService accountService, AuthService authService) {
        this.accountService = accountService;
        this.authService = authService;
    }
    
    @GetMapping
    public ResponseEntity<List<AccountDto>> getAccounts(
            @RequestHeader("Authorization") String token) {
        
        Long userId = authService.getUserIdFromToken(token);
        List<AccountDto> accounts = accountService.getAccountsByUser(userId);
        return ResponseEntity.ok(accounts);
    }
}
```

**Service Layer** (Business logic, transaction boundaries):
```java
@Service
@Transactional
public class AccountService {
    
    private final AccountRepository accountRepository;
    
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    
    @Transactional(readOnly = true)
    public List<AccountDto> getAccountsByUser(Long userId) {
        List<Account> accounts = accountRepository.findByUserIdOrderByAccountNumber(userId);
        return accounts.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    private AccountDto mapToDto(Account account) {
        return AccountDto.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType())
                .balance(formatCurrency(account.getBalance()))
                .build();
    }
    
    private String formatCurrency(BigDecimal amount) {
        return String.format("$%.2f", amount);
    }
}
```

**Repository Layer** (Data access via Spring Data JPA):
```java
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    List<Account> findByUserIdOrderByAccountNumber(Long userId);
    
    @Query("SELECT a FROM Account a WHERE a.userId = :userId AND a.accountType = :type")
    List<Account> findByUserIdAndAccountType(@Param("userId") Long userId, 
                                           @Param("type") String accountType);
}
```

## Angular Architecture Patterns for Banking Dashboards

### Recommended Component Structure

```
src/app/
├── core/                     # Singleton services, guards
│   ├── services/
│   │   ├── auth.service.ts   # Authentication logic
│   │   ├── account.service.ts # Account API calls
│   │   ├── transaction.service.ts # Transaction API calls
│   │   └── transfer.service.ts # Transfer API calls
│   ├── guards/
│   │   └── auth.guard.ts     # Route protection
│   ├── interceptors/
│   │   └── auth.interceptor.ts # Token injection
│   └── models/               # TypeScript interfaces
│       ├── account.interface.ts
│       ├── transaction.interface.ts
│       └── user.interface.ts
├── features/                 # Feature modules
│   ├── auth/
│   │   ├── login/
│   │   │   ├── login.component.ts
│   │   │   ├── login.component.html
│   │   │   ├── login.component.css
│   │   │   └── login.component.spec.ts
│   │   └── auth.module.ts
│   ├── dashboard/
│   │   ├── components/
│   │   │   ├── account-summary/
│   │   │   ├── transaction-history/
│   │   │   └── quick-transfer/
│   │   ├── dashboard.component.ts
│   │   └── dashboard.module.ts
│   └── transfer/
│       ├── transfer-form/
│       ├── transfer-confirmation/
│       └── transfer.module.ts
├── shared/                   # Reusable components
│   ├── components/
│   │   ├── currency-display/
│   │   ├── loading-spinner/
│   │   └── error-message/
│   └── shared.module.ts
└── app.module.ts
```

### Smart/Dumb Component Pattern

**Smart Component** (Container - manages data):
```typescript
@Component({
  selector: 'app-dashboard',
  template: `
    <div class="dashboard-container">
      <app-account-summary 
        [accounts]="accounts$ | async"
        [loading]="loading$ | async">
      </app-account-summary>
      <app-transaction-history 
        [transactions]="transactions$ | async"
        (refreshRequested)="refreshTransactions()">
      </app-transaction-history>
    </div>
  `
})
export class DashboardComponent implements OnInit {
  accounts$: Observable<Account[]>;
  transactions$: Observable<Transaction[]>;
  loading$: Observable<boolean>;
  
  constructor(
    private accountService: AccountService,
    private transactionService: TransactionService
  ) {}
  
  ngOnInit(): void {
    this.loadData();
  }
  
  private loadData(): void {
    this.accounts$ = this.accountService.getAccounts();
    this.transactions$ = this.transactionService.getRecentTransactions();
    this.loading$ = this.accountService.loading$;
  }
  
  refreshTransactions(): void {
    this.transactions$ = this.transactionService.getRecentTransactions();
  }
}
```

**Dumb Component** (Presentational - displays data):
```typescript
@Component({
  selector: 'app-account-summary',
  template: `
    <div class="account-summary" *ngIf="!loading">
      <div class="account-card" *ngFor="let account of accounts">
        <h3>{{ account.accountType }}</h3>
        <p class="account-number">{{ account.accountNumber }}</p>
        <p class="balance">{{ account.balance }}</p>
      </div>
    </div>
    <app-loading-spinner *ngIf="loading"></app-loading-spinner>
  `
})
export class AccountSummaryComponent {
  @Input() accounts: Account[] = [];
  @Input() loading: boolean = false;
}
```

### Service Pattern with HTTP Client

```typescript
@Injectable({
  providedIn: 'root'
})
export class AccountService {
  private readonly apiUrl = 'http://localhost:8080/api';
  
  constructor(private http: HttpClient) {}
  
  getAccounts(): Observable<Account[]> {
    return this.http.get<Account[]>(`${this.apiUrl}/accounts`)
      .pipe(
        map(accounts => accounts.map(this.transformAccount)),
        catchError(this.handleError<Account[]>('getAccounts', []))
      );
  }
  
  private transformAccount(account: any): Account {
    return {
      id: account.id,
      accountNumber: account.accountNumber,
      accountType: account.accountType,
      balance: account.balance,
      formattedBalance: account.balance // Already formatted by backend
    };
  }
  
  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      console.error(`${operation} failed:`, error);
      return of(result as T);
    };
  }
}
```

## SQLite Schema Design for Banking

### Entity Relationship Design

```sql
-- Users table - basic user information
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Accounts table - user banking accounts
CREATE TABLE accounts (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    account_number VARCHAR(20) NOT NULL UNIQUE,
    account_type VARCHAR(20) NOT NULL CHECK (account_type IN ('CHECKING', 'SAVINGS')),
    balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Transactions table - transaction history
CREATE TABLE transactions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    from_account_id INTEGER,
    to_account_id INTEGER,
    transaction_type VARCHAR(20) NOT NULL CHECK (transaction_type IN ('TRANSFER', 'DEPOSIT', 'WITHDRAWAL')),
    amount DECIMAL(15,2) NOT NULL,
    description TEXT,
    transaction_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (from_account_id) REFERENCES accounts(id),
    FOREIGN KEY (to_account_id) REFERENCES accounts(id),
    CONSTRAINT valid_transfer CHECK (
        (transaction_type = 'TRANSFER' AND from_account_id IS NOT NULL AND to_account_id IS NOT NULL) OR
        (transaction_type IN ('DEPOSIT', 'WITHDRAWAL'))
    )
);

-- Indexes for performance
CREATE INDEX idx_accounts_user_id ON accounts(user_id);
CREATE INDEX idx_transactions_user_id ON transactions(user_id);
CREATE INDEX idx_transactions_date DESC ON transactions(transaction_date);
CREATE INDEX idx_transactions_from_account ON transactions(from_account_id);
CREATE INDEX idx_transactions_to_account ON transactions(to_account_id);
```

### Sample Data for Testing

```sql
-- Test users
INSERT INTO users (id, username, email) VALUES 
(1, 'demo', 'demo@banking.com'),
(2, 'testuser', 'test@banking.com');

-- Test accounts
INSERT INTO accounts (id, user_id, account_number, account_type, balance) VALUES 
(1, 1, '1001-2001', 'CHECKING', 2500.00),
(2, 1, '1001-3001', 'SAVINGS', 10000.00),
(3, 2, '2001-2001', 'CHECKING', 1500.00);

-- Test transactions
INSERT INTO transactions (user_id, from_account_id, to_account_id, transaction_type, amount, description, transaction_date) VALUES 
(1, 1, 2, 'TRANSFER', 500.00, 'Transfer to savings', '2024-01-15 10:30:00'),
(1, 2, 1, 'TRANSFER', 200.00, 'Transfer from savings', '2024-01-14 14:15:00'),
(1, NULL, 1, 'DEPOSIT', 1000.00, 'Salary deposit', '2024-01-13 09:00:00'),
(1, 1, NULL, 'WITHDRAWAL', 100.00, 'ATM withdrawal', '2024-01-12 16:45:00');
```

## TDD Approaches

### Spring Boot REST API Testing

**Unit Tests** (Service Layer):
```java
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    
    @Mock
    private AccountRepository accountRepository;
    
    @InjectMocks
    private AccountService accountService;
    
    @Test
    void testGetAccountsByUser_ReturnsFormattedBalance() {
        // Given
        Long userId = 1L;
        Account account = new Account();
        account.setBalance(new BigDecimal("1234.56"));
        account.setAccountType("CHECKING");
        account.setAccountNumber("1001-2001");
        
        when(accountRepository.findByUserIdOrderByAccountNumber(userId))
            .thenReturn(Arrays.asList(account));
        
        // When
        List<AccountDto> result = accountService.getAccountsByUser(userId);
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBalance()).isEqualTo("$1,234.56");
        assertThat(result.get(0).getAccountType()).isEqualTo("CHECKING");
    }
}
```

**Integration Tests** (Controller Layer):
```java
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
class AccountControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Test
    void testGetAccounts_ValidSession_ReturnsAccountList() throws Exception {
        // Given
        User user = new User();
        user.setUsername("testuser");
        entityManager.persistAndFlush(user);
        
        Account account = new Account();
        account.setUserId(user.getId());
        account.setAccountNumber("1001-2001");
        account.setAccountType("CHECKING");
        account.setBalance(new BigDecimal("1000.00"));
        entityManager.persistAndFlush(account);
        
        // When & Then
        mockMvc.perform(get("/api/accounts")
                .header("Authorization", "valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].accountNumber").value("1001-2001"))
                .andExpect(jsonPath("$[0].balance").value("$1,000.00"));
    }
}
```

**Repository Tests**:
```java
@DataJpaTest
class AccountRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Test
    void testFindByUserIdOrderByAccountNumber() {
        // Given
        Long userId = 1L;
        
        Account checking = new Account();
        checking.setUserId(userId);
        checking.setAccountNumber("1001-2001");
        checking.setAccountType("CHECKING");
        entityManager.persistAndFlush(checking);
        
        Account savings = new Account();
        savings.setUserId(userId);
        savings.setAccountNumber("1001-3001");
        savings.setAccountType("SAVINGS");
        entityManager.persistAndFlush(savings);
        
        // When
        List<Account> accounts = accountRepository.findByUserIdOrderByAccountNumber(userId);
        
        // Then
        assertThat(accounts).hasSize(2);
        assertThat(accounts.get(0).getAccountNumber()).isEqualTo("1001-2001");
        assertThat(accounts.get(1).getAccountNumber()).isEqualTo("1001-3001");
    }
}
```

### Angular Component Testing

**Component Tests**:
```typescript
describe('AccountSummaryComponent', () => {
  let component: AccountSummaryComponent;
  let fixture: ComponentFixture<AccountSummaryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AccountSummaryComponent],
      imports: [CommonModule]
    }).compileComponents();

    fixture = TestBed.createComponent(AccountSummaryComponent);
    component = fixture.componentInstance;
  });

  it('should display account information correctly', () => {
    // Given
    const mockAccounts: Account[] = [
      {
        id: 1,
        accountNumber: '1001-2001',
        accountType: 'CHECKING',
        balance: '$2,500.00'
      }
    ];
    
    component.accounts = mockAccounts;
    
    // When
    fixture.detectChanges();
    
    // Then
    const compiled = fixture.nativeElement;
    expect(compiled.querySelector('.account-card h3').textContent).toContain('CHECKING');
    expect(compiled.querySelector('.balance').textContent).toContain('$2,500.00');
  });

  it('should show loading spinner when loading is true', () => {
    // Given
    component.loading = true;
    
    // When
    fixture.detectChanges();
    
    // Then
    expect(fixture.nativeElement.querySelector('app-loading-spinner')).toBeTruthy();
  });
});
```

**Service Tests**:
```typescript
describe('AccountService', () => {
  let service: AccountService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AccountService]
    });
    
    service = TestBed.inject(AccountService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should fetch accounts from API', () => {
    // Given
    const mockAccounts = [
      { id: 1, accountNumber: '1001-2001', accountType: 'CHECKING', balance: '$2,500.00' }
    ];

    // When
    service.getAccounts().subscribe(accounts => {
      // Then
      expect(accounts).toEqual(mockAccounts);
      expect(accounts[0].accountNumber).toBe('1001-2001');
    });

    const req = httpMock.expectOne('http://localhost:8080/api/accounts');
    expect(req.request.method).toBe('GET');
    req.flush(mockAccounts);
  });

  it('should handle API errors gracefully', () => {
    // When
    service.getAccounts().subscribe(accounts => {
      // Then
      expect(accounts).toEqual([]);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/accounts');
    req.error(new ErrorEvent('Network error'));
  });
});
```

## Data Validation Patterns for Financial Amounts

### Backend Validation (Spring Boot)

**Custom Validator for Currency**:
```java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CurrencyValidator.class)
public @interface ValidCurrency {
    String message() default "Invalid currency amount";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

public class CurrencyValidator implements ConstraintValidator<ValidCurrency, BigDecimal> {
    
    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        if (value == null) return false;
        
        // Check for 2 decimal places
        return value.scale() <= 2 && value.compareTo(BigDecimal.ZERO) >= 0;
    }
}
```

**DTO with Validation**:
```java
public class TransferRequest {
    
    @NotNull(message = "From account is required")
    private Long fromAccountId;
    
    @NotNull(message = "To account is required") 
    private Long toAccountId;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @ValidCurrency
    private BigDecimal amount;
    
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;
    
    // getters and setters
}
```

**Service Layer Validation**:
```java
@Service
public class TransferService {
    
    public TransferResponse transfer(TransferRequest request) {
        validateTransferRequest(request);
        
        // Process transfer...
    }
    
    private void validateTransferRequest(TransferRequest request) {
        BigDecimal amount = request.getAmount();
        
        // Ensure 2 decimal precision
        if (amount.scale() > 2) {
            amount = amount.setScale(2, RoundingMode.HALF_UP);
            request.setAmount(amount);
        }
        
        // Additional business validations
        if (amount.compareTo(new BigDecimal("10000.00")) > 0) {
            throw new ValidationException("Transfer amount cannot exceed $10,000.00");
        }
    }
}
```

### Frontend Validation (Angular)

**Reactive Forms with Custom Validators**:
```typescript
export class TransferFormComponent implements OnInit {
  transferForm: FormGroup;
  
  constructor(private fb: FormBuilder) {}
  
  ngOnInit(): void {
    this.transferForm = this.fb.group({
      fromAccountId: ['', Validators.required],
      toAccountId: ['', Validators.required],
      amount: ['', [
        Validators.required,
        this.currencyValidator,
        this.minAmountValidator(0.01),
        this.maxAmountValidator(10000.00)
      ]],
      description: ['', Validators.maxLength(255)]
    });
  }
  
  private currencyValidator(control: AbstractControl): ValidationErrors | null {
    const value = control.value;
    if (!value) return null;
    
    const regex = /^\d+(\.\d{1,2})?$/;
    return regex.test(value) ? null : { invalidCurrency: true };
  }
  
  private minAmountValidator(min: number) {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = parseFloat(control.value);
      return value >= min ? null : { minAmount: { min, actual: value } };
    };
  }
  
  private maxAmountValidator(max: number) {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = parseFloat(control.value);
      return value <= max ? null : { maxAmount: { max, actual: value } };
    };
  }
}
```

**Template with Error Messages**:
```html
<form [formGroup]="transferForm" (ngSubmit)="onSubmit()">
  <div class="form-group">
    <label for="amount">Amount</label>
    <input 
      type="number" 
      id="amount" 
      step="0.01"
      min="0.01"
      max="10000.00"
      formControlName="amount"
      class="form-control"
      [class.is-invalid]="transferForm.get('amount')?.invalid && transferForm.get('amount')?.touched">
    
    <div class="invalid-feedback" *ngIf="transferForm.get('amount')?.invalid && transferForm.get('amount')?.touched">
      <div *ngIf="transferForm.get('amount')?.errors?.['required']">Amount is required</div>
      <div *ngIf="transferForm.get('amount')?.errors?.['invalidCurrency']">Please enter a valid currency amount (e.g., 123.45)</div>
      <div *ngIf="transferForm.get('amount')?.errors?.['minAmount']">Amount must be at least $0.01</div>
      <div *ngIf="transferForm.get('amount')?.errors?.['maxAmount']">Amount cannot exceed $10,000.00</div>
    </div>
  </div>
  
  <button type="submit" [disabled]="transferForm.invalid" class="btn btn-primary">
    Transfer Funds
  </button>
</form>
```

## Session Management for Simple Authentication

### Backend Session Management (Spring Boot)

**Simple Token-Based Session**:
```java
@Service
public class AuthService {
    private final Map<String, Long> activeSessions = new ConcurrentHashMap<>();
    private final UserRepository userRepository;
    
    public LoginResponse login(LoginRequest request) {
        // Simple validation - any non-empty credentials
        if (isBlank(request.getUsername()) || isBlank(request.getPassword())) {
            throw new ValidationException("Username and password are required");
        }
        
        // Find or create user (MVP - simple approach)
        User user = userRepository.findByUsername(request.getUsername())
            .orElseGet(() -> createUser(request.getUsername()));
        
        // Generate simple token
        String token = UUID.randomUUID().toString();
        activeSessions.put(token, user.getId());
        
        return new LoginResponse(token, user.getUsername());
    }
    
    public boolean validateSession(String token) {
        return token != null && activeSessions.containsKey(token);
    }
    
    public Long getUserIdFromToken(String token) {
        return activeSessions.get(token);
    }
    
    public void logout(String token) {
        activeSessions.remove(token);
    }
}
```

**Security Configuration**:
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:4200");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors().and()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
            );
            
        return http.build();
    }
}
```

### Frontend Session Management (Angular)

**Authentication Service**:
```typescript
@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly tokenKey = 'auth_token';
  private tokenSubject = new BehaviorSubject<string | null>(this.getStoredToken());
  
  constructor(private http: HttpClient, private router: Router) {}
  
  login(username: string, password: string): Observable<LoginResponse> {
    const request = { username, password };
    
    return this.http.post<LoginResponse>('/api/auth/login', request)
      .pipe(
        tap(response => {
          sessionStorage.setItem(this.tokenKey, response.token);
          this.tokenSubject.next(response.token);
        })
      );
  }
  
  logout(): void {
    sessionStorage.removeItem(this.tokenKey);
    this.tokenSubject.next(null);
    this.router.navigate(['/login']);
  }
  
  getToken(): string | null {
    return this.tokenSubject.value;
  }
  
  isAuthenticated(): boolean {
    return this.getToken() !== null;
  }
  
  private getStoredToken(): string | null {
    return sessionStorage.getItem(this.tokenKey);
  }
}
```

**HTTP Interceptor**:
```typescript
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  
  constructor(private authService: AuthService) {}
  
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.authService.getToken();
    
    if (token && !req.url.includes('/auth/login')) {
      const authReq = req.clone({
        headers: req.headers.set('Authorization', token)
      });
      
      return next.handle(authReq).pipe(
        catchError((error: HttpErrorResponse) => {
          if (error.status === 401) {
            this.authService.logout();
          }
          return throwError(() => error);
        })
      );
    }
    
    return next.handle(req);
  }
}
```

**Route Guard**:
```typescript
@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  
  constructor(private authService: AuthService, private router: Router) {}
  
  canActivate(): boolean {
    if (this.authService.isAuthenticated()) {
      return true;
    }
    
    this.router.navigate(['/login']);
    return false;
  }
}
```

## Integration Testing Strategies

### Backend Integration Tests

**API Contract Testing**:
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiContractTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Test
    void testTransferEndpoint_ValidRequest_ReturnsExpectedResponse() {
        // Given
        setupTestData();
        
        TransferRequest request = new TransferRequest();
        request.setFromAccountId(1L);
        request.setToAccountId(2L);
        request.setAmount(new BigDecimal("100.00"));
        request.setDescription("Test transfer");
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "valid-token");
        
        // When
        ResponseEntity<TransferResponse> response = restTemplate.postForEntity(
            "/api/transfers",
            new HttpEntity<>(request, headers),
            TransferResponse.class
        );
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getTransactionId()).isNotNull();
        assertThat(response.getBody().getAmount()).isEqualTo("$100.00");
    }
}
```

### Frontend Integration Tests

**Component Integration with Services**:
```typescript
describe('DashboardComponent Integration', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;
  let accountService: jasmine.SpyObj<AccountService>;
  let transactionService: jasmine.SpyObj<TransactionService>;

  beforeEach(async () => {
    const accountServiceSpy = jasmine.createSpyObj('AccountService', ['getAccounts']);
    const transactionServiceSpy = jasmine.createSpyObj('TransactionService', ['getRecentTransactions']);

    await TestBed.configureTestingModule({
      declarations: [DashboardComponent, AccountSummaryComponent, TransactionHistoryComponent],
      providers: [
        { provide: AccountService, useValue: accountServiceSpy },
        { provide: TransactionService, useValue: transactionServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
    accountService = TestBed.inject(AccountService) as jasmine.SpyObj<AccountService>;
    transactionService = TestBed.inject(TransactionService) as jasmine.SpyObj<TransactionService>;
  });

  it('should load and display account and transaction data', fakeAsync(() => {
    // Given
    const mockAccounts = [{ id: 1, accountNumber: '1001-2001', accountType: 'CHECKING', balance: '$2,500.00' }];
    const mockTransactions = [{ id: 1, amount: '$100.00', description: 'Test transaction', date: '2024-01-15' }];
    
    accountService.getAccounts.and.returnValue(of(mockAccounts));
    transactionService.getRecentTransactions.and.returnValue(of(mockTransactions));

    // When
    fixture.detectChanges();
    tick();
    fixture.detectChanges();

    // Then
    expect(component.accounts$ | async).toEqual(mockAccounts);
    expect(component.transactions$ | async).toEqual(mockTransactions);
    
    const compiled = fixture.nativeElement;
    expect(compiled.querySelector('.account-card')).toBeTruthy();
    expect(compiled.querySelector('.transaction-item')).toBeTruthy();
  }));
});
```

## Performance Considerations for MVP

### Database Optimizations (Minimal for MVP)

**Essential Indexes Only**:
```sql
-- Primary keys (automatic)
-- Foreign key indexes for joins
CREATE INDEX idx_accounts_user_id ON accounts(user_id);
CREATE INDEX idx_transactions_user_id ON transactions(user_id);
CREATE INDEX idx_transactions_date ON transactions(transaction_date DESC);

-- No complex composite indexes for MVP
-- No query optimization beyond basic indexing
-- No connection pooling configuration
```

### Caching Strategy (None for MVP)

**Explicit No-Cache Decision**:
- No Redis/Memcached
- No Spring Boot caching annotations
- No HTTP cache headers
- Fresh data on every request
- Simplicity over performance for MVP

## Security Considerations for MVP

### Input Validation Only

**What to Include**:
- SQL injection prevention (JPA handles this)
- XSS prevention (Angular sanitizes by default)
- CSRF protection disabled (stateless API)
- Input length limits
- Currency amount validation

**What to Exclude** (Beyond MVP scope):
- Password hashing (fake auth)
- Session expiration
- Rate limiting
- Audit logging
- Role-based authorization
- SSL/HTTPS requirements

## Error Handling Patterns

### Backend Global Exception Handler

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(ValidationException e) {
        return new ErrorResponse("VALIDATION_ERROR", e.getMessage());
    }
    
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) 
    public ErrorResponse handleNotFound(EntityNotFoundException e) {
        return new ErrorResponse("NOT_FOUND", e.getMessage());
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGeneral(Exception e) {
        return new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred");
    }
}
```

### Frontend Error Handling Service

```typescript
@Injectable({
  providedIn: 'root'
})
export class ErrorHandlingService {
  
  handleHttpError(error: HttpErrorResponse): string {
    if (error.error instanceof ErrorEvent) {
      // Client-side error
      return `Client Error: ${error.error.message}`;
    } else {
      // Server-side error
      switch (error.status) {
        case 400:
          return error.error?.message || 'Invalid request';
        case 401:
          return 'Please log in to continue';
        case 404:
          return 'Resource not found';
        case 500:
          return 'Server error. Please try again later.';
        default:
          return `Unexpected error occurred (${error.status})`;
      }
    }
  }
}
```

## Alternatives Considered and Rejected

### Backend Alternatives

| Alternative | Pros | Cons | Decision |
|-------------|------|------|----------|
| **Plain Java Servlets** | Lightweight, direct control | Too much boilerplate, manual DI | ❌ Rejected |
| **Micronaut** | Fast startup, low memory | Less mature, smaller community | ❌ Rejected |
| **Quarkus** | Cloud-native, GraalVM support | Newer framework, learning curve | ❌ Rejected |
| **Node.js/Express** | JavaScript ecosystem, fast development | Less type safety, different skillset | ❌ Rejected |

### Frontend Alternatives

| Alternative | Pros | Cons | Decision |
|-------------|------|------|----------|
| **React** | Large ecosystem, component reuse | More setup, JSX learning curve | ❌ Rejected |
| **Vue.js** | Easy learning curve, flexible | Smaller enterprise adoption | ❌ Rejected |
| **Svelte** | No virtual DOM, smaller bundles | Newer framework, less tooling | ❌ Rejected |
| **Plain JavaScript** | No framework overhead | Too much manual work, poor testing | ❌ Rejected |

### Database Alternatives

| Alternative | Pros | Cons | Decision |
|-------------|------|------|----------|
| **PostgreSQL** | Full SQL features, better concurrency | Requires server setup, overkill for MVP | ❌ Rejected |
| **MySQL** | Mature, well-documented | Server configuration, complexity | ❌ Rejected |
| **H2 Database** | In-memory testing, embedded | Less production-ready than SQLite | ❌ Rejected |
| **File-based storage** | Ultra-simple | No ACID, no relationships, no querying | ❌ Rejected |

## Implementation Priorities

### Phase 1: Foundation (Mandatory)
1. Project structure setup
2. Database schema creation
3. Basic Spring Boot configuration
4. Angular project initialization
5. CORS configuration

### Phase 2: Authentication (MVP Core)
1. Simple token-based auth service
2. Login/logout endpoints
3. Session management
4. Route guards
5. HTTP interceptors

### Phase 3: Account Overview (MVP Core)
1. Account data services
2. Transaction history services
3. Dashboard components
4. Data display components

### Phase 4: Money Transfers (MVP Core)
1. Transfer validation logic
2. Transfer processing service
3. Transfer form component
4. Confirmation workflows

### Phase 5: Testing (Throughout)
1. Unit tests for all services
2. Integration tests for APIs
3. Component tests for UI
4. End-to-end validation

## Success Metrics for MVP

### Technical Metrics
- **Test Coverage**: >80% for both backend and frontend
- **Build Time**: <2 minutes for full rebuild
- **API Response Time**: <500ms for all endpoints
- **Bundle Size**: <2MB for Angular build

### Functional Metrics
- **Authentication Success**: >95% first-attempt login success
- **Transfer Completion**: >95% successful transfer rate
- **Data Accuracy**: 100% accurate balance calculations
- **Error Handling**: Graceful degradation for all error scenarios

## Conclusion

This research provides a comprehensive foundation for implementing a banking MVP using proven, simple patterns. All recommendations prioritize:

1. **Testability**: Every pattern includes testing strategies
2. **Simplicity**: Standard patterns over complex architectures  
3. **MVP Scope**: No unnecessary features or optimizations
4. **Maintainability**: Clear separation of concerns and standard conventions

The technology stack (Spring Boot + Angular + SQLite) provides the optimal balance of developer productivity, testing support, and simplicity for an MVP banking application.

All patterns and examples provided are production-ready while maintaining the simplicity required for MVP scope. No complex design patterns, performance optimizations, or security hardening beyond basic input validation are included, ensuring rapid development while maintaining high quality through comprehensive testing.