import { Component, Input } from '@angular/core';
import { Account } from '../account.model';

/**
 * Account list component to display user accounts.
 * Shows account type, name, and balance.
 */
@Component({
  selector: 'app-account-list',
  template: `
    <div class="grid gap-4 grid-cols-1 md:grid-cols-2 lg:grid-cols-3">
      <div
        *ngFor="let account of accounts"
        class="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow cursor-pointer">
        <div class="flex justify-between items-start mb-2">
          <h4 class="text-sm font-semibold text-gray-900">{{ account.accountName }}</h4>
          <span class="text-xs px-2 py-1 bg-gray-100 text-gray-800 rounded">
            {{ account.accountType }}
          </span>
        </div>
        <div class="text-2xl font-bold text-gray-900 mb-2">
          ${{ account.balance | number: '1.2-2' }}
        </div>
        <div class="text-xs text-gray-500">
          Account ID: {{ account.id }}
        </div>
      </div>

      <!-- Empty State -->
      <div *ngIf="accounts.length === 0" class="col-span-full text-center py-8 text-gray-500">
        No accounts found
      </div>
    </div>
  `,
  styleUrls: ['./account-list.component.scss']
})
export class AccountListComponent {
  @Input() accounts: Account[] = [];
}
