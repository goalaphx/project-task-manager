// ...existing code...
import { HttpInterceptorFn, HttpRequest } from '@angular/common/http';
import { HttpHandler } from '@angular/common/http';
import { Observable } from 'rxjs';
import { HttpEvent } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req: HttpRequest<any>, next: (req: HttpRequest<any>) => Observable<HttpEvent<any>>) => {
    // Ensure the browser sends cookies (jwt cookie) with cross-origin requests
    let modified = req.clone({ withCredentials: true });

    // Optional: add Authorization header if you store the token in localStorage (not needed if you rely only on the cookie)
    const token = localStorage.getItem('token');
    if (token) {
        modified = modified.clone({
            setHeaders: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    return next(modified);
};
// ...existing code...