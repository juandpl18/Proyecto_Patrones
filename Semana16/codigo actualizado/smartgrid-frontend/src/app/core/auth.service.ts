import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';

export interface LoginResponse {
  mensaje: string;
  usuario: string | null;
  rol: string | null;
  token: string | null;
  expiraEnSegundos: number;
}
export interface SessionUser {
  username: string;
  role: string;
  token: string;
  expiresAt: number;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly storageKey = 'smartgrid.session';
  readonly user = signal<SessionUser | null>(this.readSession());

  constructor(private http: HttpClient, private router: Router) {}

  login(username: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${environment.apiUrl}/api/auth/login`, { username, password }).pipe(
      tap(response => {
        if (response.token && response.usuario && response.rol) {
          const session: SessionUser = {
            username: response.usuario,
            role: String(response.rol),
            token: response.token,
            expiresAt: Date.now() + response.expiraEnSegundos * 1000
          };
          localStorage.setItem(this.storageKey, JSON.stringify(session));
          this.user.set(session);
        }
      })
    );
  }

  logout(): void {
    localStorage.removeItem(this.storageKey);
    this.user.set(null);
    this.router.navigateByUrl('/login');
  }

  token(): string | null {
    const current = this.user();
    if (!current) return null;
    if (current.expiresAt <= Date.now()) {
      this.logout();
      return null;
    }
    return current.token;
  }

  hasRole(...roles: string[]): boolean {
    const role = this.user()?.role;
    return !!role && roles.includes(role);
  }

  private readSession(): SessionUser | null {
    try {
      const raw = localStorage.getItem(this.storageKey);
      if (!raw) return null;
      const parsed = JSON.parse(raw) as SessionUser;
      if (!parsed.token || parsed.expiresAt <= Date.now()) {
        localStorage.removeItem(this.storageKey);
        return null;
      }
      return parsed;
    } catch {
      localStorage.removeItem(this.storageKey);
      return null;
    }
  }
}
