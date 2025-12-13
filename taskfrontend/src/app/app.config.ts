import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { authInterceptor } from './interceptors/auth.interceptor'; // Import check

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    // IMPORTANT: This line activates the token logic
    provideHttpClient(withInterceptors([authInterceptor])) 
  ]
};