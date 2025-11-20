# Research: Personal Banking Application

**Phase**: 0 - Outline & Research  
**Date**: November 19, 2025  
**Status**: Complete

## Technology Decisions

### Backend Framework: Spring Boot

**Decision**: Use Spring Boot 3.x with Java 17+

**Rationale**:
- Industry-standard framework with extensive documentation
- Built-in dependency injection simplifies testing
- Spring Data JPA abstracts database operations
- Excellent testing support (MockMvc, @SpringBootTest)
- Convention over configuration reduces boilerplate
- Auto-configuration for web, JPA, and testing

**Alternatives Considered**:
- **Plain Java Servlets**: Rejected - too much boilerplate, manual dependency management
- **Micronaut**: Rejected - less mature, smaller community, overkill for MVP
- **Quarkus**: Rejected - newer framework, less documentation, not needed for SQLite use case

### Frontend Framework: Angular

**Decision**: Use Angular 17+ with TypeScript

**Rationale**:
- Component-based architecture aligns with feature modules (auth, dashboard, transfer)
- Dependency injection enables easy service mocking for tests
- RxJS observables for reactive HTTP communication
- Strong typing with TypeScript reduces runtime errors
- CLI tooling (ng generate) speeds up development
- Built-in routing with guards for authentication

**Alternatives Considered**:
- **React**: Rejected - requires additional routing library, less opinionated structure
- **Vue**: Rejected - smaller ecosystem, less enterprise adoption
- **Vanilla JS**: Rejected - too much manual DOM manipulation, no testing framework

### Database: SQLite

**Decision**: Use SQLite as embedded database

**Rationale**:
- Zero configuration - single file database
- Embedded - no separate server process
- Perfect for MVP/demo applications
- Full SQL support for transactions and constraints
- Excellent for testing (in-memory mode)
- Spring Boot JPA works seamlessly with SQLite

**Alternatives Considered**:
- **PostgreSQL**: Rejected - overkill for MVP, requires separate server, adds complexity
- **H2**: Considered - similar to SQLite but less portable, SQLite more widely known
- **In-memory collections**: Rejected - no persistence, no transaction guarantees

### Build Tools

**Backend - Gradle**:
- More flexible than Maven for multi-module projects
- Kotlin DSL provides better IDE support
- Faster incremental builds
- Spring Boot gradle plugin simplifies packaging

**Frontend - npm/Angular CLI**:
- Standard Angular tooling
- Extensive package ecosystem
- Integrated testing with Karma/Jasmine

### CSS Framework: Tailwind CSS

**Decision**: Use Tailwind CSS 3.x

**Rationale**:
- Utility-first approach speeds up development
- No custom CSS needed for MVP
- Responsive design built-in
- Easy to integrate with Angular
- Smaller bundle size than Bootstrap

**Alternatives Considered**:
- **Bootstrap**: Rejected - more opinionated, larger bundle, harder to customize
- **Custom CSS**: Rejected - too time-consuming for MVP

## Best Practices

### Backend Patterns

**Repository Pattern** (via Spring Data JPA):
- Abstraction over data access
- Automatic CRUD operations
- Query derivation from method names
- Easy to mock in tests

**Service Layer Pattern**:
- Business logic separation from controllers
- Transaction boundary definition
- Reusable across multiple controllers
- Unit testable in isolation

**DTO Pattern**:
- Separation of API contracts from domain models
- Prevents over/under-fetching
- API versioning flexibility

### Frontend Patterns

**Feature Modules**:
- Organized by feature (auth, dashboard, transfer)
- Lazy loading potential (not needed for MVP)
- Clear separation of concerns

**Smart/Dumb Components**:
- Smart: DashboardComponent (orchestrates data)
- Dumb: AccountsListComponent, TransactionsListComponent (pure display)
- Easier testing and reusability

**Observable Streams**:
- RxJS for async operations
- Composable data transformations
- Automatic subscription cleanup

### Testing Strategies

**Backend**:
- **Unit Tests**: Service layer with mocked repositories
- **Integration Tests**: Controller tests with MockMvc
- **Repository Tests**: @DataJpaTest for data layer
- **Test Data**: SQL scripts for consistent test fixtures

**Frontend**:
- **Component Tests**: Isolated with mocked services
- **Service Tests**: HTTP mocking with HttpClientTestingModule
- **Integration Tests**: Test harness for component interactions
- **E2E** (out of scope for MVP): Protractor/Cypress

### Sequential Transfer Processing

**Pattern**: Synchronized method with database transactions

**Implementation**:
```java
@Transactional
public synchronized TransferResponse executeTransfer(TransferRequest request) {
    // Load accounts
    // Validate
    // Update balances
    // Create transactions
    // Save all entities
    // Return response
}
```

**Rationale**:
- `@Transactional`: Ensures atomicity (all or nothing)
- `synchronized`: Enforces sequential processing (per spec clarification)
- Simple and testable
- Meets constitution requirement for simplicity

**Alternatives Considered**:
- **Optimistic Locking**: Rejected - adds complexity, not needed for MVP
- **Pessimistic Locking**: Rejected - requires SELECT FOR UPDATE, complicates testing
- **Queue-based**: Rejected - overkill for MVP, adds infrastructure

## Integration Patterns

### REST API Design

**Endpoint Structure**:
- `/api/auth/*` - Authentication operations
- `/api/accounts` - Account queries
- `/api/transactions/*` - Transaction queries
- `/api/transfers` - Transfer operations

**HTTP Methods**:
- POST /api/auth/login - Login (creates session)
- GET /api/accounts - List user's accounts
- GET /api/transactions/recent - Recent transactions
- POST /api/transfers - Create transfer

**Status Codes**:
- 200 OK - Success
- 401 Unauthorized - Session expired or invalid
- 400 Bad Request - Validation failure
- 500 Internal Server Error - Unexpected errors

### CORS Configuration

**Setup**: Allow Angular dev server (http://localhost:4200)

**Configuration**:
```java
@Configuration
public class SecurityConfig {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:4200");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        // ...
    }
}
```

### Session Management

**Frontend**:
- Store token in AuthService (in-memory)
- Add token to headers via HTTP interceptor
- Clear token on 401 response

**Backend**:
- Simple token validation (exists in session map)
- No expiration for MVP (can add timeout if needed)
- Session cleared on logout

## Performance Considerations (MVP Constraints)

**Out of Scope per Constitution**:
- Database indexing (SQLite defaults sufficient)
- Connection pooling (embedded SQLite, single file)
- Caching (adds complexity, not needed for demo)
- Query optimization (N+1 acceptable for MVP)
- Lazy loading (eager loading fine for small dataset)

**In Scope**:
- Transaction management (required for data integrity)
- Input validation (baseline security)
- Error handling (user experience)

## Development Environment Setup

**Backend Requirements**:
- Java 17+ JDK
- Gradle 8.x (wrapper included)
- IDE: IntelliJ IDEA or VS Code with Java extensions

**Frontend Requirements**:
- Node.js 18+ and npm
- Angular CLI 17+
- IDE: VS Code with Angular extensions

**Database**:
- SQLite JDBC driver (included via Gradle dependency)
- No installation required (embedded)

## Summary

All technology choices prioritize:
1. **Simplicity**: Minimal configuration, standard patterns
2. **Testability**: Built-in testing frameworks, easy mocking
3. **MVP Scope**: No unnecessary features or optimizations
4. **Constitution Compliance**: Boring, well-understood technologies

No unknowns or "NEEDS CLARIFICATION" items remain. Ready for Phase 1: Design & Contracts.
