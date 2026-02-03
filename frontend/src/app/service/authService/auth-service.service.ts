import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { Router } from '@angular/router';
export interface SignupRequest {
  username: string;
  email: string;
  password: string;
  userType: string;
}

export interface SignupResponse {
  msg: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token?: string;
  msg?: string;
  roles?: string[];
  username?: string;
  ExpirationTime?: number;
}

@Injectable({
  providedIn: 'root'
})
export class AuthServiceService {
  private logoutTimer: any;

  // private signupUrl = `${environment.apiUrl}/auth/signup`;
  // private loginUrl = `${environment.apiUrl}/auth/login`;

  private signupUrl = 'http://localhost:8080/api/auth/signup';
  private loginUrl = 'http://localhost:8080/api/auth/login';

  constructor(private http: HttpClient, private router: Router) { }

  signup(data: SignupRequest): Observable<SignupResponse> {
    return this.http.post<SignupResponse>(this.signupUrl, data);
  }

  login(data: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(this.loginUrl, data);
  }

  startLogoutTimer(expirationTime: number) {
    // Clear any existing timer
    if (this.logoutTimer) {
      clearTimeout(this.logoutTimer);
    }

    // expirationTime should be the number of milliseconds until expiration
    this.logoutTimer = setTimeout(() => {
      this.logout();
    }, expirationTime);
  }

  logout() {
    localStorage.removeItem('Token');
    localStorage.removeItem('Roles');
    localStorage.removeItem('UserName');
    localStorage.removeItem('ExpirationTime');

    this.router.navigate(['/']);
  }
}