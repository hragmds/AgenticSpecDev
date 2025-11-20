# Data Model: Banking App MVP

**Feature**: Banking App MVP  
**Date**: 2025-11-19  
**Input**: Functional requirements from [spec.md](./spec.md)

## Entity Relationships

```text
┌─────────────┐         ┌─────────────────┐         ┌──────────────────┐
│    User     │ 1     * │     Account     │ *     * │   Transaction    │
│             │◄────────│                 │◄────────│                  │
│ - id        │         │ - id            │         │ - id             │
│ - username  │         │ - userId        │         │ - fromAccountId  │
│ - password  │         │ - accountType   │         │ - toAccountId    │
│             │         │ - accountName   │         │ - amount         │
│             │         │ - balance       │         │ - description    │
│             │         │ - createdAt     │         │ - transactionDate│
└─────────────┘         └─────────────────┘         └──────────────────┘
```

## Entity Specifications

### User Entity

Represents an authenticated banking customer with credentials and account ownership.

**Attributes**:
- `id` (Long): Primary key, auto-generated
- `username` (String): Unique login identifier, max 50 characters
- `password` (String): Authentication credential, max 100 characters

**Business Rules**:
- Username must be unique across system
- For MVP: Single hardcoded user with username "demo", password "password"
- No password hashing required (MVP scope - no security)

**Database Constraints**:
```sql
- Primary key: id
- Unique constraint: username  
- Not null: username, password
- Max lengths: username(50), password(100)
```

### Account Entity

Represents a financial account belonging to a user with balance and type information.

**Attributes**:
- `id` (Long): Primary key, auto-generated
- `userId` (Long): Foreign key to User entity
- `accountType` (String): Account classification - "CHECKING" or "SAVINGS"  
- `accountName` (String): Display name for account, max 100 characters
- `balance` (BigDecimal): Current account balance with 2 decimal precision
- `createdAt` (LocalDateTime): Account creation timestamp

**Business Rules**:
- Each user must have 2-3 accounts (per specification FR-014)
- Account types limited to CHECKING and SAVINGS (per clarification)
- Balance must maintain 2 decimal precision (per specification FR-015)
- Balance cannot go negative during transfers (per specification FR-009)

**Database Constraints**:
```sql
- Primary key: id
- Foreign key: userId references User(id)
- Not null: userId, accountType, accountName, balance, createdAt
- Check constraint: accountType IN ('CHECKING', 'SAVINGS')
- Check constraint: balance >= 0.00
- Decimal precision: balance DECIMAL(15,2)
```

### Transaction Entity

Represents a money movement between accounts with audit trail information.

**Attributes**:
- `id` (Long): Primary key, auto-generated
- `fromAccountId` (Long): Source account foreign key (nullable for future deposits)
- `toAccountId` (Long): Destination account foreign key  
- `amount` (BigDecimal): Transfer amount with 2 decimal precision
- `description` (String): Optional transfer note, max 255 characters
- `transactionDate` (LocalDateTime): When transaction occurred

**Business Rules**:
- Amount must be positive (> 0.00)
- Both accounts must exist and belong to same user (MVP scope)
- fromAccountId and toAccountId cannot be identical (per specification FR-012)
- fromAccountId nullable to support future deposit/withdrawal features
- Records created for every successful transfer (per specification FR-011)

**Database Constraints**:
```sql
- Primary key: id
- Foreign key: fromAccountId references Account(id) (nullable)
- Foreign key: toAccountId references Account(id) (not null)
- Not null: toAccountId, amount, transactionDate
- Check constraint: amount > 0.00
- Check constraint: fromAccountId != toAccountId (when not null)
- Decimal precision: amount DECIMAL(15,2)
```

## Data Validation Rules

### Input Validation

**Username Validation**:
- Pattern: Alphanumeric characters only
- Length: 1-50 characters
- Required field

**Password Validation**:
- Length: 1-100 characters (MVP scope - no complexity requirements)
- Required field

**Account Name Validation**:
- Length: 1-100 characters
- Required field
- Free text for user convenience

**Transfer Amount Validation**:
- Format: Decimal with exactly 2 decimal places
- Range: 0.01 to 999,999,999,999.99
- Required field
- Must not exceed source account balance

**Transfer Description Validation**:
- Length: 0-255 characters
- Optional field
- Free text for user notes

### Business Logic Validation

**Account Ownership Validation**:
- All accounts in transfer must belong to authenticated user
- Prevents cross-user transfers (MVP security)

**Balance Validation**:
- Source account balance >= transfer amount
- Prevents overdrafts (per specification FR-009)

**Account Selection Validation**:
- Source and destination accounts must be different
- Both accounts must exist and be active

## Initial Test Data

### Test User

```sql
INSERT INTO users (id, username, password) 
VALUES (1, 'demo', 'password');
```

### Test Accounts

```sql
-- Demo user checking account
INSERT INTO accounts (id, user_id, account_type, account_name, balance, created_at)
VALUES (1, 1, 'CHECKING', 'Primary Checking', 1500.00, CURRENT_TIMESTAMP);

-- Demo user savings account  
INSERT INTO accounts (id, user_id, account_type, account_name, balance, created_at)
VALUES (2, 1, 'SAVINGS', 'Emergency Savings', 5000.00, CURRENT_TIMESTAMP);

-- Demo user second checking account
INSERT INTO accounts (id, user_id, account_type, account_name, balance, created_at)
VALUES (3, 1, 'CHECKING', 'Secondary Checking', 750.50, CURRENT_TIMESTAMP);
```

### Test Transactions (Optional)

```sql
-- Sample transfer from checking to savings
INSERT INTO transactions (id, from_account_id, to_account_id, amount, description, transaction_date)
VALUES (1, 1, 2, 100.00, 'Monthly savings', '2025-11-15 10:30:00');

-- Sample transfer from savings to checking
INSERT INTO transactions (id, from_account_id, to_account_id, amount, description, transaction_date)
VALUES (2, 2, 1, 250.75, 'Emergency withdrawal', '2025-11-16 14:45:00');
```

## Database Schema Migration

### Creation Scripts

**Users Table**:
```sql
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL
);
```

**Accounts Table**:
```sql
CREATE TABLE accounts (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    account_type VARCHAR(20) NOT NULL CHECK (account_type IN ('CHECKING', 'SAVINGS')),
    account_name VARCHAR(100) NOT NULL,
    balance DECIMAL(15,2) NOT NULL DEFAULT 0.00 CHECK (balance >= 0.00),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

**Transactions Table**:
```sql
CREATE TABLE transactions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    from_account_id INTEGER,
    to_account_id INTEGER NOT NULL,
    amount DECIMAL(15,2) NOT NULL CHECK (amount > 0.00),
    description TEXT,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (from_account_id) REFERENCES accounts(id),
    FOREIGN KEY (to_account_id) REFERENCES accounts(id),
    CHECK (from_account_id IS NULL OR from_account_id != to_account_id)
);
```

**Indexes for Performance**:
```sql
CREATE INDEX idx_accounts_user_id ON accounts(user_id);
CREATE INDEX idx_transactions_from_account ON transactions(from_account_id);
CREATE INDEX idx_transactions_to_account ON transactions(to_account_id);
CREATE INDEX idx_transactions_date ON transactions(transaction_date DESC);
```

## Entity Mapping Strategy

### JPA Entity Annotations

**User Entity Mapping**:
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 50)
    private String username;
    
    @Column(nullable = false, length = 100)
    private String password;
    
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Account> accounts;
}
```

**Account Entity Mapping**:
```java
@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private AccountType accountType;
    
    @Column(name = "account_name", nullable = false, length = 100)
    private String accountName;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
```

**Transaction Entity Mapping**:
```java
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_account_id")
    private Account fromAccount;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_account_id", nullable = false)
    private Account toAccount;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
    
    @Column(length = 255)
    private String description;
    
    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;
}
```