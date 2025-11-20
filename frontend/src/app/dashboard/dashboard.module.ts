import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { DashboardComponent } from './dashboard.component';
import { AccountListComponent } from '../accounts/account-list/account-list.component';
import { TransactionListComponent } from '../transactions/transaction-list/transaction-list.component';

@NgModule({
  declarations: [
    DashboardComponent,
    AccountListComponent,
    TransactionListComponent
  ],
  imports: [
    CommonModule,
    RouterModule.forChild([
      { path: '', component: DashboardComponent }
    ])
  ]
})
export class DashboardModule { }