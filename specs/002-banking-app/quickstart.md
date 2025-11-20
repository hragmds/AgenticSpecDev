# Quickstart Guide: Banking App MVP

**Feature**: Banking App MVP  
**Tech Stack**: Java 21 + Spring Boot + Angular + SQLite  
**Development Approach**: Test-Driven Development (TDD)

## Prerequisites

### Required Software
- **Java 21** - OpenJDK or Oracle JDK
- **Node.js 18+** - For Angular development
- **Angular CLI 17+** - `npm install -g @angular/cli`
- **Git** - Version control

### Verification Commands
```bash
# Verify installations
java --version          # Should show Java 21.x
node --version          # Should show v18.x or higher  
ng version             # Should show Angular CLI 17.x
git --version          # Any recent version
```

## Project Setup

### 1. Initialize Backend (Spring Boot)

```bash
# Navigate to project root
cd /path/to/banking-app

# Create backend directory
mkdir backend
cd backend

# Initialize Spring Boot project with Gradle
curl https://start.spring.io/starter.tgz \
  -d dependencies=web,data-jpa,validation,h2 \
  -d type=gradle-project \
  -d language=java \
  -d bootVersion=3.2.0 \
  -d groupId=com.banking \
  -d artifactId=banking-app \
  -d name=banking-app \
  -d description="Banking App MVP" \
  -d packageName=com.banking \
  -d packaging=jar \
  -d javaVersion=21 | tar -xzvf -

# Move files to backend directory
mv banking-app/* .
rm -rf banking-app
```

### 2. Configure Database (SQLite)

Edit `backend/build.gradle`:
```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.xerial:sqlite-jdbc:3.43.0.0'
    implementation 'org.hibernate.orm:hibernate-community-dialects:6.3.1.Final'
    
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.testcontainers:junit-jupiter'
}
```

Configure `backend/src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:sqlite:banking.db
    driver-class-name: org.sqlite.JDBC
  jpa:
    database-platform: org.hibernate.community.dialect.SQLiteDialect
    hibernate:
      ddl-auto: create-drop
    sql:
      init:
        mode: always
        data-locations: classpath:data.sql
  jackson:
    serialization:
      write-dates-as-timestamps: false

server:
  port: 8080
  servlet:
    context-path: /api

logging:
  level:
    com.banking: DEBUG
    org.springframework.web: DEBUG
```

### 3. Initialize Frontend (Angular)

```bash
# Return to project root
cd ..

# Create Angular project
ng new frontend --routing --style=scss --package-manager=npm
cd frontend

# Install Tailwind CSS
npm install -D tailwindcss postcss autoprefixer
npx tailwindcss init

# Configure Tailwind (tailwind.config.js)
echo 'module.exports = {
  content: [
    "./src/**/*.{html,ts}",
  ],
  theme: {
    extend: {},
  },
  plugins: [],
}' > tailwind.config.js

# Add Tailwind to styles.scss
echo '@tailwind base;
@tailwind components;
@tailwind utilities;' > src/styles.scss
```

### 4. Project Structure Verification

After setup, verify this structure exists:
```text
banking-app/
├── backend/
│   ├── src/main/java/com/banking/
│   ├── src/main/resources/
│   ├── src/test/java/
│   ├── build.gradle
│   └── gradlew
├── frontend/
│   ├── src/app/
│   ├── src/assets/
│   ├── package.json
│   └── angular.json
└── README.md
```

## TDD Development Workflow

### Backend TDD Cycle

1. **Red Phase** - Write failing test:
```bash
cd backend
./gradlew test  # Should fail initially
```

2. **Green Phase** - Write minimal code:
```java
// Example: AuthController test first
@Test
void shouldAuthenticateValidUser() {
    LoginRequest request = new LoginRequest("demo", "password");
    ResponseEntity<LoginResponse> response = authController.login(request);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody().getToken());
}
```

3. **Refactor Phase** - Improve design:
```bash
./gradlew test  # Should pass after implementation
```

### Frontend TDD Cycle

1. **Red Phase** - Write failing test:
```bash
cd frontend
ng test --watch=false  # Should fail initially
```

2. **Green Phase** - Write minimal code:
```typescript
// Example: Login component test first
it('should authenticate user on valid credentials', () => {
  component.loginForm.patchValue({ username: 'demo', password: 'password' });
  spyOn(authService, 'login').and.returnValue(of({ token: 'test-token' }));
  
  component.onSubmit();
  
  expect(authService.login).toHaveBeenCalledWith('demo', 'password');
});
```

3. **Refactor Phase** - Improve design:
```bash
ng test --watch=false  # Should pass after implementation
```

## Development Commands

### Backend Development

```bash
cd backend

# Run tests
./gradlew test

# Run application  
./gradlew bootRun

# Build application
./gradlew build

# Clean build
./gradlew clean build
```

### Frontend Development

```bash
cd frontend

# Run tests
ng test

# Run development server
ng serve

# Build for production
ng build

# Lint code
ng lint
```

## Testing Strategy

### Backend Testing Layers

1. **Unit Tests** - Service layer with mocked dependencies:
```bash
./gradlew test --tests "*Service*"
```

2. **Integration Tests** - Repository layer with database:
```bash
./gradlew test --tests "*Repository*"
```

3. **Contract Tests** - Controller layer with MockMvc:
```bash
./gradlew test --tests "*Controller*"
```

### Frontend Testing Layers

1. **Component Tests** - UI logic with mocked services:
```bash
ng test --include="**/*.component.spec.ts"
```

2. **Service Tests** - HTTP layer with HttpClientTestingModule:
```bash
ng test --include="**/*.service.spec.ts"
```

3. **Integration Tests** - Full component integration:
```bash
ng e2e  # If configured
```

## Implementation Order (Following Spec Priorities)

### Phase 1: Authentication (P1 User Story)

**Backend Tasks**:
1. Create User entity and repository
2. Implement AuthController with login endpoint
3. Add session management service
4. Write comprehensive tests

**Frontend Tasks**:
1. Create login component with form validation  
2. Implement authentication service
3. Add route guard for protected routes
4. Write component and service tests

**Test the Integration**:
```bash
# Start backend
cd backend && ./gradlew bootRun

# Start frontend (new terminal)
cd frontend && ng serve

# Visit http://localhost:4200
# Login with: demo / password
```

### Phase 2: Account Overview (P2 User Story)

**Backend Tasks**:
1. Create Account and Transaction entities
2. Implement AccountController and TransactionController
3. Add initial test data (data.sql)
4. Write repository and service tests

**Frontend Tasks**:
1. Create dashboard component
2. Implement account-list and transaction-list components
3. Add account and transaction services
4. Write comprehensive tests

### Phase 3: Money Transfer (P3 User Story)

**Backend Tasks**:
1. Implement TransferController with validation
2. Add transfer service with business logic
3. Update account balances atomically
4. Write transfer integration tests

**Frontend Tasks**:
1. Create transfer dialog component
2. Implement transfer form with validation
3. Add transfer service for API calls
4. Write end-to-end transfer tests

## Debugging and Troubleshooting

### Common Issues

**Backend Won't Start**:
```bash
# Check Java version
java --version

# Verify SQLite database
ls -la banking.db

# Check application logs
./gradlew bootRun --debug
```

**Frontend Won't Start**:
```bash
# Clear npm cache
npm cache clean --force

# Reinstall dependencies
rm -rf node_modules package-lock.json
npm install

# Check Angular CLI
ng version
```

**Tests Failing**:
```bash
# Backend - run specific test
./gradlew test --tests "AuthControllerTest"

# Frontend - run specific test  
ng test --include="auth.service.spec.ts"
```

### Database Inspection

```bash
# Install SQLite CLI (if needed)
brew install sqlite  # macOS
sudo apt install sqlite3  # Ubuntu

# Inspect database
cd backend
sqlite3 banking.db
.tables
.schema accounts
SELECT * FROM accounts;
.quit
```

## API Testing

### Using curl

```bash
# Login to get token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","password":"password"}'

# Get accounts (replace TOKEN)
curl -X GET http://localhost:8080/api/accounts \
  -H "Authorization: Bearer TOKEN"

# Transfer money
curl -X POST http://localhost:8080/api/transfers \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN" \
  -d '{"fromAccountId":1,"toAccountId":2,"amount":100.00,"description":"Test transfer"}'
```

### Expected Flow

1. **Login** → Get session token
2. **Dashboard** → Load accounts and recent transactions
3. **Transfer** → Select accounts, enter amount, confirm
4. **Verification** → See updated balances and new transaction

## Production Build

### Backend

```bash
cd backend
./gradlew build
java -jar build/libs/banking-app-1.0.0.jar
```

### Frontend

```bash
cd frontend
ng build --configuration production
# Serve dist/frontend/ with web server
```

## Next Steps

After completing the MVP:

1. **Security**: Add proper authentication and authorization
2. **Validation**: Enhanced input validation and error handling  
3. **Performance**: Database optimization and caching
4. **Features**: Account creation, transaction filtering, reporting
5. **Deployment**: Containerization and cloud deployment

## Support

- **Specification**: [spec.md](./spec.md) - Complete feature requirements
- **Data Model**: [data-model.md](./data-model.md) - Entity relationships  
- **API Contracts**: [contracts/api-spec.yaml](./contracts/api-spec.yaml) - REST API specification
- **Architecture**: [plan.md](./plan.md) - Implementation plan and technical decisions