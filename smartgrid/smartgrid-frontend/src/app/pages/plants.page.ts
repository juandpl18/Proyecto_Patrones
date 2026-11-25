import { Component, OnInit, signal } from '@angular/core';
import { DecimalPipe, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService, Plant, Zone } from '../core/api.service';
import { AuthService } from '../core/auth.service';

@Component({
  standalone: true,
  imports: [FormsModule, DecimalPipe, DatePipe],
  template: `
  <div class="page-heading"><div><div class="eyebrow">INFRAESTRUCTURA ENERGÉTICA</div><h1>Generación</h1><p>Administra las plantas, sus fuentes renovables y su estado operativo.</p></div>@if(auth.hasRole('ADMIN')) { <button class="btn btn-primary" (click)="showForm.set(!showForm())">＋ Registrar planta</button> }</div>
  @if (message()) { <div class="notice notice-success">{{ message() }}</div> } @if (error()) { <div class="notice notice-error">{{ error() }}</div> }
  @if (showForm() && auth.hasRole('ADMIN')) {
    <section class="panel form-panel"><div class="panel-heading"><div><div class="eyebrow">NUEVA INFRAESTRUCTURA</div><h3>Registrar planta de generación</h3></div><button class="icon-button" (click)="showForm.set(false)">×</button></div>
      <form class="form-grid" (ngSubmit)="createPlant()">
        <div class="field"><label>Nombre de la planta *</label><input name="nombre" [(ngModel)]="form.nombre" required placeholder="Ej. Planta Solar Norte"></div>
        <div class="field"><label>Fuente de energía *</label><select name="fuente" [(ngModel)]="form.fuente"><option value="SOLAR">Solar</option><option value="EOLICA">Eólica</option><option value="HIDRAULICA">Hidráulica</option></select></div>
        <div class="field"><label>Capacidad instalada (kW) *</label><input type="number" min="0.1" step="0.1" name="capacidad" [(ngModel)]="form.capacidad" required></div>
        <div class="field"><label>Ubicación *</label><input name="ubicacion" [(ngModel)]="form.ubicacion" required placeholder="Ciudad o sector"></div>
        <label class="check-field"><input type="checkbox" name="bateria" [(ngModel)]="form.bateria"> Cuenta con batería de reserva</label>
        <div class="form-actions"><button type="button" class="btn btn-ghost" (click)="showForm.set(false)">Cancelar</button><button class="btn btn-primary" [disabled]="saving()">{{ saving() ? 'Guardando…' : 'Guardar planta' }}</button></div>
      </form>
      <p class="form-hint">La planta se registra mediante el flujo de validación y construcción configurado en el backend.</p>
    </section>
  }
  <section class="metric-grid compact-metrics">
    <article class="metric-card"><div class="metric-top"><span>Total de plantas</span><span class="metric-icon green">ϟ</span></div><strong>{{ plants().length }}</strong><small>Registros de generación</small></article>
    <article class="metric-card"><div class="metric-top"><span>Capacidad instalada</span><span class="metric-icon blue">⌁</span></div><strong>{{ totalCapacity() | number:'1.0-1' }} <small>kW</small></strong><small>Suma de capacidades</small></article>
    <article class="metric-card"><div class="metric-top"><span>En operación</span><span class="metric-icon green">✓</span></div><strong>{{ countStatus('ACTIVA') }}</strong><small>Plantas activas</small></article>
    <article class="metric-card"><div class="metric-top"><span>Con reserva</span><span class="metric-icon violet">▣</span></div><strong>{{ batteryCount() }}</strong><small>Con batería de respaldo</small></article>
  </section>
  <section class="panel table-panel"><div class="panel-heading"><div><div class="eyebrow">REGISTRO CENTRAL</div><h3>Plantas de generación</h3></div><div class="table-tools"><input class="search-input" placeholder="Buscar planta…" [(ngModel)]="search"><select [(ngModel)]="statusFilter"><option value="">Todos los estados</option><option>ACTIVA</option><option>MANTENIMIENTO</option><option>FUERA_DE_SERVICIO</option></select></div></div>
    @if (loading()) { <div class="loading-state">Cargando infraestructura…</div> }
    @else if (filtered().length) { <div class="table-wrap"><table><thead><tr><th>PLANTA</th><th>FUENTE</th><th>CAPACIDAD</th><th>UBICACIÓN</th><th>ESTADO</th><th>ACCIONES</th></tr></thead><tbody>
      @for (p of filtered(); track p.id) { <tr><td><b>{{ p.nombre }}</b><small class="cell-sub">#{{ p.id }} · {{ p.tieneBateriaReserva ? 'Con reserva' : 'Sin reserva' }}</small></td><td><span class="source-pill">{{ p.tipoFuente }}</span></td><td>{{ p.capacidadKw | number:'1.0-2' }} kW</td><td>{{ p.ubicacion }}</td><td><span class="badge-status" [class]="statusClass(p.estado)">{{ p.estado.replace('_',' ') }}</span></td><td><button class="table-action" (click)="inspect(p)">Detalle</button>@if(auth.hasRole('ADMIN')) { <button class="table-action" (click)="selectState(p)">Estado</button> }</td></tr> }
    </tbody></table></div> } @else { <div class="empty-state"><span>ϟ</span><b>No se encontraron plantas</b><small>Registra una planta o cambia los filtros.</small></div> }
  </section>
  @if (selected()) {
    <div class="modal-backdrop" (click)="selected.set(null)"><section class="modal-card" (click)="$event.stopPropagation()"><div class="panel-heading"><div><div class="eyebrow">FICHA TÉCNICA</div><h3>{{ selected()!.nombre }}</h3></div><button class="icon-button" (click)="selected.set(null)">×</button></div>
      <div class="detail-grid"><div><small>Fuente</small><b>{{ selected()!.tipoFuente }}</b></div><div><small>Capacidad</small><b>{{ selected()!.capacidadKw }} kW</b></div><div><small>Voltaje</small><b>{{ selected()!.voltajeSalida }} V</b></div><div><small>Estado</small><b>{{ selected()!.estado }}</b></div><div><small>Ubicación</small><b>{{ selected()!.ubicacion }}</b></div><div><small>Batería</small><b>{{ selected()!.tieneBateriaReserva ? 'Sí' : 'No' }}</b></div></div>
      @if(auth.hasRole('ADMIN')) { <div class="field"><label>Asignar a una zona</label><div class="inline-fields"><select [(ngModel)]="selectedZoneId"><option [ngValue]="null">Selecciona una zona</option>@for(z of zones();track z.id){<option [ngValue]="z.id">{{z.nombre}}</option>}</select><button class="btn btn-secondary" (click)="assignZone()" [disabled]="!selectedZoneId">Asignar</button></div></div> }
      <div class="field"><label>Estimar producción</label><div class="inline-fields"><input type="number" placeholder="Irradiancia W/m²" [(ngModel)]="irradiance"><input type="number" placeholder="Viento m/s" [(ngModel)]="wind"><button class="btn btn-secondary" (click)="estimate()">Calcular</button></div></div>
      <div class="form-actions"><button class="btn btn-ghost" (click)="loadHistory()">Ver historial de estados</button></div>
      @if(history().length){<div class="history-list"><b>Historial de estados</b>@for(h of history();track h.id){<div><span>{{h.estadoAnterior}} → {{h.estadoNuevo}}</span><small>{{h.fechaHora | date:'dd/MM/yyyy HH:mm'}} · {{h.usuario}} {{h.motivo ? '· '+h.motivo : ''}}</small></div>}</div>}
      @if (production()) { <div class="production-result"><span>Producción estimada</span><b>{{ production()!.produccionKw }} kW</b><small>{{ production()!.metodo }}</small></div> }
    </section></div>
  }
  @if (stateTarget()) {
    <div class="modal-backdrop" (click)="stateTarget.set(null)"><section class="modal-card" (click)="$event.stopPropagation()"><div class="panel-heading"><h3>Cambiar estado operativo</h3><button class="icon-button" (click)="stateTarget.set(null)">×</button></div><div class="field"><label>Nuevo estado</label><select [(ngModel)]="newState"><option value="ACTIVA">Activa</option><option value="MANTENIMIENTO">Mantenimiento</option><option value="FUERA_DE_SERVICIO">Fuera de servicio</option></select></div><div class="field"><label>Motivo (opcional)</label><input [(ngModel)]="reason" placeholder="Motivo del cambio"></div><div class="form-actions"><button class="btn btn-ghost" (click)="stateTarget.set(null)">Cancelar</button><button class="btn btn-primary" (click)="saveState()">Actualizar estado</button></div></section></div>
  }
  `,
})
export class PlantsPage implements OnInit {
  plants = signal<Plant[]>([]); zones = signal<Zone[]>([]);
  showForm = signal(false); loading = signal(true); saving = signal(false);
  message = signal(''); error = signal(''); selected = signal<Plant | null>(null); stateTarget = signal<Plant | null>(null);
  production = signal<any | null>(null); history = signal<any[]>([]); selectedZoneId: number | null = null; irradiance = 800; wind = 8; newState = 'ACTIVA'; reason = '';
  search = ''; statusFilter = '';
  form = { nombre: '', fuente: 'SOLAR', capacidad: 500, bateria: false, ubicacion: '' };
  constructor(private api: ApiService, public auth: AuthService) {}
  ngOnInit(): void { this.load(); this.api.zones().subscribe({ next: v => this.zones.set(v), error: () => undefined }); }
  load(): void { this.loading.set(true); this.api.plants().subscribe({ next: v => { this.plants.set(v); this.loading.set(false); }, error: e => { this.error.set(e.error?.mensaje || 'No se pudieron cargar las plantas.'); this.loading.set(false); } }); }
  filtered(): Plant[] { const q = this.search.toLowerCase(); return this.plants().filter(p => (!q || `${p.nombre} ${p.tipoFuente} ${p.ubicacion}`.toLowerCase().includes(q)) && (!this.statusFilter || p.estado === this.statusFilter)); }
  totalCapacity(): number { return this.plants().reduce((sum,p) => sum + Number(p.capacidadKw || 0), 0); }
  countStatus(status: string): number { return this.plants().filter(p => p.estado === status).length; }
  batteryCount(): number { return this.plants().filter(p => p.tieneBateriaReserva).length; }
  statusClass(s: string): string { return s === 'ACTIVA' ? 'success' : s === 'MANTENIMIENTO' ? 'warning' : 'danger'; }
  createPlant(): void {
    this.error.set(''); this.message.set('');
    if (!this.form.nombre.trim() || !this.form.ubicacion.trim() || this.form.capacidad <= 0) { this.error.set('Completa los campos obligatorios con valores válidos.'); return; }
    this.saving.set(true);
    this.api.createPlant({ nombre: this.form.nombre, fuente: this.form.fuente, capacidad: this.form.capacidad, bateria: this.form.bateria, ubicacion: this.form.ubicacion }).subscribe({
      next: () => { this.saving.set(false); this.showForm.set(false); this.message.set('Planta registrada correctamente.'); this.form = { nombre: '', fuente: 'SOLAR', capacidad: 500, bateria: false, ubicacion: '' }; this.load(); },
      error: e => { this.saving.set(false); this.error.set(e.error?.mensaje || e.error?.message || 'No fue posible registrar la planta.'); }
    });
  }
  inspect(p: Plant): void { this.selected.set(p); this.production.set(null); this.history.set([]); this.selectedZoneId = null; }
  loadHistory(): void { const p=this.selected(); if(!p)return; this.api.plantHistory(p.id).subscribe({next:v=>this.history.set(v),error:e=>this.error.set(e.error?.mensaje||'No se pudo consultar el historial.')}); }
  assignZone(): void { const p=this.selected(); if(!p||!this.selectedZoneId)return; this.api.assignPlantZone(p.id,this.selectedZoneId).subscribe({next:()=>{this.message.set('Planta asignada a la zona seleccionada.');this.selected.set(null);},error:e=>this.error.set(e.error?.mensaje||'No se pudo asignar la zona.')}); }
  estimate(): void { const p = this.selected(); if (!p) return; this.api.plantProduction(p.id, { irradianciaWm2: this.irradiance, vientoMs: this.wind }).subscribe({ next: v => this.production.set(v), error: e => this.error.set(e.error?.mensaje || 'No se pudo estimar la producción.') }); }
  selectState(p: Plant): void { this.stateTarget.set(p); this.newState = p.estado; this.reason = ''; }
  saveState(): void { const p = this.stateTarget(); if (!p) return; this.api.changePlantState(p.id, this.newState, this.reason).subscribe({ next: () => { this.stateTarget.set(null); this.message.set('Estado de la planta actualizado.'); this.load(); }, error: e => this.error.set(e.error?.mensaje || 'No fue posible cambiar el estado.') }); }
}
