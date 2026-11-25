import { Component, computed, signal } from '@angular/core';
import { NavigationEnd, Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../core/auth.service';

@Component({
  selector: 'sg-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
  <div class="app-shell">
    <aside class="sidebar" [class.open]="menuOpen()">
      <a class="brand" routerLink="/">
        <span class="brand-mark"><svg viewBox="0 0 40 40" aria-hidden="true"><path d="M22 3 8 22h10l-2 15 16-22H21l1-12Z" fill="none" stroke="currentColor" stroke-width="2.8" stroke-linejoin="round"/></svg></span>
        <span><strong>smartgrid</strong><small>ENERGY MANAGEMENT</small></span>
      </a>
      <div class="workspace-label">PLATAFORMA</div>
      <nav class="nav-list">
        <a routerLink="/inicio" routerLinkActive="active" (click)="closeMenu()"><span class="nav-icon">◫</span>Resumen general</a>
        <div class="nav-caption">OPERACIÓN</div>
        <a routerLink="/generacion" routerLinkActive="active" (click)="closeMenu()"><span class="nav-icon">ϟ</span>Generación</a>
        <a routerLink="/red" routerLinkActive="active" (click)="closeMenu()"><span class="nav-icon">⌘</span>Red eléctrica</a>
        <a routerLink="/consumo" routerLinkActive="active" (click)="closeMenu()"><span class="nav-icon">▥</span>Consumo</a>
        <a routerLink="/balanceo" routerLinkActive="active" (click)="closeMenu()"><span class="nav-icon">⇄</span>Balanceo de cargas</a>
        <div class="nav-caption">GESTIÓN</div>
        <a routerLink="/facturacion" routerLinkActive="active" (click)="closeMenu()"><span class="nav-icon">▤</span>Facturación</a>
        <a routerLink="/alertas" routerLinkActive="active" (click)="closeMenu()"><span class="nav-icon">♧</span>Alertas</a>
        @if (auth.hasRole('ADMIN')) {
          <a routerLink="/usuarios" routerLinkActive="active" (click)="closeMenu()"><span class="nav-icon">♙</span>Usuarios</a>
        }
      </nav>
      <div class="sidebar-bottom">
        <div class="system-status"><span class="status-dot"></span><span><b>Sistema operativo</b><small>Servicios disponibles</small></span></div>
        <div class="sidebar-foot">SmartGrid · Plataforma académica<br>Gestión inteligente de energía</div>
      </div>
    </aside>
    <div class="mobile-scrim" [class.visible]="menuOpen()" (click)="closeMenu()"></div>
    <main class="main-area">
      <header class="topbar">
        <button class="mobile-menu" type="button" (click)="toggleMenu()" aria-label="Abrir menú">☰</button>
        <div class="breadcrumb"><span>SmartGrid</span><i>/</i><strong>{{ currentSection() }}</strong></div>
        <div class="top-actions">
          <a class="top-alert" routerLink="/alertas" title="Ver alertas">♧</a>
          <div class="user-chip">
            <div class="avatar">{{ initials() }}</div>
            <div class="user-meta"><b>{{ auth.user()?.username }}</b><small>{{ auth.user()?.role === 'ADMIN' ? 'Administrador' : 'Operador' }}</small></div>
          </div>
          <button class="logout-btn" (click)="auth.logout()" title="Cerrar sesión">↗</button>
        </div>
      </header>
      <section class="page-content"><router-outlet /></section>
    </main>
  </div>
  `
})
export class AppLayout {
  menuOpen = signal(false);
  private path = signal('inicio');
  currentSection = computed(() => {
    const names: Record<string,string> = { inicio:'Resumen general', generacion:'Generación', red:'Red eléctrica', consumo:'Consumo', balanceo:'Balanceo de cargas', facturacion:'Facturación', alertas:'Alertas', usuarios:'Usuarios' };
    return names[this.path()] ?? 'Resumen general';
  });
  initials = computed(() => (this.auth.user()?.username ?? 'SG').slice(0,2).toUpperCase());
  constructor(public auth: AuthService, router: Router) {
    this.path.set(router.url.split('/')[1] || 'inicio');
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) this.path.set(event.urlAfterRedirects.split('/')[1] || 'inicio');
    });
  }
  toggleMenu(): void { this.menuOpen.update(v => !v); }
  closeMenu(): void { this.menuOpen.set(false); }
}
