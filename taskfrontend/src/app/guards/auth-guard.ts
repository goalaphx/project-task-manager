import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth';

export const authGuard: CanActivateFn = async (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Fast path: if we already have a cached user
  if (authService.isLoggedInSync()) {
    return true;
  }

  // Otherwise ask backend via AuthService (which updates UserService)
  const user = await authService.getCurrentUser();
  if (user) return true;

  router.navigate(['/login']);
  return false;
};