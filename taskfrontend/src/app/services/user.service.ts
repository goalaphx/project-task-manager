import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class UserService {
  private user$ = new BehaviorSubject<string | null>(null);

  setUser(username: string | null) {
    this.user$.next(username);
  }

  getUser$(): Observable<string | null> {
    return this.user$.asObservable();
  }

  isLoggedInSync(): boolean {
    return !!this.user$.getValue();
  }
}
