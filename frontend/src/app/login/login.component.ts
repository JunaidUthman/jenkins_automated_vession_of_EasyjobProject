import { Component } from '@angular/core';
import { AuthServiceService } from '../service/authService/auth-service.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-login',
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  email = '';
  password = '';

  emailError = false;
  passwordError = false;
  UserExists = false;

  constructor(private router: Router, private authService: AuthServiceService, private toastr: ToastrService) { }

  onSubmit() {
    // Reset error flags
    this.emailError = false;
    this.passwordError = false;

    let valid = true;

    if (!this.email.match(/^[^\s@]+@[^\s@]+\.[^\s@]+$/)) {
      this.emailError = true;
      valid = false;
    }
    if (!this.password || this.password.length < 4) {
      this.passwordError = true;
      valid = false;
    }

    if (!valid) {
      return;
    }

    this.authService.login({ email: this.email, password: this.password }).subscribe({
      next: (res) => {
        if (res.token) {
          localStorage.setItem('Token', res.token);
          const roles = (res.roles ?? []).join(',');
          localStorage.setItem('Roles', roles);
          localStorage.setItem('UserName', res.username ?? '');
          localStorage.setItem('ExpirationTime', (res.ExpirationTime ?? 0).toString());
          this.showSuccess();
          setTimeout(() => {
            if (roles.includes('Recrutter')) {
              this.router.navigate(['/dashboard/home']);
            } else {
              this.router.navigate(['/jobs']);
            }
          }, 1500);

          const expiresIn = res.ExpirationTime ?? 0;
          this.authService.startLogoutTimer(expiresIn);


        } else if (res.msg) {
          alert(res.msg);
        }
      },
      error: (err) => {
        if (err.status === 404) {
          alert('User not found');
        } else if (err.status === 401) {
          alert('Invalid password');
        } else {
          console.error('Login error:', err);
          alert('An error occurred during login');
        }
      }
    });
  }


  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('expirationTime');
    localStorage.removeItem('username');

    this.router.navigate(['/login']);
  }

  showSuccess() {
    this.toastr.success('You Are Logged In Successfully!', 'Success');
  }
}