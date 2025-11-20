# Implementation Plan: Banking App MVP

**Branch**: `002-banking-app` | **Date**: 2025-11-19 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/002-banking-app/spec.md`

## Summary

Banking MVP with three-tier architecture: Angular frontend with Tailwind CSS, Spring Boot REST API backend, and SQLite database. Core features include hardcoded authentication, account overview dashboard, and money transfers between user's own accounts. TDD approach ensures all functionality is test-driven with red → green → refactor cycle.

## Technical Context

**Language/Version**: Java 21 (backend), TypeScript/Angular (frontend)  
**Primary Dependencies**: Spring Boot 3.x, Angular 18+, Tailwind CSS, SQLite JDBC  
**Storage**: SQLite database with JPA/Hibernate entities  
**Testing**: JUnit 5 + Mockito (backend), Jasmine + Karma (frontend)  
**Target Platform**: Web application (desktop browsers)
**Project Type**: Web app - backend/src/, frontend/src/  
**Performance Goals**: Per success criteria in spec.md (SC-002, SC-003)  
**Constraints**: MVP scope - no security, no performance optimization  
**Scale/Scope**: Single test user, 2-3 accounts, session-based persistence

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

**Initial Check (Pre-Phase 0)**: ✅ PASSED
- [x] **SDD Compliance**: Complete specification exists defining all behavior
- [x] **TDD Ready**: JUnit 5/Mockito (backend), Jasmine/Karma (frontend) identified
- [x] **MVP Scope**: No security/performance optimization in requirements
- [x] **Simplicity**: Spring Boot + Angular standard stack maximizes simplicity
- [x] **AI Compliance**: All outputs follow constitution principles

**Post-Phase 1 Design Re-check**: ✅ PASSED
- [x] **SDD Compliance**: Data model and contracts precisely match specification entities and requirements
- [x] **TDD Ready**: Complete test strategy defined for all layers (unit, integration, contract)
- [x] **MVP Scope**: Architecture maintains hardcoded auth, session-based storage, no premature optimization
- [x] **Simplicity**: Standard Spring Boot/Angular patterns, SQLite for simplicity, modular structure matches spec
- [x] **AI Compliance**: All design artifacts follow constitution principles and specification requirements

## Project Structure

### Documentation (this feature)

```text
specs/002-banking-app/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
backend/
├── src/main/java/com/banking/
│   ├── auth/            # Authentication module
│   │   ├── AuthController.java
│   │   ├── AuthService.java
│   │   └── LoginRequest.java
│   ├── accounts/        # Account management module
│   │   ├── AccountController.java
│   │   ├── AccountService.java
│   │   ├── Account.java
│   │   └── AccountRepository.java
│   ├── transactions/    # Transaction tracking module
│   │   ├── TransactionController.java
│   │   ├── TransactionService.java
│   │   ├── Transaction.java
│   │   └── TransactionRepository.java
│   ├── transfers/       # Money transfer module
│   │   ├── TransferController.java
│   │   ├── TransferService.java
│   │   └── TransferRequest.java
│   ├── config/          # Configuration
│   │   ├── DatabaseConfig.java
│   │   └── SecurityConfig.java
│   └── BankingApplication.java
├── src/main/resources/
│   ├── application.yml
│   └── data.sql         # Initial test data
├── src/test/java/       # Backend tests (JUnit 5 + Mockito)
│   ├── auth/
│   ├── accounts/
│   ├── transactions/
│   └── transfers/
├── build.gradle
└── gradle/

frontend/
├── src/
│   ├── app/
│   │   ├── auth/        # Authentication module
│   │   │   ├── login/
│   │   │   │   ├── login.component.ts
│   │   │   │   ├── login.component.html
│   │   │   │   └── login.component.scss
│   │   │   ├── auth.service.ts
│   │   │   └── auth.guard.ts
│   │   ├── dashboard/   # Main dashboard module
│   │   │   ├── dashboard.component.ts
│   │   │   ├── dashboard.component.html
│   │   │   └── dashboard.component.scss
│   │   ├── accounts/    # Account display components
│   │   │   ├── account-list/
│   │   │   ├── account.service.ts
│   │   │   └── account.model.ts
│   │   ├── transactions/ # Transaction display components
│   │   │   ├── transaction-list/
│   │   │   ├── transaction.service.ts
│   │   │   └── transaction.model.ts
│   │   ├── transfers/   # Transfer dialog module
│   │   │   ├── transfer-dialog/
│   │   │   ├── transfer.service.ts
│   │   │   └── transfer.model.ts
│   │   ├── shared/      # Shared components/services
│   │   │   ├── api.service.ts
│   │   │   └── error-handler.service.ts
│   │   ├── app-routing.module.ts
│   │   ├── app.component.ts
│   │   └── app.module.ts
│   ├── assets/
│   ├── environments/
│   └── styles.scss
├── src/test/            # Frontend tests (Jasmine + Karma)
│   ├── auth/
│   ├── accounts/
│   ├── transactions/
│   └── transfers/
├── package.json
├── angular.json
├── tailwind.config.js
└── tsconfig.json
```

**Structure Decision**: Selected web application structure with separate backend and frontend projects. Backend uses standard Spring Boot Maven structure with feature-based modules (auth, accounts, transactions, transfers). Frontend uses Angular CLI structure with feature modules matching backend organization.

## Architecture Overview

### System Architecture

```text
┌─────────────────┐    HTTP/REST    ┌─────────────────┐    JPA/JDBC    ┌─────────────┐
│   Angular       │ ◄─────────────► │  Spring Boot    │ ◄────────────► │   SQLite    │
│   Frontend      │                 │   Backend       │                │  Database   │
│                 │                 │                 │                │             │
│ • Login Page    │                 │ • Auth API      │                │ • Users     │
│ • Dashboard     │                 │ • Accounts API  │                │ • Accounts  │
│ • Transfer      │                 │ • Transfers API │                │ • Trans.    │
│   Dialog        │                 │ • Business      │                │             │
│                 │                 │   Logic         │                │             │
└─────────────────┘                 └─────────────────┘                └─────────────┘
```

### Data Flow

1. **Authentication Flow**:
   - User enters credentials in Angular login component
   - Frontend sends POST /api/auth/login to Spring Boot
   - Backend validates against hardcoded credentials
   - Returns session token/cookie for subsequent requests
   - Frontend stores auth state and redirects to dashboard

2. **Dashboard Flow**:
   - Frontend requests GET /api/accounts (user's accounts)
   - Frontend requests GET /api/transactions?limit=5 (recent transactions)
   - Backend queries SQLite database via JPA repositories
   - Returns JSON data to populate Angular dashboard components

3. **Transfer Flow**:
   - User selects accounts and enters amount in Angular dialog
   - Frontend validates client-side (amount > 0, different accounts)
   - Posts transfer request to POST /api/transfers
   - Backend validates business rules (sufficient balance, account ownership)
   - Creates transaction records and updates account balances in database
   - Returns updated account balances to frontend
   - Frontend updates dashboard display

### Module Dependencies

**Backend Modules**:
- `auth` → provides authentication for all other modules
- `accounts` → used by `transactions` and `transfers` for account validation
- `transactions` → used by `transfers` to record transaction history
- `transfers` → orchestrates account updates and transaction recording

**Frontend Modules**:
- `auth` → protects all other routes via guards
- `shared` → provides common API services used by all feature modules
- `accounts`, `transactions`, `transfers` → independent feature modules consuming shared services

## Implementation Reference Guide

### User Story 1 - Authentication Implementation
- **Entity Specification**: See [data-model.md](./data-model.md) lines 25-45 (User entity attributes and constraints)
- **JPA Entity Mapping**: See [research.md](./research.md) lines 89-110 (User.java JPA annotations)
- **Controller Pattern**: See [research.md](./research.md) lines 111-130 (AuthController REST endpoint)
- **Service Pattern**: See [research.md](./research.md) lines 140-170 (AuthService business logic)
- **Test Examples**: See [research.md](./research.md) lines 426-445 (Service unit tests)
- **API Contract**: See [contracts/api-spec.yaml](./contracts/api-spec.yaml) lines 18-60 (/auth/login endpoint)
- **Frontend Component**: See [research.md](./research.md) lines 235-260 (Login component pattern)
- **Frontend Service**: See [research.md](./research.md) lines 300-320 (Auth service pattern)

### User Story 2 - Account Overview Implementation
- **Entity Specifications**: See [data-model.md](./data-model.md) lines 50-85 (Account and Transaction entities)
- **JPA Entity Mappings**: See [data-model.md](./data-model.md) lines 280-295 (Entity annotations with @JoinColumn for foreign keys)
- **Repository Patterns**: See [research.md](./research.md) lines 180-200 (JPA repository queries)
- **Controller Examples**: See [research.md](./research.md) lines 111-130 (REST controller pattern)
- **API Contracts**: See [contracts/api-spec.yaml](./contracts/api-spec.yaml) lines 80-120 (/accounts and /transactions)
- **Frontend Dashboard**: See [research.md](./research.md) lines 278-300 (Dashboard component pattern)
- **Data Display**: See [research.md](./research.md) lines 320-340 (List component patterns)

### User Story 3 - Money Transfer Implementation
- **Business Logic**: See [data-model.md](./data-model.md) lines 90-120 (Transfer validation rules)
- **Transaction Management**: See [research.md](./research.md) lines 140-170 (Service @Transactional)
- **Validation Examples**: See [research.md](./research.md) lines 180-220 (BigDecimal validation)
- **API Contract**: See [contracts/api-spec.yaml](./contracts/api-spec.yaml) lines 140-200 (/transfers endpoint)
- **Error Handling**: See [contracts/api-spec.yaml](./contracts/api-spec.yaml) lines 180-200 (Error responses)
- **Frontend Dialog**: See [research.md](./research.md) lines 350-380 (Modal dialog pattern)
- **Form Validation**: See [research.md](./research.md) lines 380-400 (Reactive forms)

## TDD Implementation Sequence

### User Story 1 - Authentication (Red → Green → Refactor)

**Phase 1.1: Backend Authentication**
1. **Red Phase**:
   - Write `UserRepositoryTest.shouldFindUserByUsername()` (Reference: [research.md](./research.md) lines 462-470)
   - Write `AuthServiceTest.shouldAuthenticateValidUser()` (Reference: [research.md](./research.md) lines 426-445)
   - Write `AuthControllerTest.shouldReturnTokenOnValidLogin()` (Reference: [research.md](./research.md) lines 498-510)

2. **Green Phase**:
   - Create User entity (Reference: [data-model.md](./data-model.md) User mapping + [research.md](./research.md) lines 89-110)
   - Create UserRepository interface (Reference: [research.md](./research.md) lines 180-200)
   - Implement AuthService.authenticate() (Minimal code to pass service test)
   - Implement AuthController.login() (Minimal code to pass controller test)

3. **Refactor Phase**:
   - Extract token generation logic
   - Add proper error handling
   - Optimize for readability while keeping tests green

**Phase 1.2: Frontend Authentication**
1. **Red Phase**:
   - Write `AuthService.shouldLoginWithValidCredentials()` (Reference: [research.md](./research.md) lines 972-985)
   - Write `LoginComponent.shouldCallAuthServiceOnSubmit()` (Reference: [research.md](./research.md) lines 540-560)
   - Write `AuthGuard.shouldAllowAccessWhenAuthenticated()` (Reference: [research.md](./research.md) lines 520-540)

2. **Green Phase**:
   - Create auth.service.ts (Reference: [research.md](./research.md) lines 300-320)
   - Create login.component.ts (Reference: [research.md](./research.md) lines 235-260)
   - Create auth.guard.ts (Minimal guard implementation)

3. **Refactor Phase**:
   - Extract common HTTP error handling
   - Improve form validation UX
   - Optimize component structure

### User Story 2 - Account Overview (Red → Green → Refactor)

**Phase 2.1: Backend Account Management**
1. **Red Phase**:
   - Write `AccountRepositoryTest.shouldFindAccountsByUserId()` (Reference: [research.md](./research.md) lines 462-470)
   - Write `AccountServiceTest.shouldReturnUserAccounts()` (Reference: [research.md](./research.md) lines 426-445)
   - Write `TransactionServiceTest.shouldReturnRecentTransactions()` (New test based on service pattern)

2. **Green Phase**:
   - Create Account and Transaction entities (Reference: [data-model.md](./data-model.md) entity mappings)
   - Create AccountRepository and TransactionRepository (Reference: [research.md](./research.md) lines 180-200)
   - Implement AccountService and TransactionService (Minimal implementations)
   - Create AccountController and TransactionController (Reference: [research.md](./research.md) lines 111-130)

3. **Refactor Phase**:
   - Add DTO mapping for cleaner API responses
   - Extract common repository patterns
   - Optimize query performance

**Phase 2.2: Frontend Dashboard**
1. **Red Phase**:
   - Write `DashboardComponent.shouldLoadAccountsOnInit()` (Reference: [research.md](./research.md) component test pattern)
   - Write `AccountService.shouldFetchUserAccounts()` (Reference: [research.md](./research.md) lines 972-985)
   - Write `TransactionService.shouldFetchRecentTransactions()` (Similar to account service test)

2. **Green Phase**:
   - Create dashboard.component.ts (Reference: [research.md](./research.md) lines 278-300)
   - Create account.service.ts and transaction.service.ts (Reference: [research.md](./research.md) lines 300-320)
   - Create account-list and transaction-list components (Reference: [research.md](./research.md) lines 320-340)

3. **Refactor Phase**:
   - Extract reusable list components
   - Add loading states and error handling
   - Optimize data binding patterns

### User Story 3 - Money Transfer (Red → Green → Refactor)

**Phase 3.1: Backend Transfer Logic**
1. **Red Phase**:
   - Write `TransferServiceTest.shouldTransferMoneyBetweenAccounts()` (Reference: [research.md](./research.md) lines 426-445)
   - Write `TransferServiceTest.shouldRejectInsufficientFunds()` (New validation test)
   - Write `TransferControllerTest.shouldProcessValidTransfer()` (Reference: [research.md](./research.md) lines 498-510)

2. **Green Phase**:
   - Create TransferRequest DTO (Reference: [contracts/api-spec.yaml](./contracts/api-spec.yaml) TransferRequest schema)
   - Implement TransferService with @Transactional (Reference: [research.md](./research.md) lines 140-170)
   - Create TransferController (Reference: [research.md](./research.md) lines 111-130)

3. **Refactor Phase**:
   - Extract validation logic into separate validator
   - Add comprehensive error handling
   - Optimize transaction management

**Phase 3.2: Frontend Transfer Dialog**
1. **Red Phase**:
   - Write `TransferDialogComponent.shouldValidateTransferForm()` (Reference: [research.md](./research.md) component test pattern)
   - Write `TransferService.shouldSubmitTransferRequest()` (Reference: [research.md](./research.md) lines 972-985)
   - Write integration test for complete transfer workflow

2. **Green Phase**:
   - Create transfer-dialog.component.ts (Reference: [research.md](./research.md) lines 350-380)
   - Create transfer.service.ts (Reference: [research.md](./research.md) lines 300-320)
   - Add reactive form validation (Reference: [research.md](./research.md) lines 380-400)

3. **Refactor Phase**:
   - Extract form validation into reusable validators
   - Add user feedback for transfer status
   - Optimize dialog UX flow

## Task Dependencies Matrix

### Project Setup Dependencies
| Task | Depends On | Produces | Test Criteria | Reference |
|------|------------|----------|---------------|-----------|
| Create backend project | Java 21, Gradle | Spring Boot structure | `./gradlew test` passes | [quickstart.md](./quickstart.md) lines 60-90 |
| Create frontend project | Node.js, Angular CLI | Angular structure | `ng test` passes | [quickstart.md](./quickstart.md) lines 100-130 |
| Configure database | Backend project | SQLite config | Application starts | [quickstart.md](./quickstart.md) lines 70-85 |

### User Story 1 - Authentication Dependencies
| Task | Depends On | Produces | Test Criteria | Reference |
|------|------------|----------|---------------|-----------|
| Create User entity | Database config | User.java | JPA validation test passes | [data-model.md](./data-model.md) lines 25-45 |
| Create UserRepository | User entity | UserRepository.java | findByUsername test passes | [research.md](./research.md) lines 180-200 |
| Create AuthService | UserRepository | AuthService.java | authenticate() test passes | [research.md](./research.md) lines 426-445 |
| Create AuthController | AuthService | AuthController.java | login endpoint test passes | [research.md](./research.md) lines 498-510 |
| Create auth.service.ts | Backend API running | auth.service.ts | HTTP login test passes | [research.md](./research.md) lines 972-985 |
| Create login component | auth.service.ts | login.component.ts | Form submission test passes | [research.md](./research.md) lines 540-560 |
| Create auth guard | auth.service.ts | auth.guard.ts | Route protection test passes | [research.md](./research.md) lines 520-540 |

### User Story 2 - Account Overview Dependencies
| Task | Depends On | Produces | Test Criteria | Reference |
|------|------------|----------|---------------|-----------|
| Create Account entity | User entity | Account.java | JPA relationship test passes | [data-model.md](./data-model.md) lines 50-75 |
| Create Transaction entity | Account entity | Transaction.java | JPA mapping test passes | [data-model.md](./data-model.md) lines 80-110 |
| Create AccountRepository | Account entity | AccountRepository.java | findByUserId test passes | [research.md](./research.md) lines 180-200 |
| Create TransactionRepository | Transaction entity | TransactionRepository.java | findRecent test passes | [research.md](./research.md) lines 180-200 |
| Create AccountService | AccountRepository | AccountService.java | getAccountsByUser test passes | [research.md](./research.md) lines 426-445 |
| Create TransactionService | TransactionRepository | TransactionService.java | getRecentTransactions test passes | [research.md](./research.md) lines 426-445 |
| Create AccountController | AccountService | AccountController.java | GET /accounts test passes | [research.md](./research.md) lines 498-510 |
| Create TransactionController | TransactionService | TransactionController.java | GET /transactions test passes | [research.md](./research.md) lines 498-510 |
| Add initial test data | Entities created | data.sql | Accounts visible in API | [data-model.md](./data-model.md) lines 200-230 |
| Create dashboard component | Auth guard | dashboard.component.ts | Component loads data | [research.md](./research.md) lines 278-300 |
| Create account.service.ts | Backend APIs | account.service.ts | HTTP requests work | [research.md](./research.md) lines 972-985 |
| Create transaction.service.ts | Backend APIs | transaction.service.ts | HTTP requests work | [research.md](./research.md) lines 972-985 |
| Create account-list component | account.service.ts | account-list.component.ts | Displays account data | [research.md](./research.md) lines 320-340 |
| Create transaction-list component | transaction.service.ts | transaction-list.component.ts | Displays transaction data | [research.md](./research.md) lines 320-340 |

### User Story 3 - Money Transfer Dependencies
| Task | Depends On | Produces | Test Criteria | Reference |
|------|------------|----------|---------------|-----------|
| Create TransferRequest DTO | - | TransferRequest.java | Validation annotations work | [contracts/api-spec.yaml](./contracts/api-spec.yaml) TransferRequest |
| Create TransferService | Account/Transaction services | TransferService.java | Transfer logic test passes | [research.md](./research.md) lines 426-445 |
| Create TransferController | TransferService | TransferController.java | POST /transfers test passes | [research.md](./research.md) lines 498-510 |
| Create transfer.service.ts | Backend transfer API | transfer.service.ts | HTTP transfer request works | [research.md](./research.md) lines 972-985 |
| Create transfer-dialog component | transfer.service.ts | transfer-dialog.component.ts | Form validation works | [research.md](./research.md) lines 350-380 |
| Integrate transfer dialog | Dashboard component | Updated dashboard | Transfer button works | - |

### Cross-Cutting Dependencies
| Task | Depends On | Produces | Test Criteria | Reference |
|------|------------|----------|---------------|-----------|
| Configure CORS | Backend setup | SecurityConfig.java | Frontend can call backend | [research.md](./research.md) lines 60-80 |
| Add error handling | All controllers | GlobalExceptionHandler.java | Proper error responses | [contracts/api-spec.yaml](./contracts/api-spec.yaml) ErrorResponse |
| Style with Tailwind | Frontend setup | Styled components | UI looks professional | [quickstart.md](./quickstart.md) lines 110-125 |
| Add route guards | Auth service | Protected routes | Unauthenticated users redirected | [research.md](./research.md) lines 520-540 |

## TDD Implementation Strategy

### Backend Test Strategy
1. **Unit Tests**: Each service class with mocked dependencies
2. **Integration Tests**: Repository layer with embedded SQLite
3. **Contract Tests**: Controller layer with MockMvc
4. **Test Data**: @Sql scripts for consistent test state

### Frontend Test Strategy  
1. **Unit Tests**: Component logic with mocked services
2. **Integration Tests**: Service layer with HttpClientTestingModule
3. **E2E Tests**: Full user workflows with Protractor/Cypress

### TDD Cycle per Feature
1. **Red**: Write failing test defining expected behavior
2. **Green**: Write minimal code to pass the test
3. **Refactor**: Improve design while keeping tests green
4. **Repeat**: For each acceptance scenario in specification

## Complexity Tracking

*No constitution violations - all choices align with simplicity principles*

| Decision | Justification |
|----------|---------------|
| Spring Boot | Standard, well-documented framework minimizing configuration |
| Angular CLI | Conventional structure reduces decision fatigue |
| SQLite | Simplest database requiring no external setup |
| JPA/Hibernate | Standard ORM reducing SQL complexity |
| Modular structure | Matches specification entities for traceability |
