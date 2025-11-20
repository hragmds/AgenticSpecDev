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
**Performance Goals**: Dashboard load <2s, transfer completion <1min (per spec)  
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
