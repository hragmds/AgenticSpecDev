-- Initial user data for Banking App MVP
-- This file is loaded during application startup via spring.sql.init.mode=always

-- Insert demo user for authentication testing
-- Password: 'password' (stored as plain text for MVP - no encryption required per spec)
INSERT OR REPLACE INTO users (id, username, password, full_name, email, is_active) 
VALUES (1, 'demo', 'password', 'Demo User', 'demo@bankingapp.com', true);

-- Phase 4 (User Story 2) - Account and Transaction data
-- Insert demo accounts for the user per FR-014 requirements

-- Account 1: Checking account with $1,500.00 balance
INSERT OR REPLACE INTO accounts (id, user_id, account_type, account_name, balance, created_at) 
VALUES (1, 1, 'CHECKING', 'Primary Checking', 1500.00, CURRENT_TIMESTAMP);

-- Account 2: Savings account with $3,250.75 balance  
INSERT OR REPLACE INTO accounts (id, user_id, account_type, account_name, balance, created_at) 
VALUES (2, 1, 'SAVINGS', 'Emergency Savings', 3250.75, CURRENT_TIMESTAMP);

-- Account 3: Additional checking account with $875.50 balance
INSERT OR REPLACE INTO accounts (id, user_id, account_type, account_name, balance, created_at) 
VALUES (3, 1, 'CHECKING', 'Secondary Checking', 875.50, CURRENT_TIMESTAMP);

-- Insert demo transactions for account history (per FR-006 requirements)
-- Recent transactions showing transfers, deposits, and withdrawals

-- Transaction 1: Recent transfer from checking to savings
INSERT OR REPLACE INTO transactions (id, from_account_id, to_account_id, amount, description, transaction_date)
VALUES (1, 1, 2, 200.00, 'Emergency fund contribution', '2025-11-18 14:30:00');

-- Transaction 2: Deposit to primary checking
INSERT OR REPLACE INTO transactions (id, from_account_id, to_account_id, amount, description, transaction_date)
VALUES (2, NULL, 1, 1000.00, 'Salary deposit - Direct Deposit', '2025-11-17 09:00:00');

-- Transaction 3: Transfer between checking accounts  
INSERT OR REPLACE INTO transactions (id, from_account_id, to_account_id, amount, description, transaction_date)
VALUES (3, 1, 3, 150.00, 'Monthly budget allocation', '2025-11-15 16:45:00');

-- Transaction 4: Withdrawal from secondary checking
INSERT OR REPLACE INTO transactions (id, from_account_id, to_account_id, amount, description, transaction_date)
VALUES (4, 3, NULL, 75.00, 'ATM withdrawal', '2025-11-13 18:20:00');

-- Transaction 5: Large savings deposit
INSERT OR REPLACE INTO transactions (id, from_account_id, to_account_id, amount, description, transaction_date)
VALUES (5, NULL, 2, 500.00, 'Tax refund deposit', '2025-11-06 11:15:00');

-- Transaction 6: Transfer from savings to checking
INSERT OR REPLACE INTO transactions (id, from_account_id, to_account_id, amount, description, transaction_date)
VALUES (6, 2, 1, 300.00, 'Monthly expense funding', '2025-10-30 08:30:00');

-- Transaction 7: Recent small transfer
INSERT OR REPLACE INTO transactions (id, from_account_id, to_account_id, amount, description, transaction_date)
VALUES (7, 1, 3, 25.50, 'Coffee fund transfer', '2025-11-19 12:00:00');

-- Transaction 8: Older deposit to show history
INSERT OR REPLACE INTO transactions (id, from_account_id, to_account_id, amount, description, transaction_date)
VALUES (8, NULL, 1, 750.00, 'Freelance payment', '2025-10-20 15:45:00');

-- Note: This data provides a realistic banking scenario for demo purposes
-- Total balances: $1,500.00 + $3,250.75 + $875.50 = $5,626.25
-- Transaction history shows various transfer patterns and account usage