# Tasks: Personal Banking Application

**Input**: Design documents from `/specs/001-banking-app/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/api.yaml, quickstart.md

**Tests**: Tests are NOT explicitly requested in the feature specification - tasks include TDD approach as per Constitution Principle II

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `- [ ] [ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [ ] T001 Create backend directory structure: `backend/src/main/java/com/banking/{config,model,repository,service,controller,dto}/` and `backend/src/test/java/com/banking/{service,controller}/`
- [ ] T002 Initialize Gradle project with `backend/build.gradle` including Spring Boot 3.x, Spring Data JPA, SQLite JDBC driver, JUnit 5 dependencies per [research.md](./research.md) "Backend Framework: Spring Boot"
- [ ] T003 [P] Initialize Angular project in `frontend/` using Angular CLI 17+: `ng new frontend --routing --style=css`
- [ ] T004 [P] Install Tailwind CSS in `frontend/`: `npm install -D tailwindcss postcss autoprefixer` and configure `tailwind.config.js` per [research.md](./research.md) "CSS Framework: Tailwind CSS"
- [ ] T005 [P] Create `backend/src/main/resources/application.properties` with SQLite configuration per [quickstart.md](./quickstart.md) "Backend Setup"
- [ ] T006 [P] Create `backend/src/main/resources/schema.sql` with DDL from [data-model.md](./data-model.md) "Database Schema (SQLite DDL)"

**Verification**: Run `./gradlew build` (backend) and `ng build` (frontend) - both should complete successfully

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T007 Create `backend/src/main/java/com/banking/BankingApplication.java` Spring Boot main class with `@SpringBootApplication` annotation
- [ ] T008 [P] Create `backend/src/main/java/com/banking/config/DatabaseConfig.java` for SQLite dialect configuration per [research.md](./research.md) "Database: SQLite"
- [ ] T009 [P] Create `backend/src/main/java/com/banking/config/SecurityConfig.java` for CORS configuration allowing http://localhost:4200 per [research.md](./research.md) "CORS Configuration"
- [ ] T010 [P] Create User entity in `backend/src/main/java/com/banking/model/User.java` with JPA mappings from [data-model.md](./data-model.md) "User Entity"
- [ ] T011 [P] Create Account entity in `backend/src/main/java/com/banking/model/Account.java` with JPA mappings and AccountType enum from [data-model.md](./data-model.md) "Account Entity"
- [ ] T012 [P] Create Transaction entity in `backend/src/main/java/com/banking/model/Transaction.java` with JPA mappings from [data-model.md](./data-model.md) "Transaction Entity"
- [ ] T013 [P] Create Transfer entity in `backend/src/main/java/com/banking/model/Transfer.java` with JPA mappings from [data-model.md](./data-model.md) "Transfer Entity"
- [ ] T014 Insert sample data from [data-model.md](./data-model.md) "Sample Data" into `backend/src/main/resources/data.sql` for testing
- [ ] T015 [P] Create `frontend/src/app/core/models/user.model.ts` with User, LoginRequest, LoginResponse interfaces from [data-model.md](./data-model.md) "TypeScript Interfaces"
- [ ] T016 [P] Create `frontend/src/app/core/models/account.model.ts` with Account interface from [data-model.md](./data-model.md) "TypeScript Interfaces"
- [ ] T017 [P] Create `frontend/src/app/core/models/transaction.model.ts` with Transaction interface from [data-model.md](./data-model.md) "TypeScript Interfaces"
- [ ] T018 [P] Create `frontend/src/app/core/models/transfer.model.ts` with Transfer, TransferRequest, TransferResponse interfaces from [data-model.md](./data-model.md) "TypeScript Interfaces"

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - User Authentication (Priority: P1) 🎯 MVP

**Goal**: Enable users to log in and access their personal dashboard

**Independent Test**: Navigate to login page, enter any non-empty credentials, verify successful redirection to dashboard and session persistence across navigation

### Tests for User Story 1 (TDD - Write FIRST, ensure FAIL)

- [ ] T019 [P] [US1] Create `backend/src/test/java/com/banking/service/AuthServiceTest.java` with tests:
  - `testLogin_WithNonEmptyCredentials_ReturnsToken()`
  - `testLogin_WithEmptyUsername_ThrowsException()`
  - `testLogin_WithEmptyPassword_ThrowsException()`
  - `testLogout_ValidToken_RemovesSession()`
  - `testValidateSession_ValidToken_ReturnsTrue()`
  - Reference: [Module Breakdown](./plan.md#backend-modules) "Auth Module"
- [ ] T020 [P] [US1] Create `backend/src/test/java/com/banking/controller/AuthControllerTest.java` using MockMvc with tests:
  - `testLogin_ValidCredentials_Returns200()`
  - `testLogin_EmptyUsername_Returns400()`
  - `testLogin_EmptyPassword_Returns400()`
  - `testLogout_ValidSession_Returns200()`
  - `testLogout_NoSession_Returns401()`
  - Reference: [contracts/api.yaml](./contracts/api.yaml) "/auth/login" and "/auth/logout" endpoints
- [ ] T021 [P] [US1] Create `frontend/src/app/core/services/auth.service.spec.ts` with tests:
  - `should store token after login`
  - `should mark user as authenticated after login`
  - `should return true for isAuthenticated when token exists`
  - `should clear token and redirect on logout`
  - Reference: [Core Services](./plan.md#core-services-frontendcoreservices) "AuthService"
- [ ] T022 [P] [US1] Create `frontend/src/app/core/guards/auth.guard.spec.ts` with tests:
  - `should allow navigation when authenticated`
  - `should redirect to login when not authenticated`
- [ ] T023 [P] [US1] Create `frontend/src/app/features/auth/login/login.component.spec.ts` with tests:
  - `should call authService.login on form submit`
  - `should navigate to dashboard on successful login`
  - `should display error on failed login`
  - `should disable submit button when form invalid`

**Run tests - ALL should FAIL (RED phase)**

### Implementation for User Story 1

- [ ] T024 [P] [US1] Create UserRepository in `backend/src/main/java/com/banking/repository/UserRepository.java` extending JpaRepository with `Optional<User> findByUsername(String username)` method
- [ ] T025 [P] [US1] Create LoginRequest DTO in `backend/src/main/java/com/banking/dto/LoginRequest.java` with username and password fields per [contracts/api.yaml](./contracts/api.yaml) "LoginRequest"
- [ ] T026 [P] [US1] Create LoginResponse DTO in `backend/src/main/java/com/banking/dto/LoginResponse.java` with token, userId, username fields per [contracts/api.yaml](./contracts/api.yaml) "LoginResponse"
- [ ] T027 [P] [US1] Create ErrorResponse DTO in `backend/src/main/java/com/banking/dto/ErrorResponse.java` with message, timestamp fields per [contracts/api.yaml](./contracts/api.yaml) "ErrorResponse"
- [ ] T028 [US1] Implement AuthService in `backend/src/main/java/com/banking/service/AuthService.java` with methods:
  - `LoginResponse login(String username, String password)` - validate non-empty, create session token, find or create user
  - `boolean validateSession(String token)` - check token exists in session store
  - `void logout(String token)` - remove from session store
  - Reference: [Data Flow](./plan.md#flow-1-user-login-priority-p1) for step-by-step logic
  - Reference: [Module Breakdown](./plan.md#backend-modules) "Auth Module" for validation rules
- [ ] T029 [US1] Implement AuthController in `backend/src/main/java/com/banking/controller/AuthController.java` with:
  - `POST /api/auth/login` endpoint
  - `POST /api/auth/logout` endpoint
  - Extract session token from `X-Session-Token` header for logout
  - Reference: [contracts/api.yaml](./contracts/api.yaml) paths section for request/response structure
- [ ] T030 [P] [US1] Create AuthService in `frontend/src/app/core/services/auth.service.ts` with methods:
  - `login(username: string, password: string): Observable<LoginResponse>` using HttpClient
  - `logout(): void` - clear token, navigate to login
  - `isAuthenticated(): boolean` - check token exists
  - `getToken(): string | null` - return current token
  - Use `inject(HttpClient)` for dependency injection per retrieved Angular documentation
  - Reference: [Data Flow](./plan.md#flow-1-user-login-priority-p1) for frontend logic
- [ ] T031 [P] [US1] Create AuthGuard in `frontend/src/app/core/guards/auth.guard.ts` implementing `CanActivateFn`:
  - Check `authService.isAuthenticated()`
  - Redirect to `/login` if not authenticated
  - Reference: [Layer Responsibilities](./plan.md#layer-responsibilities) "Frontend - Guards"
- [ ] T032 [P] [US1] Create AuthInterceptor in `frontend/src/app/core/interceptors/auth.interceptor.ts`:
  - Add `X-Session-Token` header to all requests using `authService.getToken()`
  - Catch 401 errors, call `authService.logout()`, redirect to login
  - Reference: [Session Management Flow](./plan.md#session-management-flow) for error handling
- [ ] T033 [US1] Create LoginComponent in `frontend/src/app/features/auth/login/login.component.ts`:
  - Use `FormBuilder` to create reactive form with username and password controls
  - Add `Validators.required` to both fields
  - Implement `onSubmit()` calling `authService.login()`
  - Navigate to `/dashboard` on success
  - Display error message on failure
  - Reference: Retrieved Angular documentation for reactive forms pattern
- [ ] T034 [US1] Create LoginComponent template in `frontend/src/app/features/auth/login/login.component.html`:
  - Form with username and password inputs
  - Submit button disabled when form invalid
  - Error message display area
  - Apply Tailwind CSS classes: `rounded border px-4 py-2` for inputs, `bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded` for button
  - Reference: Retrieved Tailwind CSS documentation for form styling
- [ ] T035 [US1] Configure routing in `frontend/src/app/app-routing.module.ts`:
  - `/login` → LoginComponent (no guard)
  - `/dashboard` → DashboardComponent with `canActivate: [authGuard]`
  - `/` → redirect to `/login`
  - Reference: Retrieved Angular documentation for router configuration with guards
- [ ] T036 [US1] Provide AuthInterceptor in `frontend/src/app/app.config.ts` using `provideHttpClient(withInterceptors([authInterceptor]))`

**Run all US1 tests - should now PASS (GREEN phase)**

**Integration Test**: 
1. Start backend: `cd backend && ./gradlew bootRun`
2. Start frontend: `cd frontend && ng serve`
3. Navigate to http://localhost:4200
4. Enter username "demo" and password "test"
5. Verify redirect to /dashboard
6. Verify session persists on page refresh
7. Test logout and verify redirect to /login

**Checkpoint**: User Story 1 complete - users can log in, access dashboard, and maintain session

---

## Phase 4: User Story 2 - Account Overview (Priority: P2)

**Goal**: Display all user accounts with current balances and show 10 most recent transactions

**Independent Test**: Log in, verify dashboard loads within 2 seconds showing all accounts with USD-formatted balances and 10 recent transactions ordered by date descending

### Tests for User Story 2 (TDD - Write FIRST, ensure FAIL)

- [ ] T037 [P] [US2] Create `backend/src/test/java/com/banking/repository/AccountRepositoryTest.java` using @DataJpaTest:
  - `testFindByUserId_ReturnsUserAccounts()`
  - `testFindByUserId_EmptyListForNonexistentUser()`
- [ ] T038 [P] [US2] Create `backend/src/test/java/com/banking/repository/TransactionRepositoryTest.java` using @DataJpaTest:
  - `testFindTop10ByAccountIdOrderByTransactionDateDesc_Returns10Transactions()`
  - `testFindTop10ByAccountIdOrderByTransactionDateDesc_OrderedCorrectly()`
- [ ] T039 [P] [US2] Create `backend/src/test/java/com/banking/service/AccountServiceTest.java` with tests:
  - `testGetAccountsByUser_ReturnsUserAccounts()`
  - `testGetAccountsByUser_FormatsBalanceCorrectly()` - verify 2 decimal places
  - `testGetAccountsByUser_EmptyListForNonexistentUser()`
- [ ] T040 [P] [US2] Create `backend/src/test/java/com/banking/service/TransactionServiceTest.java` with tests:
  - `testGetRecentTransactions_ReturnsLast10()`
  - `testGetRecentTransactions_OrderedByDateDesc()`
  - `testGetRecentTransactions_FormatsAmountCorrectly()` - verify 2 decimal USD format
- [ ] T041 [P] [US2] Create `backend/src/test/java/com/banking/controller/AccountControllerTest.java` using MockMvc:
  - `testGetAccounts_ValidSession_Returns200()`
  - `testGetAccounts_NoSession_Returns401()`
  - `testGetAccounts_ReturnsCorrectJsonStructure()`
- [ ] T042 [P] [US2] Create `backend/src/test/java/com/banking/controller/TransactionControllerTest.java` using MockMvc:
  - `testGetRecentTransactions_ValidSession_Returns200()`
  - `testGetRecentTransactions_NoSession_Returns401()`
  - `testGetRecentTransactions_LimitsTo10()`
- [ ] T043 [P] [US2] Create `frontend/src/app/core/services/account.service.spec.ts` with tests:
  - `should fetch accounts from API`
  - `should include session token in request`
  - `should handle 401 error`
- [ ] T044 [P] [US2] Create `frontend/src/app/core/services/transaction.service.spec.ts` with tests:
  - `should fetch recent transactions with limit parameter`
  - `should handle empty transaction list`
- [ ] T045 [P] [US2] Create `frontend/src/app/features/dashboard/dashboard.component.spec.ts` with tests:
  - `should load accounts and transactions on init using forkJoin`
  - `should display loading state while fetching`
  - `should handle errors gracefully`
- [ ] T046 [P] [US2] Create `frontend/src/app/features/dashboard/accounts-list/accounts-list.component.spec.ts` with tests:
  - `should display all accounts`
  - `should format balance as USD with $ symbol and 2 decimals`
  - `should show account type and number`
- [ ] T047 [P] [US2] Create `frontend/src/app/features/dashboard/transactions-list/transactions-list.component.spec.ts` with tests:
  - `should display all transactions`
  - `should format amount as USD`
  - `should show empty message when no transactions`

**Run tests - ALL should FAIL (RED phase)**

### Implementation for User Story 2

- [ ] T048 [P] [US2] Create AccountRepository in `backend/src/main/java/com/banking/repository/AccountRepository.java` extending JpaRepository with `List<Account> findByUserId(Long userId)` method
- [ ] T049 [P] [US2] Create TransactionRepository in `backend/src/main/java/com/banking/repository/TransactionRepository.java` extending JpaRepository with `List<Transaction> findTop10ByAccountIdOrderByTransactionDateDesc(Long accountId)` method per [data-model.md](./data-model.md) "Display Rules"
- [ ] T050 [P] [US2] Create AccountDto in `backend/src/main/java/com/banking/dto/AccountDto.java` with fields: id, accountType, accountNumber, balance per [contracts/api.yaml](./contracts/api.yaml) "AccountDto"
- [ ] T051 [P] [US2] Create TransactionDto in `backend/src/main/java/com/banking/dto/TransactionDto.java` with fields: id, accountId, amount, description, note, transactionDate per [contracts/api.yaml](./contracts/api.yaml) "TransactionDto"
- [ ] T052 [US2] Implement AccountService in `backend/src/main/java/com/banking/service/AccountService.java`:
  - `List<AccountDto> getAccountsByUser(Long userId)` - fetch accounts, map to DTOs with balance formatted to 2 decimals
  - Reference: [Module Breakdown](./plan.md#backend-modules) "Accounts Module" for method signature
  - Reference: [Data Flow](./plan.md#flow-2-view-dashboard-priority-p2) for logic
- [ ] T053 [US2] Implement TransactionService in `backend/src/main/java/com/banking/service/TransactionService.java`:
  - `List<TransactionDto> getRecentTransactions(Long userId, int limit)` - fetch last 10 transactions per account, map to DTOs
  - Reference: [Module Breakdown](./plan.md#backend-modules) "Transactions Module"
  - Reference: Spec clarification "Last 10 transactions regardless of date"
- [ ] T054 [US2] Implement AccountController in `backend/src/main/java/com/banking/controller/AccountController.java`:
  - `GET /api/accounts` endpoint returning `List<AccountDto>`
  - Extract userId from session token
  - Add @CrossOrigin annotation
  - Reference: [contracts/api.yaml](./contracts/api.yaml) "/accounts" endpoint
- [ ] T055 [US2] Implement TransactionController in `backend/src/main/java/com/banking/controller/TransactionController.java`:
  - `GET /api/transactions/recent?limit=10` endpoint returning `List<TransactionDto>`
  - Extract userId from session token
  - Reference: [contracts/api.yaml](./contracts/api.yaml) "/transactions/recent" endpoint
- [ ] T056 [P] [US2] Create AccountService in `frontend/src/app/core/services/account.service.ts`:
  - `getAccounts(): Observable<Account[]>` calling `GET /api/accounts`
  - Use `inject(HttpClient)` for dependency injection
  - Reference: [Core Services](./plan.md#core-services-frontendcoreservices) "AccountService"
- [ ] T057 [P] [US2] Create TransactionService in `frontend/src/app/core/services/transaction.service.ts`:
  - `getRecentTransactions(limit: number): Observable<Transaction[]>` calling `GET /api/transactions/recent?limit={limit}`
  - Reference: [Core Services](./plan.md#core-services-frontendcoreservices) "TransactionService"
- [ ] T058 [US2] Create DashboardComponent in `frontend/src/app/features/dashboard/dashboard.component.ts`:
  - Use `inject()` for AccountService and TransactionService
  - In `ngOnInit()`, use `forkJoin()` to load accounts and transactions in parallel
  - Store results in component properties
  - Handle loading and error states
  - Reference: [Data Flow](./plan.md#flow-2-view-dashboard-priority-p2) for parallel API calls
  - Reference: Retrieved Angular documentation for forkJoin pattern
- [ ] T059 [US2] Create DashboardComponent template in `frontend/src/app/features/dashboard/dashboard.component.html`:
  - Grid layout using Tailwind: `grid grid-cols-1 md:grid-cols-2 gap-6`
  - Pass accounts to `<app-accounts-list [accounts]="accounts">`
  - Pass transactions to `<app-transactions-list [transactions]="transactions">`
  - Add "Transfer Money" button (no functionality yet)
  - Reference: Retrieved Tailwind CSS documentation for responsive grid layout
- [ ] T060 [US2] Create AccountsListComponent in `frontend/src/app/features/dashboard/accounts-list/accounts-list.component.ts`:
  - `@Input() accounts: Account[]`
  - Pure presentation component (no services)
- [ ] T061 [US2] Create AccountsListComponent template in `frontend/src/app/features/dashboard/accounts-list/accounts-list.component.html`:
  - Display account cards using `@for (account of accounts; track account.id)`
  - Show account type, number, and balance
  - Format balance using Angular CurrencyPipe: `{{ account.balance | currency:'USD':'symbol':'1.2-2' }}`
  - Apply Tailwind card styles: `bg-white rounded-lg shadow-md p-6`
  - Reference: Retrieved Tailwind CSS documentation for card components
  - Reference: [Frontend Modules](./plan.md#frontend-modules) "Dashboard Module"
- [ ] T062 [US2] Create TransactionsListComponent in `frontend/src/app/features/dashboard/transactions-list/transactions-list.component.ts`:
  - `@Input() transactions: Transaction[]`
  - Pure presentation component
- [ ] T063 [US2] Create TransactionsListComponent template in `frontend/src/app/features/dashboard/transactions-list/transactions-list.component.html`:
  - Display table with columns: Date, Description, Amount
  - Use `@for (tx of transactions; track tx.id)` for rows
  - Format amount with CurrencyPipe
  - Apply Tailwind table styles: `table-auto w-full`
  - Show "No recent transactions" message with `@empty` block
  - Reference: Retrieved Tailwind CSS documentation for table styling
  - Reference: [Frontend Modules](./plan.md#frontend-modules) "TransactionsListComponent"

**Run all US2 tests - should now PASS (GREEN phase)**

**Integration Test**:
1. Ensure backend and frontend running
2. Log in with sample user credentials
3. Verify dashboard loads within 2 seconds (SC-005)
4. Verify all accounts displayed with balances in USD format: $1,234.56
5. Verify 10 most recent transactions shown
6. Verify account types (CHECKING/SAVINGS) and account numbers visible
7. Verify transaction dates, descriptions, and amounts formatted correctly

**Checkpoint**: User Stories 1 AND 2 complete - users can log in and view complete account overview with transaction history

---

## Phase 5: User Story 3 - Money Transfer Between Accounts (Priority: P3)

**Goal**: Enable users to transfer money between their own accounts with validation and immediate balance updates

**Independent Test**: Log in, initiate transfer from checking to savings, verify balances update immediately and transfer appears in transaction history for both accounts

### Tests for User Story 3 (TDD - Write FIRST, ensure FAIL)

- [ ] T064 [P] [US3] Create `backend/src/test/java/com/banking/repository/TransferRepositoryTest.java` using @DataJpaTest:
  - `testSave_CreateTransfer_Success()`
  - `testFindByUserId_ReturnsUserTransfers()`
- [ ] T065 [P] [US3] Create `backend/src/test/java/com/banking/service/TransferServiceTest.java` with comprehensive tests:
  - `testExecuteTransfer_ValidRequest_UpdatesBalances()` - verify both accounts updated
  - `testExecuteTransfer_InsufficientFunds_ThrowsException()` - verify FR-010
  - `testExecuteTransfer_SameAccount_ThrowsException()` - verify FR-012
  - `testExecuteTransfer_NegativeAmount_ThrowsException()` - verify FR-011
  - `testExecuteTransfer_ZeroAmount_ThrowsException()` - verify FR-011
  - `testExecuteTransfer_CreatesTransactionRecords()` - verify FR-015
  - `testExecuteTransfer_ConcurrentRequests_ProcessedSequentially()` - verify sequential processing per spec clarification
  - `testExecuteTransfer_AccountsNotOwnedByUser_ThrowsException()` - verify validation
  - Reference: [Module Breakdown](./plan.md#backend-modules) "Transfers Module"
  - Reference: [Data Flow](./plan.md#flow-3-money-transfer-priority-p3) for all validation rules
- [ ] T066 [P] [US3] Create `backend/src/test/java/com/banking/controller/TransferControllerTest.java` using MockMvc:
  - `testCreateTransfer_ValidRequest_Returns200()`
  - `testCreateTransfer_InsufficientFunds_Returns400()`
  - `testCreateTransfer_NoSession_Returns401()`
  - `testCreateTransfer_SessionExpiredDuringTransfer_Returns401AndDiscardsTransfer()` - verify FR-017
  - `testCreateTransfer_ReturnsUpdatedBalances()`
- [ ] T067 [P] [US3] Create `frontend/src/app/core/services/transfer.service.spec.ts` with tests:
  - `should create transfer via POST /api/transfers`
  - `should handle 400 validation errors`
  - `should handle 401 session expiry`
- [ ] T068 [P] [US3] Create `frontend/src/app/features/transfer/transfer-dialog/transfer-dialog.component.spec.ts` with tests:
  - `should validate source != destination` - verify FR-012
  - `should validate amount > 0` - verify FR-011
  - `should validate amount <= source balance` - verify FR-010
  - `should call transferService on submit`
  - `should close dialog on successful transfer`
  - `should display error message on failure`
  - `should filter destination dropdown to exclude source account` - verify FR-009
  - Reference: [Frontend Modules](./plan.md#frontend-modules) "Transfer Module"

**Run tests - ALL should FAIL (RED phase)**

### Implementation for User Story 3

- [ ] T069 [P] [US3] Create TransferRepository in `backend/src/main/java/com/banking/repository/TransferRepository.java` extending JpaRepository
- [ ] T070 [P] [US3] Create TransferRequest DTO in `backend/src/main/java/com/banking/dto/TransferRequest.java` with fields: sourceAccountId, destinationAccountId, amount, note per [contracts/api.yaml](./contracts/api.yaml) "TransferRequest"
- [ ] T071 [P] [US3] Create TransferResponse DTO in `backend/src/main/java/com/banking/dto/TransferResponse.java` with fields: success, transferId, updatedAccounts per [contracts/api.yaml](./contracts/api.yaml) "TransferResponse"
- [ ] T072 [US3] Implement TransferService in `backend/src/main/java/com/banking/service/TransferService.java`:
  - `@Transactional public synchronized TransferResponse executeTransfer(TransferRequest request, Long userId)` - CRITICAL: synchronized for sequential processing per [research.md](./research.md) "Sequential Transfer Processing"
  - Step 1: Load source and destination accounts using AccountRepository
  - Step 2: Validate using `validateTransfer()` private method checking all rules from [data-model.md](./data-model.md) "Transfer Entity Validation Rules"
  - Step 3: Update account balances atomically
  - Step 4: Create two Transaction records (debit for source, credit for destination) per [Data Flow](./plan.md#flow-3-money-transfer-priority-p3) step 8c
  - Step 5: Create Transfer record linking transactions
  - Step 6: Save all entities (accounts, transactions, transfer)
  - Step 7: Return TransferResponse with updated account balances
  - Reference: [Module Breakdown](./plan.md#backend-modules) "Transfers Module" for complete implementation details
  - Reference: Retrieved Spring Data JPA documentation for @Transactional pattern
- [ ] T073 [US3] Implement TransferController in `backend/src/main/java/com/banking/controller/TransferController.java`:
  - `POST /api/transfers` endpoint accepting TransferRequest
  - Extract userId from session token in `X-Session-Token` header
  - Return 401 if session invalid/expired per FR-017
  - Return 400 for validation failures per FR-019
  - Reference: [contracts/api.yaml](./contracts/api.yaml) "/transfers" endpoint
- [ ] T074 [P] [US3] Create TransferService in `frontend/src/app/core/services/transfer.service.ts`:
  - `createTransfer(request: TransferRequest): Observable<TransferResponse>` calling `POST /api/transfers`
  - Use `inject(HttpClient)`
  - Handle errors with retry logic (1 retry) using RxJS `retry(1)` operator
  - Reference: [Core Services](./plan.md#core-services-frontendcoreservices) "TransferService"
  - Reference: Retrieved Angular documentation for HttpClient with RxJS operators
- [ ] T075 [US3] Create TransferDialogComponent in `frontend/src/app/features/transfer/transfer-dialog/transfer-dialog.component.ts`:
  - `@Input() accounts: Account[]` - receive from DashboardComponent
  - Use FormBuilder to create form with controls: sourceAccountId, destinationAccountId, amount, note
  - Add custom validator for source != destination per FR-012
  - Add validator for amount > 0 per FR-011
  - Add validator for amount <= source account balance per FR-010
  - Implement `onSubmit()` calling transferService.createTransfer()
  - Emit event on success to refresh dashboard
  - Handle session expiry (401) by showing message and redirecting per FR-017
  - Reference: [Data Flow](./plan.md#flow-3-money-transfer-priority-p3) for validation logic
  - Reference: Retrieved Angular documentation for custom validators
- [ ] T076 [US3] Create TransferDialogComponent template in `frontend/src/app/features/transfer/transfer-dialog/transfer-dialog.component.html`:
  - Modal/dialog structure with Tailwind styling
  - Source account dropdown - populate from accounts input
  - Destination account dropdown - filter to exclude selected source account per FR-009
  - Amount input with type="number" step="0.01" min="0.01" for 2 decimal precision
  - Optional note textarea (max 500 characters)
  - Real-time validation error messages for each field
  - Submit button disabled when form invalid or submitting
  - Cancel button to close dialog
  - Apply Tailwind form styles: `rounded border px-4 py-2` for inputs, `bg-green-500 hover:bg-green-600 text-white px-4 py-2 rounded` for submit button
  - Reference: [Frontend Modules](./plan.md#frontend-modules) "TransferDialogComponent"
  - Reference: Retrieved Tailwind CSS documentation for form and modal styling
- [ ] T077 [US3] Update DashboardComponent in `frontend/src/app/features/dashboard/dashboard.component.ts`:
  - Add method `openTransferDialog()` to show TransferDialogComponent
  - Add method `onTransferComplete()` to handle successful transfer callback
  - In `onTransferComplete()`: call `loadDashboardData()` to refresh accounts and transactions
  - Wire transfer dialog close event to trigger `onTransferComplete()` on success
  - Reference: [Data Flow](./plan.md#flow-3-money-transfer-priority-p3) steps 10-11
- [ ] T078 [US3] Update DashboardComponent template in `frontend/src/app/features/dashboard/dashboard.component.html`:
  - Wire "Transfer Money" button to call `openTransferDialog()`
  - Apply Tailwind button styles: `bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-4 rounded`

**Run all US3 tests - should now PASS (GREEN phase)**

**Integration Test**:
1. Ensure backend and frontend running
2. Log in with sample user having multiple accounts
3. Click "Transfer Money" button - dialog should open
4. Select source account (e.g., Checking with $5000 balance)
5. Select destination account (e.g., Savings) - verify source not in dropdown
6. Enter amount $500.00
7. Add optional note "Test transfer"
8. Submit transfer
9. Verify dialog closes
10. Verify source balance decreased by $500.00 immediately (SC-003)
11. Verify destination balance increased by $500.00 immediately
12. Verify transfer appears in transaction history for both accounts (SC-007)
13. Verify transfer completion under 30 seconds (SC-002)
14. Test edge cases:
    - Try amount > source balance - verify validation error (SC-006)
    - Try negative amount - verify validation error
    - Try source == destination - verify validation error
    - Try zero amount - verify validation error

**Checkpoint**: All user stories complete - full banking functionality operational

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Improvements affecting multiple user stories and final verification

- [ ] T079 [P] Add comprehensive error messages for all validation failures per FR-018 and FR-019:
  - Backend: Create custom exception classes and @ControllerAdvice for global error handling
  - Frontend: Display user-friendly error messages for each validation scenario using templates from [spec.md](./spec.md) "Error Message Templates" section
  - Verify all 7 transfer validation scenarios and 2 authentication scenarios display correct messages
- [ ] T080 [P] Add loading spinners to all components during API calls:
  - DashboardComponent: Show spinner while fetching accounts/transactions
  - TransferDialogComponent: Disable form and show spinner during transfer submission
  - Apply Tailwind spinner styles
- [ ] T081 [P] Implement session expiry handling per FR-017:
  - Backend: Add session timeout mechanism (e.g., 30 minutes)
  - Frontend: AuthInterceptor already handles 401, verify redirect and discard in-progress transfers
  - Add toast/notification: "Session expired. Please log in again."
- [ ] T082 [P] Add responsive design verification:
  - Test all components on mobile viewport (375px width)
  - Verify Tailwind responsive classes work: `sm:`, `md:`, `lg:`
  - Adjust grid layouts for mobile if needed
- [ ] T083 [P] Add accessibility improvements:
  - ARIA labels on form inputs
  - Keyboard navigation support
  - Focus management in dialogs
- [ ] T084 Performance verification per Success Criteria:
  - Test dashboard load time < 2 seconds (SC-005)
  - Test transfer balance update < 1 second (SC-003)
  - Test login to dashboard < 10 seconds (SC-001)
  - Verify 95% success rate for first-time transfers (SC-004): Execute 10 test scenarios with valid transfer data (different amounts, account combinations), record successful submissions without validation errors, calculate success rate (target: ≥9.5/10)
- [ ] T085 [P] Run comprehensive validation and testing:
  - **Test Suite Execution**:
    - Backend: `cd backend && ./gradlew test` - verify all tests pass and coverage >80% (TDD compliance)
    - Frontend: `cd frontend && ng test` - verify all tests pass
  - **Functional Requirements Validation**:
    - Cross-reference FR-001 through FR-019 in [spec.md](./spec.md) with implemented features
    - Mark each as ✅ in tracking document
  - **Success Criteria Validation**:
    - Verify SC-001 through SC-008 measurements documented
    - Document test results for each criterion
- [ ] T086 Run quickstart.md validation:
  - Follow all steps in [quickstart.md](./quickstart.md) from clean environment
  - Verify application runs successfully
  - Document any missing steps or errors
- [ ] T087 Final acceptance testing:
  - Execute all acceptance scenarios from User Stories 1, 2, 3 in [spec.md](./spec.md)
  - Verify all edge cases handled gracefully
  - Document any deviations or issues
- [ ] T088 [P] API contract validation:
  - Validate implemented endpoints against [contracts/api.yaml](./contracts/api.yaml) OpenAPI schema
  - Verify request/response structures match schemas for all 5 endpoints:
    - POST /api/auth/login (LoginRequest → LoginResponse)
    - POST /api/auth/logout
    - GET /api/accounts (→ AccountDto[])
    - GET /api/transactions/recent (→ TransactionDto[])
    - POST /api/transfers (TransferRequest → TransferResponse)
  - Verify error response structures match ErrorResponse schema
  - Document any deviations
- [ ] T089 Constitution compliance verification:
  - Verify no features outside spec (Principle III)
  - Verify no security hardening beyond MVP (Principle III)
  - Verify no performance optimization beyond requirements (Principle III)
  - Verify simplest possible implementation (Principle IV)

**Final Checkpoint**: Application complete, tested, and ready for deployment

**Note**: Task consolidation reduced total from 90 to 89 tasks (T085-T087 merged into comprehensive validation task T085; new T088 added for API contract validation)

**Note**: Task consolidation reduced total from 90 to 89 tasks (T085-T087 merged into single comprehensive validation task T085)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup (Phase 1) completion - BLOCKS all user stories
- **User Story 1 (Phase 3)**: Depends on Foundational (Phase 2) - Authentication gateway
- **User Story 2 (Phase 4)**: Depends on Foundational (Phase 2) - Can start in parallel with US1 if team capacity allows
- **User Story 3 (Phase 5)**: Depends on Foundational (Phase 2) - Can start in parallel with US1/US2 if team capacity allows
- **Polish (Phase 6)**: Depends on all desired user stories being complete

### User Story Independence

All user stories are designed to be independently implementable and testable after Phase 2 completes:

- **US1 (Authentication)**: Fully independent - provides login and session management
- **US2 (Account Overview)**: Depends on US1 for authentication but features are independent - can be tested by logging in first
- **US3 (Transfers)**: Depends on US1 for authentication and US2 for account data display but transfer functionality is independent - can be tested by logging in and using transfer dialog directly

### Recommended Sequence

**Sequential (Single Developer)**:
1. Complete Phase 1 (Setup)
2. Complete Phase 2 (Foundational) - CHECKPOINT
3. Complete Phase 3 (US1) - CHECKPOINT: Login working
4. Complete Phase 4 (US2) - CHECKPOINT: Dashboard working
5. Complete Phase 5 (US3) - CHECKPOINT: Transfers working
6. Complete Phase 6 (Polish) - FINAL CHECKPOINT

**Parallel (Team of 3)**:
1. All: Complete Phase 1 (Setup) together
2. All: Complete Phase 2 (Foundational) together - CHECKPOINT
3. Split work:
   - Developer A: Phase 3 (US1) - Authentication
   - Developer B: Phase 4 (US2) - Account Overview (backend tasks T037-T055)
   - Developer C: Phase 4 (US2) - Account Overview (frontend tasks T056-T063)
4. After US1 complete: Developer A joins US3
5. Integration testing once all stories complete
6. All: Phase 6 (Polish) together

### Within Each User Story (TDD Workflow)

**CRITICAL**: Tests MUST be written first and FAIL before implementation begins

1. **Write Tests** - All test tasks marked [P] can be written in parallel
2. **Run Tests** - Verify all tests FAIL (RED phase) - DO NOT PROCEED until tests fail
3. **Implement Code** - Follow task order:
   - DTOs/Models (can be parallel)
   - Repositories (can be parallel)
   - Services (may depend on repositories)
   - Controllers/Components (depend on services)
4. **Run Tests** - Verify all tests PASS (GREEN phase)
5. **Integration Test** - Manual verification of user journey
6. **Checkpoint** - Mark story complete before moving to next

### Parallel Opportunities

Within each phase, all tasks marked `[P]` can be executed in parallel:

**Phase 1 (Setup)**: T003, T004, T005, T006 (4 parallel)
**Phase 2 (Foundational)**: T008-T018 (11 parallel tasks after T007)
**Phase 3 (US1 Tests)**: T019-T023 (5 parallel)
**Phase 3 (US1 Implementation)**: T024-T027, T030-T032 (7 parallel after dependencies met)
**Phase 4 (US2 Tests)**: T037-T047 (11 parallel)
**Phase 4 (US2 Implementation)**: T048-T051, T056-T057, T060, T062 (8 parallel tasks)
**Phase 5 (US3 Tests)**: T064-T068 (5 parallel)
**Phase 5 (US3 Implementation)**: T069-T071, T074 (4 parallel)
**Phase 6 (Polish)**: T079-T082, T085, T088 (6 parallel tasks)

---

## Parallel Example: User Story 1 Backend Tests

After Foundational phase complete, all US1 backend tests can be written simultaneously by different developers or AI agents:

```bash
# Developer/Agent 1
# T019: Write AuthServiceTest.java

# Developer/Agent 2
# T020: Write AuthControllerTest.java

# Both can work in parallel since they test different layers
```

---

## Parallel Example: User Story 2 Implementation

After US2 tests written and failing, backend and frontend can be developed in parallel:

```bash
# Backend Team (Developer A)
# T048-T055: Create repositories, DTOs, services, controllers

# Frontend Team (Developer B & C)
# T056-T063: Create services, components, templates

# Both streams are independent and can merge after completion
```

---

## Success Criteria Verification Map

Each success criterion maps to specific test tasks:

- **SC-001**: Login to dashboard <10s → T023 (LoginComponent test)
- **SC-002**: Transfer completion <30s → T068 (TransferDialog test)
- **SC-003**: Balance update <1s → T065 (TransferService test for immediate update)
- **SC-004**: 95% first-time success → T068 (validation tests prevent errors)
- **SC-005**: Dashboard load <2s → T045 (DashboardComponent test)
- **SC-006**: 100% invalid transfer prevention → T065 (all validation scenarios)
- **SC-007**: View 10 recent transactions → T040, T047 (TransactionService and component tests)
- **SC-008**: No data loss → T065 (TransferService atomic transaction test)

---

## Functional Requirements Traceability

Each FR maps to specific implementation tasks:

- **FR-001**: T033, T034 (LoginComponent)
- **FR-002**: T028 (AuthService validation)
- **FR-003**: T031 (AuthGuard)
- **FR-004**: T054 (AccountController)
- **FR-005**: T052, T061 (AccountService and display with USD format)
- **FR-006**: T053, T063 (TransactionService and display)
- **FR-007**: T077 (Dashboard transfer button)
- **FR-008**: T075, T076 (TransferDialog source selection)
- **FR-009**: T075, T076 (TransferDialog destination filtering)
- **FR-010**: T072, T075 (TransferService and client validation for balance)
- **FR-011**: T072, T075 (Amount validation >0, 2 decimals)
- **FR-012**: T072, T075 (Source != Destination validation)
- **FR-013**: T075, T076 (Optional note field)
- **FR-014**: T072 (Atomic transfer with @Transactional and synchronized)
- **FR-015**: T072 (Transaction record creation)
- **FR-016**: T030, T031, T032 (Session persistence via AuthService and AuthInterceptor)
- **FR-017**: T081 (Session expiry handling)
- **FR-018**: T079 (Login error messages)
- **FR-019**: T079 (Transfer error messages)

---

## Total Task Count: 89 tasks

**Breakdown by Phase**:
- Phase 1 (Setup): 6 tasks
- Phase 2 (Foundational): 12 tasks
- Phase 3 (US1): 23 tasks (5 test + 18 implementation)
- Phase 4 (US2): 27 tasks (11 test + 16 implementation)
- Phase 5 (US3): 15 tasks (5 test + 10 implementation)
- Phase 6 (Polish): 6 tasks (consolidated validation)

**Parallel Opportunities**: 56 tasks marked [P] can execute in parallel (63% of total)

**Task Consolidation Notes**:
- T085-T087 merged into single comprehensive validation task (T085)
- T088 added for API contract validation
- T089 renumbered from T090 (Constitution compliance)

**Estimated Timeline** (single developer, sequential):
- Phase 1: 1 day
- Phase 2: 2 days
- Phase 3: 4 days
- Phase 4: 5 days
- Phase 5: 3 days
- Phase 6: 2 days
- **Total**: ~17 days (assuming 4-6 tasks per day)

**Estimated Timeline** (team of 3, parallel where possible):
- Phase 1: 0.5 days
- Phase 2: 1 day
- Phases 3-5 (parallel): 5 days
- Phase 6: 1 day
- **Total**: ~7.5 days

---

## MVP Scope Recommendation

**Minimum Viable Product**: Complete Phase 1, 2, and 3 (User Story 1 only)

This delivers:
- ✅ Project setup and infrastructure
- ✅ User authentication and session management
- ✅ Login functionality
- ✅ Protected routing

**Total MVP Tasks**: 41 tasks (~8 days single developer, ~3 days team)

**Incremental Delivery**:
- **Sprint 1**: MVP (US1) - Login capability
- **Sprint 2**: US2 - Account viewing
- **Sprint 3**: US3 - Money transfers
- **Sprint 4**: Polish

This allows early user feedback and demonstrates working software at each sprint boundary.
