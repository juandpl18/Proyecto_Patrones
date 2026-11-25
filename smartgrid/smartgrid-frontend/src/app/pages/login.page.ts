import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../core/auth.service';

@Component({
  standalone: true,
  imports: [FormsModule],
  template: `
  <main class="login-screen">
    <div class="login-art">
      <div class="login-brand"><span class="login-mark">ϟ</span><span>smartgrid</span></div>
      <div class="art-copy">
        <div class="eyebrow light">INTELLIGENT ENERGY NETWORKS</div>
        <h1>La energía<br><em>conectada.</em><br>El futuro, en equilibrio.</h1>
        <p>Una plataforma para visualizar, administrar y optimizar la operación de redes eléctricas inteligentes.</p>
        <div class="art-stats"><span><b>01</b> Generación renovable</span><span><b>02</b> Consumo inteligente</span><span><b>03</b> Balance energético</span></div>
      </div>
      <div class="grid-orbit orbit-one"></div><div class="grid-orbit orbit-two"></div>
      <div class="login-footer">SMARTGRID · ENERGY MANAGEMENT SYSTEM</div>
    </div>
    <div class="login-panel">
      <div class="login-form-wrap">
        <div class="eyebrow">BIENVENIDO DE NUEVO</div>
        <h2>Iniciar sesión</h2>
        <p class="login-subtitle">Ingresa tus credenciales para acceder al centro de operación.</p>
        <form (ngSubmit)="submit()">
          <label for="username">Usuario</label>
          <input id="username" name="username" [(ngModel)]="username" autocomplete="username" placeholder="Tu nombre de usuario" required>
          <label for="password">Contraseña</label>
          <div class="password-field"><input id="password" name="password" [(ngModel)]="password" [type]="showPassword() ? 'text' : 'password'" autocomplete="current-password" placeholder="Tu contraseña" required><button type="button" (click)="showPassword.set(!showPassword())">{{ showPassword() ? 'Ocultar' : 'Ver' }}</button></div>
          @if (error()) { <div class="form-error">{{ error() }}</div> }
          <button class="btn btn-primary login-submit" [disabled]="loading()">{{ loading() ? 'Validando…' : 'Ingresar al sistema' }} <span>→</span></button>
        </form>
        <div class="login-security"><span>⌑</span> Conexión protegida mediante autenticación JWT</div>
      </div>
      <div class="login-bottom">© 2026 SmartGrid · Sistema de gestión de redes eléctricas</div>
    </div>
  </main>
  `
})
export class LoginPage {
  username = ''; password = '';
  loading = signal(false); error = signal(''); showPassword = signal(false);
  constructor(private auth: AuthService, private router: Router) {}
  submit(): void {
    this.error.set('');
    if (!this.username.trim() || !this.password) { this.error.set('Ingresa tu usuario y contraseña.'); return; }
    this.loading.set(true);
    this.auth.login(this.username.trim(), this.password).subscribe({
      next: response => {
        this.loading.set(false);
        if (response.token) this.router.navigateByUrl('/');
        else this.error.set(response.mensaje || 'No fue posible iniciar sesión.');
      },
      error: err => { this.loading.set(false); this.error.set(err.error?.mensaje || err.error?.message || 'No se pudo conectar con el servidor. Verifica que Spring Boot esté ejecutándose.'); }
    });
  }
}
