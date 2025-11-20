# Tasks: Banking App MVP

**Input**: Design documents from `/specs/002-banking-app/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: TDD approach with red → green → refactor cycle for all functionality

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

Web application structure:
- **Backend**: `backend/src/main/java/com/banking/`
- **Frontend**: `frontend/src/app/`
- **Tests**: `backend/src/test/java/` and `frontend/src/test/`

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure per plan.md

- [ ] T001 Create backend project structure with Spring Boot 3.x in backend/
- [ ] T002 Create frontend project structure with Angular 18+ in frontend/
- [ ] T003 [P] Configure Gradle build for backend in backend/build.gradle
- [ ] T004 [P] Setup Gradle wrapper in backend/gradle/wrapper/
- [ ] T005 [P] Configure Angular CLI and Tailwind CSS in frontend/package.json
- [ ] T006 [P] Setup SQLite configuration in backend/src/main/resources/application.yml

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T007 Setup database schema and JPA configuration in backend/src/main/java/com/banking/config/DatabaseConfig.java
- [ ] T008 Configure CORS and security settings in backend/src/main/java/com/banking/config/SecurityConfig.java
- [ ] T009 [P] Setup global error handling in backend/src/main/java/com/banking/config/GlobalExceptionHandler.java
- [ ] T010 [P] Configure shared API service in frontend/src/app/shared/api.service.ts
- [ ] T011 [P] Setup routing configuration in frontend/src/app/app-routing.module.ts
- [ ] T012 Create base application entry point in backend/src/main/java/com/banking/BankingApplication.java

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - User Authentication (Priority: P1) 🎯 MVP

**Goal**: Enable users to log in with hardcoded credentials (demo/password) and access protected dashboard

**Independent Test**: Visit login page, enter valid credentials, verify navigation to dashboard and route protection works

### Tests for User Story 1 (TDD Required) ✅

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

- [ ] T013 [P] [US1] Write UserRepository test in backend/src/test/java/com/banking/auth/UserRepositoryTest.java
- [ ] T014 [P] [US1] Write AuthService unit test in backend/src/test/java/com/banking/auth/AuthServiceTest.java
- [ ] T015 [P] [US1] Write AuthController integration test in backend/src/test/java/com/banking/auth/AuthControllerTest.java
- [ ] T016 [P] [US1] Write AuthService component test in frontend/src/test/auth/auth.service.spec.ts
- [ ] T017 [P] [US1] Write LoginComponent unit test in frontend/src/test/auth/login/login.component.spec.ts
- [ ] T018 [P] [US1] Write AuthGuard unit test in frontend/src/test/auth/auth.guard.spec.ts

### Implementation for User Story 1

- [ ] T019 [P] [US1] Create User entity in backend/src/main/java/com/banking/auth/User.java
- [ ] T020 [P] [US1] Create LoginRequest DTO in backend/src/main/java/com/banking/auth/LoginRequest.java
- [ ] T021 [US1] Create UserRepository interface in backend/src/main/java/com/banking/auth/UserRepository.java (depends on T019)
- [ ] T022 [US1] Implement AuthService in backend/src/main/java/com/banking/auth/AuthService.java (depends on T021)
- [ ] T023 [US1] Implement AuthController in backend/src/main/java/com/banking/auth/AuthController.java (depends on T022)
- [ ] T024 [P] [US1] Create auth.service.ts in frontend/src/app/auth/auth.service.ts
- [ ] T025 [P] [US1] Create login component in frontend/src/app/auth/login/login.component.ts
- [ ] T026 [P] [US1] Create login template in frontend/src/app/auth/login/login.component.html
- [ ] T027 [P] [US1] Create login styles in frontend/src/app/auth/login/login.component.scss
- [ ] T028 [US1] Create auth guard in frontend/src/app/auth/auth.guard.ts (depends on T024)
- [ ] T029 [US1] Add initial user data in backend/src/main/resources/data.sql (depends on T019)
- [ ] T030 [US1] Configure protected routes and auth integration in frontend/src/app/app-routing.module.ts

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently

---

## Phase 4: User Story 2 - Account Overview (Priority: P2)

**Goal**: Display user accounts, balances, and recent transactions on dashboard after authentication

**Independent Test**: Log in and verify accounts with balances and last 5 transactions are displayed correctly

### Tests for User Story 2 (TDD Required) ✅

- [ ] T031 [P] [US2] Write AccountRepository test in backend/src/test/java/com/banking/accounts/AccountRepositoryTest.java
- [ ] T032 [P] [US2] Write TransactionRepository test in backend/src/test/java/com/banking/transactions/TransactionRepositoryTest.java
- [ ] T033 [P] [US2] Write AccountService unit test in backend/src/test/java/com/banking/accounts/AccountServiceTest.java
- [ ] T034 [P] [US2] Write TransactionService unit test in backend/src/test/java/com/banking/transactions/TransactionServiceTest.java
- [ ] T035 [P] [US2] Write AccountController integration test in backend/src/test/java/com/banking/accounts/AccountControllerTest.java
- [ ] T036 [P] [US2] Write TransactionController integration test in backend/src/test/java/com/banking/transactions/TransactionControllerTest.java
- [ ] T037 [P] [US2] Write DashboardComponent test in frontend/src/test/dashboard/dashboard.component.spec.ts
- [ ] T038 [P] [US2] Write AccountService test in frontend/src/test/accounts/account.service.spec.ts
- [ ] T039 [P] [US2] Write TransactionService test in frontend/src/test/transactions/transaction.service.spec.ts

### Implementation for User Story 2

- [ ] T040 [P] [US2] Create Account entity in backend/src/main/java/com/banking/accounts/Account.java
- [ ] T041 [P] [US2] Create Transaction entity in backend/src/main/java/com/banking/transactions/Transaction.java
- [ ] T042 [US2] Create AccountRepository interface in backend/src/main/java/com/banking/accounts/AccountRepository.java (depends on T040)
- [ ] T043 [US2] Create TransactionRepository interface in backend/src/main/java/com/banking/transactions/TransactionRepository.java (depends on T041)
- [ ] T044 [US2] Implement AccountService in backend/src/main/java/com/banking/accounts/AccountService.java (depends on T042)
- [ ] T045 [US2] Implement TransactionService in backend/src/main/java/com/banking/transactions/TransactionService.java (depends on T043)
- [ ] T046 [US2] Implement AccountController in backend/src/main/java/com/banking/accounts/AccountController.java (depends on T044)
- [ ] T047 [US2] Implement TransactionController in backend/src/main/java/com/banking/transactions/TransactionController.java (depends on T045)
- [ ] T048 [US2] Add initial account and transaction test data in backend/src/main/resources/data.sql (depends on T040, T041)
- [ ] T049 [P] [US2] Create account.model.ts in frontend/src/app/accounts/account.model.ts
- [ ] T050 [P] [US2] Create transaction.model.ts in frontend/src/app/transactions/transaction.model.ts
- [ ] T051 [P] [US2] Create account.service.ts in frontend/src/app/accounts/account.service.ts
- [ ] T052 [P] [US2] Create transaction.service.ts in frontend/src/app/transactions/transaction.service.ts
- [ ] T053 [P] [US2] Create dashboard component in frontend/src/app/dashboard/dashboard.component.ts
- [ ] T054 [P] [US2] Create dashboard template in frontend/src/app/dashboard/dashboard.component.html
- [ ] T055 [P] [US2] Create dashboard styles in frontend/src/app/dashboard/dashboard.component.scss
- [ ] T056 [P] [US2] Create account-list component in frontend/src/app/accounts/account-list/account-list.component.ts
- [ ] T057 [P] [US2] Create account-list template in frontend/src/app/accounts/account-list/account-list.component.html
- [ ] T058 [P] [US2] Create transaction-list component in frontend/src/app/transactions/transaction-list/transaction-list.component.ts
- [ ] T059 [P] [US2] Create transaction-list template in frontend/src/app/transactions/transaction-list/transaction-list.component.html
- [ ] T060 [US2] Integrate dashboard with account and transaction services in frontend/src/app/dashboard/dashboard.component.ts

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently

---

## Phase 5: User Story 3 - Money Transfer (Priority: P3)

**Goal**: Enable money transfers between user's own accounts with validation and balance updates

**Independent Test**: Open transfer dialog, select accounts, enter amount and note, verify transaction completes and appears in history

### Tests for User Story 3 (TDD Required) ✅

- [ ] T061 [P] [US3] Write TransferService unit test in backend/src/test/java/com/banking/transfers/TransferServiceTest.java
- [ ] T062 [P] [US3] Write TransferController integration test in backend/src/test/java/com/banking/transfers/TransferControllerTest.java
- [ ] T063 [P] [US3] Write TransferDialogComponent test in frontend/src/test/transfers/transfer-dialog/transfer-dialog.component.spec.ts
- [ ] T064 [P] [US3] Write TransferService test in frontend/src/test/transfers/transfer.service.spec.ts

### Implementation for User Story 3

- [ ] T065 [P] [US3] Create TransferRequest DTO in backend/src/main/java/com/banking/transfers/TransferRequest.java
- [ ] T066 [US3] Implement TransferService with transaction management in backend/src/main/java/com/banking/transfers/TransferService.java (depends on T044, T045)
- [ ] T067 [US3] Implement TransferController in backend/src/main/java/com/banking/transfers/TransferController.java (depends on T066)
- [ ] T068 [P] [US3] Create transfer.model.ts in frontend/src/app/transfers/transfer.model.ts
- [ ] T069 [P] [US3] Create transfer.service.ts in frontend/src/app/transfers/transfer.service.ts
- [ ] T070 [P] [US3] Create transfer-dialog component in frontend/src/app/transfers/transfer-dialog/transfer-dialog.component.ts
- [ ] T071 [P] [US3] Create transfer-dialog template with reactive forms in frontend/src/app/transfers/transfer-dialog/transfer-dialog.component.html
- [ ] T072 [P] [US3] Create transfer-dialog styles in frontend/src/app/transfers/transfer-dialog/transfer-dialog.component.scss
- [ ] T073 [US3] Integrate transfer dialog with dashboard in frontend/src/app/dashboard/dashboard.component.ts
- [ ] T074 [US3] Add transfer button and modal trigger to dashboard template in frontend/src/app/dashboard/dashboard.component.html

**Checkpoint**: All user stories should now be independently functional

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] T075 [P] Add comprehensive error handling across all controllers in backend/src/main/java/com/banking/config/GlobalExceptionHandler.java
- [ ] T076 [P] Add loading states and error messages in frontend shared components
- [ ] T077 [P] Enhance UI styling with Tailwind CSS across all components
- [ ] T078 [P] Add form validation feedback in all frontend forms
- [ ] T079 [P] Add proper logging throughout backend services
- [ ] T080 [P] Run quickstart.md validation and setup verification
- [ ] T081 Code cleanup and refactoring for maintainability

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3+)**: All depend on Foundational phase completion
  - User stories can then proceed in parallel (if staffed)
  - Or sequentially in priority order (P1 → P2 → P3)
- **Polish (Final Phase)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - Independent but integrates with authentication
- **User Story 3 (P3)**: Can start after Foundational (Phase 2) - Requires Account and Transaction services from US2

### Within Each User Story

- Tests MUST be written and FAIL before implementation (TDD red → green → refactor)
- Entities before repositories
- Repositories before services  
- Services before controllers
- Backend APIs before frontend services
- Frontend services before frontend components
- Core implementation before integration

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel
- All Foundational tasks marked [P] can run in parallel (within Phase 2)
- Once Foundational phase completes, User Stories 1 & 2 can start in parallel
- User Story 3 should wait for Account/Transaction services from US2
- All tests for a user story marked [P] can run in parallel
- Models/DTOs within a story marked [P] can run in parallel
- Frontend and backend tasks for same story can run in parallel once APIs are defined

---

## Parallel Example: User Story 1

```bash
# Launch all tests for User Story 1 together:
Task: T013 "UserRepository test in backend/src/test/java/com/banking/auth/UserRepositoryTest.java"
Task: T014 "AuthService unit test in backend/src/test/java/com/banking/auth/AuthServiceTest.java"
Task: T015 "AuthController integration test in backend/src/test/java/com/banking/auth/AuthControllerTest.java"
Task: T016 "AuthService component test in frontend/src/test/auth/auth.service.spec.ts"
Task: T017 "LoginComponent unit test in frontend/src/test/auth/login/login.component.spec.ts"
Task: T018 "AuthGuard unit test in frontend/src/test/auth/auth.guard.spec.ts"

# Launch all entities/DTOs for User Story 1 together:
Task: T019 "User entity in backend/src/main/java/com/banking/auth/User.java"
Task: T020 "LoginRequest DTO in backend/src/main/java/com/banking/auth/LoginRequest.java"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup (T001-T006)
2. Complete Phase 2: Foundational (T007-T012) - CRITICAL blocker
3. Complete Phase 3: User Story 1 (T013-T030)
4. **STOP and VALIDATE**: Test authentication flow independently
5. Deploy/demo if ready - users can log in and see protected dashboard

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently → Deploy/Demo (MVP - Login!)
3. Add User Story 2 → Test independently → Deploy/Demo (MVP+ - View accounts!)
4. Add User Story 3 → Test independently → Deploy/Demo (Full MVP - Transfer money!)
5. Each story adds value without breaking previous stories

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup + Foundational together (T001-T012)
2. Once Foundational is done:
   - Developer A: User Story 1 (T013-T030)
   - Developer B: User Story 2 (T031-T060) 
   - Developer C: User Story 3 after US2 services ready (T061-T074)
3. Stories complete and integrate independently

---

## Summary

- **Total Tasks**: 81 tasks across 6 phases
- **MVP Scope**: User Story 1 (Authentication) - 18 tasks
- **Full MVP Scope**: All 3 user stories - 74 implementation tasks + 7 polish tasks
- **Parallel Opportunities**: 46 tasks marked [P] can run in parallel within their phases
- **TDD Coverage**: 19 test tasks ensuring all functionality is test-driven
- **Independent Stories**: Each user story can be completed and tested independently
- **Suggested MVP**: Complete through User Story 1 for immediate value, then incrementally add US2 and US3

### Task Count per User Story
- **User Story 1 (Authentication)**: 18 tasks (6 tests + 12 implementation)
- **User Story 2 (Account Overview)**: 30 tasks (9 tests + 21 implementation) 
- **User Story 3 (Money Transfer)**: 14 tasks (4 tests + 10 implementation)

### Implementation Bridge
All tasks reference specific patterns and examples from:
- **Entity patterns**: [data-model.md](./data-model.md) JPA mappings
- **Service patterns**: [research.md](./research.md) lines 426-445, 140-170
- **Controller patterns**: [research.md](./research.md) lines 111-130, 498-510
- **Frontend patterns**: [research.md](./research.md) lines 235-260, 278-300, 320-340
- **API contracts**: [contracts/api-spec.yaml](./contracts/api-spec.yaml) endpoint definitions
- **Test patterns**: [research.md](./research.md) lines 462-470, 540-560, 972-985