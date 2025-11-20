# Quickstart Guide: Personal Banking Application

**Last Updated**: November 19, 2025

This guide walks you through setting up and running the Personal Banking Application locally.

## Prerequisites

### Required Software

- **Java Development Kit (JDK)**: Version 17 or higher
  - Download: https://adoptium.net/
  - Verify: `java -version` should show 17+

- **Node.js and npm**: Version 18 or higher
  - Download: https://nodejs.org/
  - Verify: `node --version` and `npm --version`

- **Angular CLI**: Version 17 or higher
  - Install: `npm install -g @angular/cli`
  - Verify: `ng version`

- **Git**: For cloning the repository
  - Download: https://git-scm.com/
  - Verify: `git --version`

### Optional Tools

- **IDE for Backend**: IntelliJ IDEA Community Edition or VS Code with Java extensions
- **IDE for Frontend**: VS Code with Angular Language Service extension
- **API Testing**: Postman or curl for testing endpoints

## Project Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd banking-app
```

### 2. Backend Setup

```bash
# Navigate to backend directory
cd backend

# Build the project (downloads dependencies, runs tests)
./gradlew build

# Or on Windows:
gradlew.bat build
```

**What this does**:
- Downloads Gradle wrapper (if needed)
- Resolves dependencies (Spring Boot, SQLite JDBC driver, etc.)
- Compiles Java code
- Runs unit and integration tests
- Packages the application as a JAR file

**Expected Output**:
```
BUILD SUCCESSFUL in 30s
10 actionable tasks: 10 executed
```

### 3. Frontend Setup

```bash
# Navigate to frontend directory (from project root)
cd frontend

# Install dependencies
npm install

# Install Tailwind CSS
npm install -D tailwindcss postcss autoprefixer
npx tailwindcss init
```

**What this does**:
- Downloads npm packages (Angular, RxJS, Tailwind, etc.)
- Creates `node_modules/` directory
- Generates `package-lock.json`

**Expected Output**:
```
added 1234 packages in 45s
```

## Running the Application

### Start Backend Server

```bash
# From backend/ directory
./gradlew bootRun

# Or run the JAR directly:
java -jar build/libs/banking-app-0.0.1-SNAPSHOT.jar
```

**Backend will start on**: http://localhost:8080

**Verify it's running**:
```bash
curl http://localhost:8080/api/actuator/health
# Expected: {"status":"UP"}
```

**Console Output**:
```
2025-11-19 10:30:00.000  INFO --- Started BankingApplication in 3.5 seconds
```

### Start Frontend Development Server

```bash
# From frontend/ directory
ng serve

# Or with custom port:
ng serve --port 4200
```

**Frontend will start on**: http://localhost:4200

**Verify it's running**: Open browser to http://localhost:4200

**Console Output**:
```
** Angular Live Development Server is listening on localhost:4200 **
✔ Compiled successfully.
```

## First Run Experience

### 1. Access the Application

1. Open browser to http://localhost:4200
2. You should see the **Login Page**

### 2. Log In (Simple/Fake Auth)

- **Username**: Enter any non-empty value (e.g., "demo")
- **Password**: Enter any non-empty value (e.g., "password")
- Click **Login**

**What happens**:
- Frontend sends POST /api/auth/login
- Backend validates fields are non-empty
- Backend creates session token
- Frontend stores token and navigates to dashboard

### 3. View Dashboard

After login, you'll see:
- **Accounts List**: Two demo accounts (Checking and Savings) with balances
- **Recent Transactions**: Last 10 transactions across all accounts
- **Transfer Button**: Opens transfer dialog

### 4. Try a Transfer

1. Click **Transfer Money** button
2. Select **Source Account** (e.g., Checking)
3. Select **Destination Account** (e.g., Savings)
4. Enter **Amount** (e.g., 100.00)
5. Optionally add a **Note**
6. Click **Confirm Transfer**

**What happens**:
- Frontend validates locally (source ≠ destination, amount > 0, etc.)
- Sends POST /api/transfers
- Backend validates and processes transfer sequentially
- Both account balances update atomically
- Dashboard refreshes with new balances and transactions

### 5. Verify Transfer

- Check **Accounts List**: Balances should reflect the transfer
- Check **Transactions List**: Two new transactions appear:
  - Debit on source account (negative amount)
  - Credit on destination account (positive amount)

## Database Location

SQLite database is created automatically on first run:

**Location**: `backend/banking-app.db`

**View Data** (optional):
```bash
# Install SQLite CLI (if not already installed)
# macOS: brew install sqlite
# Ubuntu: sudo apt install sqlite3
# Windows: Download from https://www.sqlite.org/download.html

# Connect to database
sqlite3 backend/banking-app.db

# View tables
.tables

# Query accounts
SELECT * FROM accounts;

# Exit
.quit
```

## Testing

### Run Backend Tests

```bash
cd backend

# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests AuthServiceTest

# Run with code coverage
./gradlew test jacocoTestReport
```

**Test Reports**: `backend/build/reports/tests/test/index.html`

### Run Frontend Tests

```bash
cd frontend

# Run unit tests (Karma + Jasmine)
ng test

# Run in headless mode (CI)
ng test --browsers=ChromeHeadless --watch=false

# Run with code coverage
ng test --code-coverage
```

**Test Reports**: `frontend/coverage/index.html`

## Development Workflow

### Backend Development

1. **Make Changes**: Edit Java files in `backend/src/main/java/`
2. **Write Tests First**: Add tests in `backend/src/test/java/`
3. **Run Tests**: `./gradlew test` (TDD red → green → refactor)
4. **Restart Server**: Ctrl+C and re-run `./gradlew bootRun`

**Hot Reload**: Use Spring Boot DevTools (already included)
```gradle
// In build.gradle
runtimeOnly 'org.springframework.boot:spring-boot-devtools'
```

### Frontend Development

1. **Make Changes**: Edit TypeScript/HTML/CSS in `frontend/src/app/`
2. **Write Tests First**: Add tests in `.spec.ts` files
3. **Run Tests**: `ng test`
4. **Auto-Reload**: `ng serve` automatically reloads on file changes

**No manual restart needed** - Angular CLI watches files

## Common Issues

### Issue: Port 8080 already in use

**Solution**:
```bash
# Find process using port 8080
lsof -i :8080

# Kill the process
kill -9 <PID>

# Or change Spring Boot port in application.properties
server.port=8081
```

### Issue: Port 4200 already in use

**Solution**:
```bash
# Use different port
ng serve --port 4201
```

### Issue: SQLite database locked

**Solution**:
```bash
# Stop backend server
# Delete database file
rm backend/banking-app.db
# Restart backend (will recreate DB)
```

### Issue: CORS errors in browser console

**Solution**:
- Ensure backend `SecurityConfig.java` allows `http://localhost:4200`
- Check frontend is running on correct port
- Verify `X-Session-Token` header is being sent

### Issue: Tests failing

**Solution**:
```bash
# Clean build
./gradlew clean build

# Clear npm cache
npm cache clean --force
rm -rf node_modules package-lock.json
npm install
```

## API Documentation

Once backend is running, access interactive API docs:

**Swagger UI**: http://localhost:8080/swagger-ui.html (if configured)

**OpenAPI Spec**: See `specs/001-banking-app/contracts/api.yaml`

## Stopping the Application

### Stop Backend
- Press `Ctrl+C` in terminal running `./gradlew bootRun`

### Stop Frontend
- Press `Ctrl+C` in terminal running `ng serve`

## Next Steps

- **Read Specification**: See `specs/001-banking-app/spec.md`
- **Review Data Model**: See `specs/001-banking-app/data-model.md`
- **Check Implementation Plan**: See `specs/001-banking-app/plan.md`
- **View Tasks**: See `specs/001-banking-app/tasks.md` (after running `/speckit.tasks`)

## Development URLs

| Service | URL | Purpose |
|---------|-----|---------|
| Frontend | http://localhost:4200 | Angular application |
| Backend API | http://localhost:8080/api | REST endpoints |
| Health Check | http://localhost:8080/api/actuator/health | Server status |
| Database | `backend/banking-app.db` | SQLite file |

## Demo Credentials

**Simple/Fake Auth** - any non-empty values work:
- Username: `demo` (or anything else)
- Password: `password` (or anything else)

## Support

For issues or questions:
1. Check this guide's "Common Issues" section
2. Review specification in `specs/001-banking-app/spec.md`
3. Check test files for examples of correct usage
4. Review constitution in `.specify/memory/constitution.md` for development principles
