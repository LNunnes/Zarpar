import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal, PLATFORM_ID } from '@angular/core'; // <--- Importe PLATFORM_ID
import { isPlatformBrowser } from '@angular/common'; // <--- Importe isPlatformBrowser
import { tap } from 'rxjs';
import { Router } from '@angular/router';
import { AuthResponse, User } from '../models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private platformId = inject(PLATFORM_ID);
  private apiUrl = 'http://localhost:8080/api/auth';

  private currentUserSignal = signal<User | null>(this.getUserFromStorage());

  public currentUser = this.currentUserSignal.asReadonly();
  public isLoggedIn = computed(() => !!this.currentUserSignal());
  public isAdmin = computed(() => this.currentUserSignal()?.role === 'ADMIN');

  constructor() {}

  login(credentials: { email: string; senha: string }) {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap((response) => this.handleAuthSuccess(response))
    );
  }

  register(data: { nome: string; email: string; senha: string }) {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, data).pipe(
      tap((response) => this.handleAuthSuccess(response))
    );
  }

logout() {
    return this.http.post(`${this.apiUrl}/logout`, {}, { responseType: 'text' as 'json' }).pipe(
      tap(() => {
        this.currentUserSignal.set(null);
        if (isPlatformBrowser(this.platformId)) {
          localStorage.removeItem('zarpar_user');
        }
        this.router.navigate(['/login']);
      })
    );
  }

  private handleAuthSuccess(response: AuthResponse) {
    const user: User = {
      id: response.id,
      nome: response.nome,
      email: response.email,
      role: response.role
    };

    this.currentUserSignal.set(user);

    if (isPlatformBrowser(this.platformId)) {
      localStorage.setItem('zarpar_user', JSON.stringify(user));
    }
  }

  private getUserFromStorage(): User | null {
    if (!isPlatformBrowser(this.platformId)) {
      return null;
    }

    const userStr = localStorage.getItem('zarpar_user');
    return userStr ? JSON.parse(userStr) : null;
  }
}
