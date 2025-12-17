import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, firstValueFrom } from 'rxjs';
import { UserService } from './user.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = '/api/auth'; // Backend controller base

  constructor(private http: HttpClient, private userService: UserService) { }

  // REGISTER -> returns backend DTO
  register(userData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, userData, { withCredentials: true });
  }

  // LOGIN -> backend sets HttpOnly cookie and returns a simple message
  // We then call /me to fetch current user info
  async login(credentials: any): Promise<boolean> {
    try {
      await firstValueFrom(this.http.post(`${this.apiUrl}/login`, credentials, { responseType: 'text', withCredentials: true }));
      const user = await this.getCurrentUser();
      return !!user;
    } catch (e) {
      this.userService.setUser(null);
      return false;
    }
  }

  // Fetch current user from backend (uses cookie-based session)
  async getCurrentUser(): Promise<string | null> {
    try {
      const username = await firstValueFrom(this.http.get(`${this.apiUrl}/me`, { responseType: 'text', withCredentials: true }));
      const user = username ?? null;
      this.userService.setUser(user);
      return user;
    } catch (e) {
      this.userService.setUser(null);
      return null;
    }
  }

  // Logout: call backend endpoint which clears cookie, and clear local state.
  async logout(): Promise<void> {
    try {
      await firstValueFrom(this.http.post(`${this.apiUrl}/logout`, {}, { responseType: 'text', withCredentials: true }));
    } catch (e) {
      // ignore -- backend may not implement logout
    }
    this.userService.setUser(null);
  }

  // Quick sync check (returns true if we have a cached user)
  isLoggedInSync(): boolean {
    return this.userService.isLoggedInSync();
  }

}