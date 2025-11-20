/**
 * Transaction model interface for frontend use.
 * Represents a money transfer between accounts.
 */
export interface Transaction {
  id: number;
  fromAccountId: number;
  toAccountId: number;
  amount: number;
  description?: string;
  transactionDate: string;
}
