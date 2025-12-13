import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login';
import { RegisterComponent } from './pages/register/register';
import { DashboardComponent } from './pages/dashboard/dashboard';
import { ProjectDetailsComponent } from './pages/project-details/project-details';
import { authGuard } from './guards/auth-guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { 
    path: 'dashboard', 
    component: DashboardComponent,
    canActivate: [authGuard] // Protect this route!
  },
  { 
    path: 'projects/:id', 
    component: ProjectDetailsComponent,
    canActivate: [authGuard] 
  },
  { path: '', redirectTo: 'login', pathMatch: 'full' } // Default to login
];