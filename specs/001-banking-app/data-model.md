# Data Model: Personal Banking Application

**Phase**: 1 - Design & Contracts  
**Date**: November 19, 2025  
**Status**: Complete

## Entity Definitions

### User

**Purpose**: Represents an authenticated person using the application

**Attributes**:
| Name | Type | Constraints | Description |
|------|------|-------------|-------------|
| id | Long | PRIMARY KEY, AUTO_INCREMENT | Unique identifier |
| username | String(100) | NOT NULL, UNIQUE | User's login identifier |
| createdAt | DateTime | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Account creation timestamp |

**Relationships**:
- One User → Many Accounts (one-to-many)

**Validation Rules**:
- Username: Non-empty (validation per spec clarification)

**Notes**:
- No password field (simple/fake auth per spec)
- No other user details needed for MVP

---

### Account

**Purpose**: Represents a financial account belonging to a user

**Attributes**:
| Name | Type | Constraints | Description |
|------|------|-------------|-------------|
| id | Long | PRIMARY KEY, AUTO_INCREMENT | Unique identifier |
| userId | Long | FOREIGN KEY(users.id), NOT NULL | Owner of the account |
| accountType | String(20) | NOT NULL | Type: "CHECKING" or "SAVINGS" |
| accountNumber | String(50) | NOT NULL, UNIQUE | Display number (e.g., "****1234") |
| balance | Decimal(15,2) | NOT NULL, DEFAULT 0.00 | Current balance in USD |
| createdAt | DateTime | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Account creation timestamp |

**Relationships**:
- Many Accounts → One User (many-to-one)
- One Account → Many Transactions (one-to-many)

**Validation Rules**:
- Balance: Must be >= 0.00 (enforced in transfer logic)
- AccountType: Must be "CHECKING" or "SAVINGS"
- Precision: Exactly 2 decimal places (USD format per spec)

**Constraints**:
- User must own account to perform operations
- Balance updated atomically during transfers

---

### Transaction

**Purpose**: Represents a financial activity on an account

**Attributes**:
| Name | Type | Constraints | Description |
|------|------|-------------|-------------|
| id | Long | PRIMARY KEY, AUTO_INCREMENT | Unique identifier |
| accountId | Long | FOREIGN KEY(accounts.id), NOT NULL | Account this transaction belongs to |
| amount | Decimal(15,2) | NOT NULL | Amount in USD (positive = credit, negative = debit) |
| description | String(255) | NOT NULL | Transaction description |
| note | String(500) | NULL | Optional user note (for transfers) |
| transactionDate | DateTime | NOT NULL, DEFAULT CURRENT_TIMESTAMP | When transaction occurred |
| transferId | Long | FOREIGN KEY(transfers.id), NULL | Link to transfer if applicable |

**Relationships**:
- Many Transactions → One Account (many-to-one)
- Many Transactions → One Transfer (many-to-one, optional)

**Validation Rules**:
- Amount: Must have exactly 2 decimal places
- Amount: Cannot be zero (enforced in transfer logic)
- Description: Non-empty, max 255 characters

**Display Rules**:
- Show in USD format: $1,234.56 (per spec clarification)
- Order by transactionDate DESC for "recent" queries
- Limit to 10 most recent per account (per spec clarification)

---

### Transfer

**Purpose**: Represents money movement between two accounts owned by the same user

**Attributes**:
| Name | Type | Constraints | Description |
|------|------|-------------|-------------|
| id | Long | PRIMARY KEY, AUTO_INCREMENT | Unique identifier |
| sourceAccountId | Long | FOREIGN KEY(accounts.id), NOT NULL | Account money is withdrawn from |
| destinationAccountId | Long | FOREIGN KEY(accounts.id), NOT NULL | Account money is deposited to |
| amount | Decimal(15,2) | NOT NULL | Transfer amount in USD |
| note | String(500) | NULL | Optional user note |
| transferDate | DateTime | NOT NULL, DEFAULT CURRENT_TIMESTAMP | When transfer was executed |
| userId | Long | FOREIGN KEY(users.id), NOT NULL | User who initiated transfer |

**Relationships**:
- One Transfer → Two Transactions (one debit, one credit)
- Many Transfers → One User (many-to-one)

**Validation Rules**:
- Source and destination must belong to same user
- Source ≠ Destination (cannot transfer to same account)
- Amount > 0.00 (must be positive)
- Source balance >= amount (sufficient funds)

**Constraints**:
- Atomic operation: Both account balances and transactions created together
- Sequential processing: Transfers processed in order received (synchronized method)

---

## Entity Relationships Diagram

```text
┌─────────────────┐
│      User       │
│─────────────────│
│ id (PK)         │
│ username        │
│ createdAt       │
└────────┬────────┘
         │ 1
         │
         │ N
┌────────▼────────┐       N        ┌─────────────────┐
│    Account      │◄────────────────│   Transaction   │
│─────────────────│                 │─────────────────│
│ id (PK)         │                 │ id (PK)         │
│ userId (FK)     │                 │ accountId (FK)  │
│ accountType     │                 │ amount          │
│ accountNumber   │                 │ description     │
│ balance         │                 │ note            │
│ createdAt       │                 │ transactionDate │
└────┬───────┬────┘                 │ transferId (FK) │
     │       │                      └────────┬────────┘
     │       │                               │
     │       │                               │ N
     │       │                               │
     │       │                      ┌────────▼────────┐
     │       │                      │    Transfer     │
     │       └─────────────────────►│─────────────────│
     │         sourceAccountId      │ id (PK)         │
     │                              │ sourceAcctId(FK)│
     └─────────────────────────────►│ destAcctId (FK) │
               destinationAccountId │ amount          │
                                    │ note            │
                                    │ transferDate    │
                                    │ userId (FK)     │
                                    └─────────────────┘
```

## Database Schema (SQLite DDL)

```sql
-- Users table
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Accounts table
CREATE TABLE accounts (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    account_type VARCHAR(20) NOT NULL CHECK(account_type IN ('CHECKING', 'SAVINGS')),
    account_number VARCHAR(50) NOT NULL UNIQUE,
    balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00 CHECK(balance >= 0),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Transactions table
CREATE TABLE transactions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    account_id INTEGER NOT NULL,
    amount DECIMAL(15, 2) NOT NULL CHECK(amount != 0),
    description VARCHAR(255) NOT NULL,
    note VARCHAR(500),
    transaction_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    transfer_id INTEGER,
    FOREIGN KEY (account_id) REFERENCES accounts(id),
    FOREIGN KEY (transfer_id) REFERENCES transfers(id)
);

-- Transfers table
CREATE TABLE transfers (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    source_account_id INTEGER NOT NULL,
    destination_account_id INTEGER NOT NULL,
    amount DECIMAL(15, 2) NOT NULL CHECK(amount > 0),
    note VARCHAR(500),
    transfer_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id INTEGER NOT NULL,
    FOREIGN KEY (source_account_id) REFERENCES accounts(id),
    FOREIGN KEY (destination_account_id) REFERENCES accounts(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    CHECK(source_account_id != destination_account_id)
);

-- Indexes for common queries (MVP: basic indexes only)
CREATE INDEX idx_accounts_user ON accounts(user_id);
CREATE INDEX idx_transactions_account ON transactions(account_id);
CREATE INDEX idx_transactions_date ON transactions(transaction_date);
CREATE INDEX idx_transfers_user ON transfers(user_id);
```

## Sample Data

```sql
-- Demo user
INSERT INTO users (username) VALUES ('demo');

-- Demo accounts
INSERT INTO accounts (user_id, account_type, account_number, balance) 
VALUES 
    (1, 'CHECKING', '****1234', 5000.00),
    (1, 'SAVINGS', '****5678', 10000.00);

-- Demo transactions
INSERT INTO transactions (account_id, amount, description, transaction_date)
VALUES
    (1, -50.00, 'Grocery Store', datetime('now', '-5 days')),
    (1, -120.00, 'Gas Station', datetime('now', '-4 days')),
    (1, 1000.00, 'Paycheck Deposit', datetime('now', '-3 days')),
    (2, 500.00, 'Interest Payment', datetime('now', '-2 days')),
    (1, -35.00, 'Restaurant', datetime('now', '-1 day'));
```

## JPA Entity Mappings

### User Entity

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 100)
    private String username;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "user")
    private List<Account> accounts;
    
    // Getters, setters, constructors
}
```

### Account Entity

```java
@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false, length = 20)
    private AccountType accountType;
    
    @Column(name = "account_number", nullable = false, unique = true, length = 50)
    private String accountNumber;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "account")
    private List<Transaction> transactions;
    
    // Getters, setters, constructors
}
```

### Transaction Entity

```java
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
    
    @Column(nullable = false, length = 255)
    private String description;
    
    @Column(length = 500)
    private String note;
    
    @Column(name = "transaction_date", nullable = false)
    @CreationTimestamp
    private LocalDateTime transactionDate;
    
    @ManyToOne
    @JoinColumn(name = "transfer_id")
    private Transfer transfer;
    
    // Getters, setters, constructors
}
```

### Transfer Entity

```java
@Entity
@Table(name = "transfers")
public class Transfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "source_account_id", nullable = false)
    private Account sourceAccount;
    
    @ManyToOne
    @JoinColumn(name = "destination_account_id", nullable = false)
    private Account destinationAccount;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
    
    @Column(length = 500)
    private String note;
    
    @Column(name = "transfer_date", nullable = false)
    @CreationTimestamp
    private LocalDateTime transferDate;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @OneToMany(mappedBy = "transfer")
    private List<Transaction> transactions;
    
    // Getters, setters, constructors
}
```

## TypeScript Interfaces (Frontend)

```typescript
export interface User {
  id: number;
  username: string;
}

export interface Account {
  id: number;
  accountType: 'CHECKING' | 'SAVINGS';
  accountNumber: string;
  balance: number; // Displayed as USD $X,XXX.XX
}

export interface Transaction {
  id: number;
  accountId: number;
  amount: number; // Displayed as USD $X,XXX.XX
  description: string;
  note?: string;
  transactionDate: string; // ISO 8601 format
}

export interface Transfer {
  sourceAccountId: number;
  destinationAccountId: number;
  amount: number;
  note?: string;
}
```

## State Transitions

### Account Balance Updates

```text
Initial State: Account.balance = $1000.00

Transfer Out ($200):
  1. Validate: balance >= 200
  2. Update: balance = 1000.00 - 200.00 = $800.00
  3. Create Transaction: amount = -200.00, description = "Transfer to ****5678"
  4. Commit

Transfer In ($300):
  1. Update: balance = 800.00 + 300.00 = $1100.00
  2. Create Transaction: amount = +300.00, description = "Transfer from ****1234"
  3. Commit

Final State: Account.balance = $1100.00
```

### Transfer Lifecycle

```text
1. Initiated → User submits TransferRequest
2. Validated → Service checks:
   - Source account exists and belongs to user
   - Destination account exists and belongs to user
   - Source ≠ Destination
   - Amount > 0
   - Source balance >= amount
3. Executing → Within @Transactional + synchronized:
   - Debit source account
   - Credit destination account
   - Create source transaction (negative amount)
   - Create destination transaction (positive amount)
   - Create transfer record linking both
4. Committed → All entities persisted atomically
5. Completed → Response sent to frontend with updated balances
```

## Data Validation Summary

| Entity | Field | Validation | Enforced By |
|--------|-------|------------|-------------|
| User | username | Non-empty | Backend service + DB constraint |
| Account | balance | >= 0.00 | Backend logic + DB check |
| Account | accountType | CHECKING or SAVINGS | Enum + DB check |
| Transaction | amount | != 0, 2 decimals | Backend logic |
| Transaction | description | Non-empty, <= 255 | Backend validation |
| Transfer | amount | > 0, 2 decimals | Backend validation + DB check |
| Transfer | source ≠ dest | Different accounts | Backend validation + DB check |
| Transfer | ownership | Same user owns both | Backend validation |

All validations align with functional requirements FR-001 through FR-019 and spec clarifications.
