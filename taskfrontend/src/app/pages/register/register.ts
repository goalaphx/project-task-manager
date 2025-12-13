import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth';
import { firstValueFrom } from 'rxjs';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './register.html',
  styleUrls: ['./register.css']
})
export class RegisterComponent {
  fullName = '';
  email = '';
  password = '';
  error: string | null = null;

  constructor(private auth: AuthService, private router: Router) {}

  async submit() {
    this.error = null;
    try {
      await firstValueFrom(this.auth.register({ fullName: this.fullName, email: this.email, password: this.password }));
      this.router.navigate(['/login']);
    } catch (e: any) {
      this.error = e?.message ?? 'Registration failed';
    }
  }
}
