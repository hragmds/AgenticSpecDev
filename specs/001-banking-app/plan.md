# Implementation Plan: Personal Banking Application

**Branch**: `001-banking-app` | **Date**: November 19, 2025 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/001-banking-app/spec.md`

## Summary

Build a personal banking application enabling users to authenticate, view account balances and transaction history, and transfer money between their own accounts. The application follows a three-tier web architecture with Angular frontend, Spring Boot REST API backend, and SQLite database for persistence.

## Technical Context

**Language/Version**: 
- Backend: Java 17+
- Frontend: TypeScript (Angular 17+)

**Primary Dependencies**: 
- Backend: Spring Boot 3.x, Spring Data JPA, Gradle 8.x
- Frontend: Angular 17+, Tailwind CSS 3.x, RxJS

**Storage**: SQLite (embedded, file-based relational database)

**Testing**: 
- Backend: JUnit 5, Spring Boot Test, MockMvc
- Frontend: Jasmine, Karma, Angular Testing Library

**Target Platform**: Web browser (Chrome, Firefox, Safari latest versions) + JVM runtime

**Project Type**: Web application (frontend + backend)

**Performance Goals**: 
- Dashboard load: <2 seconds
- Transfer completion: <1 second for balance updates
- Login to dashboard: <10 seconds

**Constraints**: 
- Simple/fake authentication (no real security hardening)
- Sequential transfer processing (no optimistic locking or advanced concurrency)
- MVP scope only (no performance optimization, caching, or advanced monitoring)

**Scale/Scope**: 
- Single-user demo application
- ~10 backend endpoints
- ~5 Angular components
- ~4 database tables

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### Principle I: Specification-Driven Development ✅
- All features directly map to spec.md requirements (FR-001 through FR-019)
- User stories (P1: Auth, P2: Account Overview, P3: Transfers) drive implementation order
- No features planned outside the specification

### Principle II: Test-Driven Development ✅
- Backend: JUnit tests written first for each service/controller method
- Frontend: Jasmine/Karma tests for each component and service
- Integration tests for API contracts
- Red → Green → Refactor cycle enforced

### Principle III: MVP Scope Only ✅
- No security hardening beyond input validation (simple/fake auth per spec)
- No performance optimization (no caching, no database indexing beyond SQLite defaults)
- No "nice-to-have" features (no password recovery, account creation, etc.)
- Every feature maps to a documented requirement

### Principle IV: Simplicity and Testability First ✅
- SQLite: Zero configuration, embedded, testable
- Spring Boot: Convention over configuration, built-in testing support
- Angular: Component-based, dependency injection enables easy mocking
- REST API: Simple, stateless, well-understood patterns
- No complex design patterns beyond standard MVC/component architecture

### Principle V: AI Agent Compliance ✅
- This plan follows all principles I-IV
- Generated code will adhere to constitution requirements
- All outputs subject to compliance verification

**GATE STATUS**: ✅ PASS - No violations detected

## Project Structure

### Documentation (this feature)

```text
specs/001-banking-app/
├── plan.md              # This file
├── research.md          # Phase 0: Technology decisions and patterns
├── data-model.md        # Phase 1: Entity definitions and relationships
├── quickstart.md        # Phase 1: Setup and run instructions
├── contracts/           # Phase 1: API contract definitions (OpenAPI)
│   └── api.yaml
└── tasks.md             # Phase 2: Implementation task breakdown
```

### Source Code (repository root)

```text
banking-app/
├── backend/                          # Spring Boot application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/banking/
│   │   │   │   ├── BankingApplication.java      # Spring Boot main
│   │   │   │   ├── config/
│   │   │   │   │   ├── SecurityConfig.java       # Auth/session config
│   │   │   │   │   └── DatabaseConfig.java       # SQLite JPA config
│   │   │   │   ├── model/                        # JPA entities
│   │   │   │   │   ├── User.java
│   │   │   │   │   ├── Account.java
│   │   │   │   │   ├── Transaction.java
│   │   │   │   │   └── Transfer.java
│   │   │   │   ├── repository/                   # Spring Data repos
│   │   │   │   │   ├── UserRepository.java
│   │   │   │   │   ├── AccountRepository.java
│   │   │   │   │   └── TransactionRepository.java
│   │   │   │   ├── service/                      # Business logic
│   │   │   │   │   ├── AuthService.java          # Login/session
│   │   │   │   │   ├── AccountService.java       # Account queries
│   │   │   │   │   ├── TransactionService.java   # Transaction queries
│   │   │   │   │   └── TransferService.java      # Transfer operations
│   │   │   │   ├── controller/                   # REST endpoints
│   │   │   │   │   ├── AuthController.java       # POST /api/auth/login
│   │   │   │   │   ├── AccountController.java    # GET /api/accounts
│   │   │   │   │   ├── TransactionController.java # GET /api/transactions
│   │   │   │   │   └── TransferController.java   # POST /api/transfers
│   │   │   │   └── dto/                          # Request/response objects
│   │   │   │       ├── LoginRequest.java
│   │   │   │       ├── LoginResponse.java
│   │   │   │       ├── AccountDto.java
│   │   │   │       ├── TransactionDto.java
│   │   │   │       └── TransferRequest.java
│   │   │   └── resources/
│   │   │       ├── application.properties        # Spring config
│   │   │       └── schema.sql                    # SQLite DDL
│   │   └── test/
│   │       └── java/com/banking/
│   │           ├── service/                      # Service unit tests
│   │           │   ├── AuthServiceTest.java
│   │           │   ├── AccountServiceTest.java
│   │           │   └── TransferServiceTest.java
│   │           └── controller/                   # Controller integration tests
│   │               ├── AuthControllerTest.java
│   │               ├── AccountControllerTest.java
│   │               └── TransferControllerTest.java
│   ├── build.gradle                              # Dependencies
│   └── settings.gradle
│
├── frontend/                         # Angular application
│   ├── src/
│   │   ├── app/
│   │   │   ├── core/                             # Singleton services
│   │   │   │   ├── services/
│   │   │   │   │   ├── auth.service.ts           # Auth state/login
│   │   │   │   │   ├── account.service.ts        # Account API calls
│   │   │   │   │   ├── transaction.service.ts    # Transaction API calls
│   │   │   │   │   └── transfer.service.ts       # Transfer API calls
│   │   │   │   ├── guards/
│   │   │   │   │   └── auth.guard.ts             # Route protection
│   │   │   │   ├── interceptors/
│   │   │   │   │   └── auth.interceptor.ts       # Session handling
│   │   │   │   └── models/                       # TypeScript interfaces
│   │   │   │       ├── user.model.ts
│   │   │   │       ├── account.model.ts
│   │   │   │       ├── transaction.model.ts
│   │   │   │       └── transfer.model.ts
│   │   │   ├── features/                         # Feature modules
│   │   │   │   ├── auth/                         # Authentication feature
│   │   │   │   │   ├── login/
│   │   │   │   │   │   ├── login.component.ts
│   │   │   │   │   │   ├── login.component.html
│   │   │   │   │   │   ├── login.component.css
│   │   │   │   │   │   └── login.component.spec.ts
│   │   │   │   │   └── auth.module.ts
│   │   │   │   ├── dashboard/                    # Dashboard feature
│   │   │   │   │   ├── dashboard.component.ts
│   │   │   │   │   ├── dashboard.component.html
│   │   │   │   │   ├── dashboard.component.css
│   │   │   │   │   ├── dashboard.component.spec.ts
│   │   │   │   │   ├── accounts-list/            # Account cards
│   │   │   │   │   │   ├── accounts-list.component.ts
│   │   │   │   │   │   ├── accounts-list.component.html
│   │   │   │   │   │   └── accounts-list.component.spec.ts
│   │   │   │   │   ├── transactions-list/        # Transaction table
│   │   │   │   │   │   ├── transactions-list.component.ts
│   │   │   │   │   │   ├── transactions-list.component.html
│   │   │   │   │   │   └── transactions-list.component.spec.ts
│   │   │   │   │   └── dashboard.module.ts
│   │   │   │   └── transfer/                     # Transfer feature
│   │   │   │       ├── transfer-dialog/
│   │   │   │       │   ├── transfer-dialog.component.ts
│   │   │   │       │   ├── transfer-dialog.component.html
│   │   │   │       │   ├── transfer-dialog.component.css
│   │   │   │       │   └── transfer-dialog.component.spec.ts
│   │   │   │       └── transfer.module.ts
│   │   │   ├── app-routing.module.ts             # Routes
│   │   │   ├── app.component.ts                  # Root component
│   │   │   └── app.module.ts                     # Root module
│   │   ├── styles.css                            # Global styles + Tailwind
│   │   └── index.html
│   ├── angular.json                              # Angular CLI config
│   ├── package.json                              # npm dependencies
│   ├── tailwind.config.js                        # Tailwind configuration
│   └── tsconfig.json                             # TypeScript config
│
└── README.md                                     # Project overview
```

**Structure Decision**: Web application structure selected (Option 2 from template). Separate `backend/` and `frontend/` directories provide clear separation of concerns, independent build systems (Gradle vs npm), and align with full-stack web development best practices. This structure supports independent development and testing of each tier.

## Architecture Overview

### Three-Tier Architecture

```text
┌─────────────────────────────────────────────────────────────┐
│                        BROWSER                              │
│  ┌──────────────────────────────────────────────────────┐   │
│  │              Angular Application                      │   │
│  │  ┌────────────┐  ┌────────────┐  ┌──────────────┐    │   │
│  │  │   Login    │  │ Dashboard  │  │   Transfer   │    │   │
│  │  │ Component  │  │ Component  │  │   Dialog     │    │   │
│  │  └─────┬──────┘  └─────┬──────┘  └──────┬───────┘    │   │
│  │        │               │                │            │   │
│  │        └───────────────┴────────────────┘            │   │
│  │                        │                              │   │
│  │          ┌─────────────▼────────────────┐             │   │
│  │          │   Angular Services Layer     │             │   │
│  │          │ (AuthService, AccountService,│             │   │
│  │          │  TransactionService, etc.)   │             │   │
│  │          └─────────────┬────────────────┘             │   │
│  └────────────────────────┼──────────────────────────────┘   │
└─────────────────────────┼─────────────────────────────────┘
                          │ HTTP/REST
                          │ (JSON)
┌─────────────────────────▼─────────────────────────────────┐
│                   Spring Boot Backend                      │
│  ┌────────────────────────────────────────────────────┐    │
│  │           REST Controllers Layer                   │    │
│  │  (AuthController, AccountController,               │    │
│  │   TransactionController, TransferController)       │    │
│  └─────────────┬──────────────────────────────────────┘    │
│                │                                            │
│  ┌─────────────▼──────────────────────────────────────┐    │
│  │            Service Layer                           │    │
│  │  (AuthService, AccountService,                     │    │
│  │   TransactionService, TransferService)             │    │
│  │  • Business logic                                  │    │
│  │  • Transaction coordination                        │    │
│  │  • Validation                                      │    │
│  └─────────────┬──────────────────────────────────────┘    │
│                │                                            │
│  ┌─────────────▼──────────────────────────────────────┐    │
│  │         Repository Layer                           │    │
│  │  (Spring Data JPA Repositories)                    │    │
│  │  • UserRepository                                  │    │
│  │  • AccountRepository                               │    │
│  │  • TransactionRepository                           │    │
│  └─────────────┬──────────────────────────────────────┘    │
└────────────────┼───────────────────────────────────────────┘
                 │ JDBC
                 │
┌────────────────▼───────────────────────────────────────────┐
│                   SQLite Database                          │
│  ┌────────────────────────────────────────────────────┐    │
│  │  Tables:                                           │    │
│  │  • users        (id, username)                     │    │
│  │  • accounts     (id, user_id, type, number, bal)   │    │
│  │  • transactions (id, account_id, amount, date...)  │    │
│  │  • transfers    (id, from_id, to_id, amount...)    │    │
│  └────────────────────────────────────────────────────┘    │
└────────────────────────────────────────────────────────────┘
```

### Layer Responsibilities

**Frontend (Angular)**
- **Components**: UI rendering, user interaction, local state management
- **Services**: HTTP communication, shared state, business logic delegation
- **Guards**: Route protection, authentication checks
- **Interceptors**: Request/response transformation, session token injection

**Backend (Spring Boot)**
- **Controllers**: HTTP request/response handling, DTO mapping, input validation
- **Services**: Business logic, transaction management, sequential processing coordination
- **Repositories**: Data access abstraction, CRUD operations
- **Models/Entities**: Domain objects, JPA mappings

**Database (SQLite)**
- **Persistence**: Relational data storage
- **Constraints**: Foreign keys, uniqueness, not-null enforcement
- **Transactions**: ACID guarantees for transfer operations

## Data Flow

### Flow 1: User Login (Priority P1)

```text
1. User enters credentials in LoginComponent
   ↓
2. LoginComponent calls AuthService.login(username, password)
   ↓
3. AuthService sends POST /api/auth/login {username, password}
   ↓
4. AuthController receives request, delegates to AuthService
   ↓
5. AuthService validates non-empty fields, creates session token
   ↓
6. Return {token, userId} to frontend
   ↓
7. AuthService stores token in memory, marks user as authenticated
   ↓
8. Router navigates to /dashboard
```

**Data Format**:
- Request: `{username: string, password: string}`
- Response: `{token: string, userId: number}`

### Flow 2: View Dashboard (Priority P2)

```text
1. DashboardComponent loads (guarded by AuthGuard)
   ↓
2. Parallel calls:
   a) AccountService.getAccounts()
      → GET /api/accounts
      → AccountService.findByUserId(userId)
      → AccountRepository.findByUserId(userId)
      → SQLite query: SELECT * FROM accounts WHERE user_id = ?
      → Returns List<Account>
      → Mapped to AccountDto[]
      
   b) TransactionService.getRecentTransactions()
      → GET /api/transactions/recent?limit=10
      → TransactionService.findRecent(userId, limit)
      → TransactionRepository.findTop10ByUserIdOrderByDateDesc(userId)
      → SQLite query: SELECT * FROM transactions WHERE user_id = ? ORDER BY date DESC LIMIT 10
      → Returns List<Transaction>
      → Mapped to TransactionDto[]
   ↓
3. DashboardComponent distributes data:
   - AccountsListComponent receives accounts
   - TransactionsListComponent receives transactions
   ↓
4. Components render with Tailwind-styled templates
```

**Data Format**:
- Accounts: `[{id, type, accountNumber, balance}]`
- Transactions: `[{id, date, description, amount, accountId}]`

### Flow 3: Money Transfer (Priority P3)

```text
1. User clicks "Transfer" in DashboardComponent
   ↓
2. TransferDialogComponent opens with user's accounts
   ↓
3. User selects source, destination, enters amount and optional note
   ↓
4. TransferDialogComponent validates locally:
   - Source ≠ Destination
   - Amount > 0
   - Amount ≤ source.balance
   ↓
5. Call TransferService.createTransfer(transferRequest)
   ↓
6. TransferService sends POST /api/transfers
   {sourceAccountId, destinationAccountId, amount, note}
   ↓
7. TransferController receives, delegates to TransferService
   ↓
8. TransferService.executeTransfer() [SYNCHRONIZED - sequential processing]:
   a) Load source and destination accounts
   b) Validate:
      - Accounts exist and belong to user
      - Source has sufficient balance
      - Source ≠ Destination
      - Amount > 0
   c) Begin transaction:
      - Debit source account: balance -= amount
      - Credit destination account: balance += amount
      - Create Transaction record for source (negative amount)
      - Create Transaction record for destination (positive amount)
      - Create Transfer record linking both
      - accountRepository.save(source)
      - accountRepository.save(destination)
      - transactionRepository.save(sourceTx)
      - transactionRepository.save(destTx)
      - transferRepository.save(transfer)
   d) Commit transaction
   ↓
9. Return success response with updated balances
   ↓
10. TransferDialogComponent closes
    ↓
11. DashboardComponent refreshes accounts and transactions
```

**Data Format**:
- Request: `{sourceAccountId: number, destinationAccountId: number, amount: number, note?: string}`
- Response: `{success: boolean, updatedAccounts: AccountDto[]}`

### Session Management Flow

```text
Frontend (Angular):
- AuthService maintains in-memory authentication state
- AuthInterceptor adds session token to all HTTP requests
- AuthGuard checks authentication before route activation
- On 401 response → clear session, redirect to /login

Backend (Spring):
- Simple token validation (token exists in session store)
- Session timeout → return 401
- No session during transfer → return 401, discard operation

Frontend handles 401:
- AuthInterceptor catches 401
- Calls AuthService.logout()
- Redirects to /login
- Shows "Session expired" message
```

## Module Breakdown

### Backend Modules

#### 1. Auth Module
**Purpose**: User authentication and session management  
**Components**:
- `AuthService`: Validates credentials (non-empty check), manages sessions
- `AuthController`: POST /api/auth/login, POST /api/auth/logout
- `SecurityConfig`: Session configuration, CORS for Angular

**Key Methods**:
- `AuthService.login(username, password) → LoginResponse`
- `AuthService.validateSession(token) → boolean`
- `AuthService.logout(token) → void`

#### 2. Accounts Module
**Purpose**: Account data retrieval  
**Components**:
- `Account` entity: JPA entity mapping to accounts table
- `AccountRepository`: Spring Data JPA repository
- `AccountService`: Business logic for account queries
- `AccountController`: GET /api/accounts

**Key Methods**:
- `AccountService.getAccountsByUser(userId) → List<AccountDto>`
- `AccountRepository.findByUserId(userId) → List<Account>`

#### 3. Transactions Module
**Purpose**: Transaction history retrieval  
**Components**:
- `Transaction` entity: JPA entity mapping to transactions table
- `TransactionRepository`: Spring Data JPA repository
- `TransactionService`: Transaction query logic
- `TransactionController`: GET /api/transactions/recent

**Key Methods**:
- `TransactionService.getRecentTransactions(userId, limit) → List<TransactionDto>`
- `TransactionRepository.findTop10ByUserIdOrderByDateDesc(userId) → List<Transaction>`

#### 4. Transfers Module
**Purpose**: Money transfer operations  
**Components**:
- `Transfer` entity: JPA entity linking source/destination transactions
- `TransferService`: Transfer execution with validation and sequential processing
- `TransferController`: POST /api/transfers

**Key Methods**:
- `TransferService.executeTransfer(TransferRequest) → TransferResponse`
- `TransferService.validateTransfer(request) → void` (throws on failure)

**Critical**: 
- `executeTransfer` method is `synchronized` to ensure sequential processing per constitution requirement
- Method is annotated with `@Transactional` to ensure atomic database updates; if session expires mid-transfer or any error occurs, Spring automatically rolls back all database changes (no partial transfers possible)
- Transaction boundary: begins at method entry, commits on successful completion, rolls back on any exception

### Frontend Modules

#### 1. Auth Module (features/auth/)
**Purpose**: User login functionality  
**Components**:
- `LoginComponent`: Login form with username/password fields
- Template: Form with validation, error display, submit button

**Key Features**:
- Two-way binding for username/password
- Client-side validation (non-empty)
- Error message display for failed login
- Redirect to dashboard on success

#### 2. Dashboard Module (features/dashboard/)
**Purpose**: Main application view after login  
**Components**:
- `DashboardComponent`: Container for accounts and transactions
- `AccountsListComponent`: Displays account cards with balances
- `TransactionsListComponent`: Table of recent transactions

**Key Features**:
- Grid layout (Tailwind CSS)
- Real-time data from API
- "Transfer" button to open transfer dialog via `openTransferDialog()` method
- Auto-refresh after transfers (reload accounts and transactions on successful transfer)

**Key Methods**:
- `loadDashboardData()`: Fetch accounts and transactions using forkJoin
- `openTransferDialog()`: Show TransferDialogComponent modal
- `onTransferComplete()`: Refresh dashboard data after successful transfer

#### 3. Transfer Module (features/transfer/)
**Purpose**: Money transfer interface  
**Components**:
- `TransferDialogComponent`: Modal dialog for transfers

**Key Features**:
- Source account dropdown (filtered to exclude selected destination)
- Destination account dropdown (filtered to exclude selected source)
- Amount input with validation
- Optional note textarea
- Real-time validation feedback
- Confirmation before submission

### Core Services (frontend/core/services/)

#### AuthService
- `login(username, password): Observable<LoginResponse>`
- `logout(): void`
- `isAuthenticated(): boolean`
- `getToken(): string`

#### AccountService
- `getAccounts(): Observable<Account[]>`
- Caches account data, refreshes on demand

#### TransactionService
- `getRecentTransactions(limit): Observable<Transaction[]>`

#### TransferService
- `createTransfer(request): Observable<TransferResponse>`

## Complexity Tracking

**No violations detected** - all choices align with constitution principles:

- SQLite: Simplest embedded database, zero configuration
- Spring Boot: Industry-standard framework with excellent testing support
- Angular: Component-based architecture maximizes testability
- REST API: Simple, stateless, well-understood
- No additional design patterns beyond standard MVC
- No caching, no optimization, pure MVP scope

## Document References Quick Guide

When implementing each component, reference these documents:

| Component Type | Primary Reference | Secondary References |
|----------------|-------------------|---------------------|
| **JPA Entities** | [data-model.md](./data-model.md) → JPA Entity Mappings | [data-model.md](./data-model.md) → Entity Definitions |
| **Database Schema** | [data-model.md](./data-model.md) → Database Schema (SQLite DDL) | [data-model.md](./data-model.md) → Sample Data |
| **REST Endpoints** | [contracts/api.yaml](./contracts/api.yaml) → paths section | [Module Breakdown](#module-breakdown) → specific module |
| **DTOs** | [contracts/api.yaml](./contracts/api.yaml) → components/schemas | [data-model.md](./data-model.md) → TypeScript Interfaces |
| **Service Logic** | [Data Flow](#data-flow) → specific flow | [Module Breakdown](#module-breakdown) → Key Methods |
| **Validation Rules** | [spec.md](./spec.md) → Functional Requirements | [data-model.md](./data-model.md) → Data Validation Summary |
| **Frontend Components** | [Frontend Modules](#frontend-modules) → specific module | [Architecture Overview](#architecture-overview) |
| **TypeScript Models** | [data-model.md](./data-model.md) → TypeScript Interfaces | [contracts/api.yaml](./contracts/api.yaml) → schemas |
| **Configuration** | [research.md](./research.md) → Best Practices | [quickstart.md](./quickstart.md) |
| **Testing Patterns** | [research.md](./research.md) → Testing Strategies | Constitution → Principle II (TDD) |

## Critical Success Factors

Before marking implementation complete, verify:

1. **All Functional Requirements Met** (FR-001 through FR-019)
   - Cross-reference each FR in [spec.md](./spec.md) with implemented feature
   - Mark as ✅ in checklist

2. **All Acceptance Scenarios Pass**
   - Test each scenario from User Stories (P1, P2, P3)
   - Document test results

3. **All Edge Cases Addressed**
   - Verify behavior for each edge case in [spec.md](./spec.md)
   - At minimum, handle gracefully with error messages

4. **All Success Criteria Met** (SC-001 through SC-008)
   - Measure performance (dashboard load <2s, transfer <1s)
   - Verify transfer validation prevents 100% of invalid transfers

5. **TDD Compliance**
   - Every component has tests written first
   - All tests pass (backend: `./gradlew test`, frontend: `ng test`)
   - Code coverage >80%

6. **Constitution Compliance**
   - No features outside spec
   - No security hardening beyond MVP
   - No performance optimization
   - Simplest possible implementation
