import { Component, OnInit, signal } from '@angular/core';
import { DecimalPipe, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ApiService, AlertItem, GenerationSummary, Invoice, Plant, Zone } from '../core/api.service';

@Component({
  standalone: true,
  imports: [DecimalPipe, DatePipe, RouterLink],
  template: `
  <div class="page-heading">
    <div><div class="eyebrow">CENTRO DE CONTROL</div><h1>Resumen general</h1><p>Visión operativa de tu red energética en un solo lugar.</p></div>
    <div class="heading-date"><span class="live-pulse"></span> Datos en tiempo real <small>{{ now | date:'dd MMM yyyy · HH:mm' }}</small></div>
  </div>

  @if (error()) { <div class="notice notice-warning">{{ error() }}</div> }
  <section class="welcome-strip">
    <div><span class="welcome-kicker">SMARTGRID / OPERACIÓN</span><h2>La energía fluye mejor cuando está conectada.</h2><p>Consulta el estado de la infraestructura, supervisa el consumo y toma decisiones con información centralizada.</p></div>
    <div class="welcome-symbol"><span>ϟ</span><i></i><i></i><i></i></div>
  </section>

  <section class="metric-grid">
    <article class="metric-card"><div class="metric-top"><span>Plantas registradas</span><span class="metric-icon green">ϟ</span></div><strong>{{ summary()?.totalPlantas ?? plants().length }}</strong><small>Infraestructura de generación</small><div class="metric-foot"><span class="mini-dot green-dot"></span>{{ summary()?.capacidadInstaladaKw ?? 0 | number:'1.0-1' }} kW instalados</div></article>
    <article class="metric-card"><div class="metric-top"><span>Capacidad disponible</span><span class="metric-icon blue">⌁</span></div><strong>{{ summary()?.capacidadDisponibleKw ?? 0 | number:'1.0-1' }} <small>kW</small></strong><small>Potencia habilitada</small><div class="metric-foot">Potencial operativo actual</div></article>
    <article class="metric-card"><div class="metric-top"><span>Zonas de red</span><span class="metric-icon violet">⌘</span></div><strong>{{ zones().length }}</strong><small>Segmentos de infraestructura</small><div class="metric-foot">Jerarquía de red registrada</div></article>
    <article class="metric-card"><div class="metric-top"><span>Alertas pendientes</span><span class="metric-icon amber">♧</span></div><strong>{{ pendingAlerts().length }}</strong><small>Requieren seguimiento</small><div class="metric-foot"><a routerLink="/alertas">Revisar alertas →</a></div></article>
  </section>

  <section class="dashboard-grid">
    <article class="panel generation-panel">
      <div class="panel-heading"><div><div class="eyebrow">MATRIZ ENERGÉTICA</div><h3>Capacidad por fuente</h3></div><a routerLink="/generacion" class="text-link">Ver generación →</a></div>
      @if (summary()?.porFuente?.length) {
        @for (source of summary()!.porFuente; track source.tipoFuente) {
          <div class="source-row"><div class="source-label"><span class="source-symbol" [class]="source.tipoFuente.toLowerCase()">{{ source.tipoFuente === 'SOLAR' ? '☼' : source.tipoFuente === 'EOLICA' ? '≋' : '◉' }}</span><div><b>{{ source.tipoFuente }}</b><small>{{ source.plantas }} planta(s)</small></div></div><div class="source-capacity"><b>{{ source.capacidadKw | number:'1.0-1' }} kW</b><div class="progress-track"><span [style.width.%]="sourcePercent(source.capacidadKw)"></span></div></div></div>
        }
      } @else { <div class="empty-state compact"><span>ϟ</span><b>Sin generación registrada</b><small>Las fuentes de energía aparecerán aquí.</small></div> }
      <div class="generation-total"><span>Producción estimada</span><b>{{ summary()?.produccionEstimadaKw ?? 0 | number:'1.0-2' }} kW</b></div>
    </article>
    <article class="panel status-panel">
      <div class="panel-heading"><div><div class="eyebrow">ESTADO OPERATIVO</div><h3>Disponibilidad de plantas</h3></div><span class="status-label"><i></i> Actualizado</span></div>
      @for (item of plantStates; track item.key) {
        <div class="state-row"><span class="state-bullet" [class]="item.css"></span><span>{{ item.label }}</span><b>{{ summary()?.plantasPorEstado?.[item.key] ?? 0 }}</b></div>
      }
      <div class="status-divider"></div>
      <div class="quick-links"><a routerLink="/red"><span>⌘</span><div><b>Administrar red</b><small>Zonas, clientes y medidores</small></div><i>→</i></a><a routerLink="/consumo"><span>▥</span><div><b>Consultar consumo</b><small>Lecturas y actividad energética</small></div><i>→</i></a></div>
    </article>
  </section>

  <section class="panel table-panel">
    <div class="panel-heading"><div><div class="eyebrow">INFRAESTRUCTURA</div><h3>Plantas de generación recientes</h3></div><a routerLink="/generacion" class="text-link">Administrar plantas →</a></div>
    @if (plants().length) {
      <div class="table-wrap"><table><thead><tr><th>PLANTA</th><th>FUENTE</th><th>CAPACIDAD</th><th>UBICACIÓN</th><th>ESTADO</th></tr></thead><tbody>
        @for (plant of plants().slice(0,5); track plant.id) { <tr><td><b>{{ plant.nombre }}</b><small class="cell-sub">ID #{{ plant.id }}</small></td><td><span class="source-pill">{{ plant.tipoFuente }}</span></td><td>{{ plant.capacidadKw | number:'1.0-2' }} kW</td><td>{{ plant.ubicacion }}</td><td><span class="badge-status" [class]="statusClass(plant.estado)">{{ plant.estado.replace('_',' ') }}</span></td></tr> }
      </tbody></table></div>
    } @else { <div class="empty-state"><span>ϟ</span><b>Aún no hay plantas registradas</b><small>Comienza agregando infraestructura de generación.</small><a routerLink="/generacion" class="btn btn-secondary">Registrar planta</a></div> }
  </section>
  `,
})
export class DashboardPage implements OnInit {
  now = new Date();
  summary = signal<GenerationSummary | null>(null);
  plants = signal<Plant[]>([]);
  zones = signal<Zone[]>([]);
  pendingAlerts = signal<AlertItem[]>([]);
  error = signal('');
  plantStates = [
    { key: 'ACTIVA', label: 'En operación', css: 'state-green' },
    { key: 'MANTENIMIENTO', label: 'En mantenimiento', css: 'state-amber' },
    { key: 'FUERA_DE_SERVICIO', label: 'Fuera de servicio', css: 'state-red' }
  ];
  constructor(private api: ApiService) {}
  ngOnInit(): void {
    this.api.generationSummary().subscribe({ next: v => this.summary.set(v), error: () => this.error.set('No fue posible cargar el resumen de generación.') });
    this.api.plants().subscribe({ next: v => this.plants.set(v), error: () => this.error.set('No fue posible cargar las plantas.') });
    this.api.zones().subscribe({ next: v => this.zones.set(v), error: () => undefined });
    this.api.alerts().subscribe({ next: v => this.pendingAlerts.set(v.filter(a => !a.atendida)), error: () => undefined });
  }
  sourcePercent(value: number): number { const total = this.summary()?.capacidadInstaladaKw || 1; return Math.min(100, value / total * 100); }
  statusClass(value: string): string { return value === 'ACTIVA' ? 'success' : value === 'MANTENIMIENTO' ? 'warning' : 'danger'; }
}
