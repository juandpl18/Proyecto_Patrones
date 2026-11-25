import { Routes } from '@angular/router';
import { authGuard, guestGuard, roleGuard } from './core/auth.guard';
import { LoginPage } from './pages/login.page';
import { DashboardPage } from './pages/dashboard.page';
import { PlantsPage } from './pages/plants.page';
import { NetworkPage } from './pages/network.page';
import { ConsumptionPage } from './pages/consumption.page';
import { BalancePage } from './pages/balance.page';
import { BillingPage } from './pages/billing.page';
import { AlertsPage } from './pages/alerts.page';
import { UsersPage } from './pages/users.page';
import { AppLayout } from './layout/app-layout.component';

export const routes: Routes = [
  { path: 'login', component: LoginPage, canActivate: [guestGuard] },
  {
    path: '',
    component: AppLayout,
    canActivate: [authGuard],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'inicio' },
      { path: 'inicio', component: DashboardPage },
      { path: 'generacion', component: PlantsPage },
      { path: 'red', component: NetworkPage },
      { path: 'consumo', component: ConsumptionPage },
      { path: 'balanceo', component: BalancePage },
      { path: 'facturacion', component: BillingPage },
      { path: 'alertas', component: AlertsPage },
      { path: 'usuarios', component: UsersPage, canActivate: [roleGuard], data: { roles: ['ADMIN'] } }
    ]
  },
  { path: '**', redirectTo: '' }
];
