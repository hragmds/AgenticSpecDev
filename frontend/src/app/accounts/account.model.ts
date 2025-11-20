/**
 * Account model interface for frontend use.
 * Represents a financial account belonging to a user.
 */
export interface Account {
  id: number;
  userId: number;
  accountType: 'CHECKING' | 'SAVINGS';
  accountName: string;
  balance: number;
  createdAt?: string;
}
