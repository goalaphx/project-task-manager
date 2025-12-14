import { Component, signal, OnInit } from '@angular/core';
import { RouterOutlet, Router, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from './services/auth';
import { UserService } from './services/user.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.html',
  styleUrls: ['./app.css']
})
export class App implements OnInit {
  protected readonly title = signal('taskfrontend');
  userName: string | null = null;
  private sub: Subscription | null = null;
  readonly year = new Date().getFullYear();

  constructor(private auth: AuthService, private router: Router, private userService: UserService) {}

  async ngOnInit() {
    // Prime user state from backend (if cookie present)
    try {
      this.userName = await this.auth.getCurrentUser();
      // subscribe to reactive user changes
  this.sub = this.userService.getUser$().subscribe((u: string | null) => this.userName = u);
    } catch (e) {
      this.userName = null;
    }
  }

  async logout() {
    await this.auth.logout();
    this.userName = null;
    this.router.navigate(['/login']);
  }
  ngOnDestroy(): void {
    if (this.sub) this.sub.unsubscribe();
  }
}
