import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService, Zone, Client, Meter } from '../core/api.service';
import { AuthService } from '../core/auth.service';

@Component({
  standalone: true,
  imports: [FormsModule],
  template: `
  <div class="page-heading"><div><div class="eyebrow">TOPOLOGÍA Y CONEXIONES</div><h1>Red eléctrica</h1><p>Organiza las zonas, los clientes conectados y sus medidores.</p></div></div>
  @if (message()) { <div class="notice notice-success">{{ message() }}</div> } @if (error()) { <div class="notice notice-error">{{ error() }}</div> }
  <div class="tabs"><button [class.active]="tab()==='zones'" (click)="tab.set('zones')">Zonas de red <span>{{ zones().length }}</span></button><button [class.active]="tab()==='clients'" (click)="tab.set('clients')">Clientes <span>{{ clients().length }}</span></button><button [class.active]="tab()==='meters'" (click)="tab.set('meters')">Medidores <span>{{ meters().length }}</span></button></div>
  @if (tab()==='zones') {
    <section class="panel"><div class="panel-heading"><div><div class="eyebrow">ESTRUCTURA JERÁRQUICA</div><h3>Zonas y sectores</h3></div>@if(auth.hasRole('ADMIN')) { <button class="btn btn-primary" (click)="showZoneForm.set(!showZoneForm())">＋ Nueva zona</button> }</div>
      @if (showZoneForm() && auth.hasRole('ADMIN')) { <form class="form-grid form-grid-compact" (ngSubmit)="createZone()"><div class="field"><label>Nombre *</label><input name="znombre" [(ngModel)]="zoneForm.nombre" required></div><div class="field"><label>Tipo</label><select name="ztipo" [(ngModel)]="zoneForm.tipo"><option>SUBESTACION</option><option>ZONA</option><option>SECTOR</option></select></div><div class="field"><label>Zona superior</label><select name="zpadre" [(ngModel)]="zoneForm.zonaPadreId"><option [ngValue]="null">Sin zona superior</option>@for(z of zones();track z.id){<option [ngValue]="z.id">{{z.nombre}}</option>}</select></div><div class="field"><label>Capacidad (kW)</label><input type="number" name="zcap" [(ngModel)]="zoneForm.capacidadKw"></div><div class="form-actions"><button type="button" class="btn btn-ghost" (click)="showZoneForm.set(false)">Cancelar</button><button class="btn btn-primary">Guardar zona</button></div></form> }
      @if (zones().length) { <div class="table-wrap"><table><thead><tr><th>NOMBRE</th><th>TIPO</th><th>ZONA SUPERIOR</th><th>CAPACIDAD</th></tr></thead><tbody>@for(z of zones();track z.id){<tr><td><b>{{z.nombre}}</b><small class="cell-sub">#{{z.id}}</small></td><td><span class="source-pill">{{z.tipo}}</span></td><td>{{parentName(z.zonaPadreId)}}</td><td>{{z.capacidadKw ?? '—'}} {{z.capacidadKw ? 'kW' : ''}}</td></tr>}</tbody></table></div> } @else { <div class="empty-state"><span>⌘</span><b>No hay zonas creadas</b><small>Define la jerarquía de la red para empezar.</small></div> }
    </section>
  }
  @if (tab()==='clients') {
    <section class="panel"><div class="panel-heading"><div><div class="eyebrow">USUARIOS DEL SERVICIO</div><h3>Clientes conectados</h3></div>@if(auth.hasRole('ADMIN')) { <button class="btn btn-primary" (click)="showClientForm.set(!showClientForm())">＋ Nuevo cliente</button> }</div>
      @if (showClientForm() && auth.hasRole('ADMIN')) { <form class="form-grid form-grid-compact" (ngSubmit)="createClient()"><div class="field"><label>Documento *</label><input name="doc" [(ngModel)]="clientForm.documento" required></div><div class="field"><label>Nombre *</label><input name="cnombre" [(ngModel)]="clientForm.nombre" required></div><div class="field"><label>Tipo</label><select name="ctipo" [(ngModel)]="clientForm.tipo"><option>RESIDENCIAL</option><option>COMERCIAL</option></select></div><div class="field"><label>Dirección</label><input name="direccion" [(ngModel)]="clientForm.direccion"></div><div class="form-actions"><button type="button" class="btn btn-ghost" (click)="showClientForm.set(false)">Cancelar</button><button class="btn btn-primary">Guardar cliente</button></div></form> }
      @if (clients().length) { <div class="table-wrap"><table><thead><tr><th>CLIENTE</th><th>DOCUMENTO</th><th>TIPO</th><th>DIRECCIÓN</th><th>ESTADO</th></tr></thead><tbody>@for(c of clients();track c.id){<tr><td><b>{{c.nombre}}</b><small class="cell-sub">#{{c.id}}</small></td><td>{{c.documento}}</td><td><span class="source-pill">{{c.tipo}}</span></td><td>{{c.direccion || '—'}}</td><td><span class="badge-status" [class]="c.activo?'success':'danger'">{{c.activo?'ACTIVO':'INACTIVO'}}</span></td></tr>}</tbody></table></div> } @else { <div class="empty-state"><span>♙</span><b>No hay clientes registrados</b><small>Registra usuarios del servicio eléctrico.</small></div> }
    </section>
  }
  @if (tab()==='meters') {
    <section class="panel"><div class="panel-heading"><div><div class="eyebrow">MEDICIÓN INTELIGENTE</div><h3>Medidores</h3></div>@if(auth.hasRole('ADMIN')) { <button class="btn btn-primary" (click)="showMeterForm.set(!showMeterForm())">＋ Nuevo medidor</button> }</div>
      @if (showMeterForm() && auth.hasRole('ADMIN')) { <form class="form-grid form-grid-compact" (ngSubmit)="createMeter()"><div class="field"><label>Código *</label><input name="mcodigo" [(ngModel)]="meterForm.codigo" required placeholder="MED-0001"></div><div class="field"><label>Cliente *</label><select name="mcliente" [(ngModel)]="meterForm.clienteId" required><option [ngValue]="null">Selecciona un cliente</option>@for(c of clients();track c.id){<option [ngValue]="c.id">{{c.nombre}}</option>}</select></div><div class="field"><label>Zona *</label><select name="mzona" [(ngModel)]="meterForm.zonaId" required><option [ngValue]="null">Selecciona una zona</option>@for(z of zones();track z.id){<option [ngValue]="z.id">{{z.nombre}}</option>}</select></div><div class="form-actions"><button type="button" class="btn btn-ghost" (click)="showMeterForm.set(false)">Cancelar</button><button class="btn btn-primary">Guardar medidor</button></div></form> }
      @if (meters().length) { <div class="table-wrap"><table><thead><tr><th>CÓDIGO</th><th>CLIENTE</th><th>ZONA</th><th>ESTADO</th></tr></thead><tbody>@for(m of meters();track m.id){<tr><td><b>{{m.codigo}}</b><small class="cell-sub">#{{m.id}}</small></td><td>{{m.clienteNombre}}</td><td>{{m.zonaNombre}}</td><td><span class="badge-status" [class]="m.activo?'success':'danger'">{{m.activo?'ACTIVO':'INACTIVO'}}</span></td></tr>}</tbody></table></div> } @else { <div class="empty-state"><span>▥</span><b>No hay medidores registrados</b><small>Asocia un medidor a un cliente y una zona.</small></div> }
    </section>
  }
  `,
})
export class NetworkPage implements OnInit {
  tab = signal<'zones'|'clients'|'meters'>('zones');
  zones = signal<Zone[]>([]); clients = signal<Client[]>([]); meters = signal<Meter[]>([]);
  showZoneForm = signal(false); showClientForm = signal(false); showMeterForm = signal(false);
  message = signal(''); error = signal('');
  zoneForm: any = { nombre: '', tipo: 'ZONA', zonaPadreId: null, capacidadKw: null };
  clientForm = { documento: '', nombre: '', tipo: 'RESIDENCIAL', direccion: '' };
  meterForm: any = { codigo: '', clienteId: null, zonaId: null };
  constructor(private api: ApiService, public auth: AuthService) {}
  ngOnInit(): void { this.loadAll(); }
  loadAll(): void {
    this.api.zones().subscribe({ next: v => this.zones.set(v), error: () => undefined });
    this.api.clients().subscribe({ next: v => this.clients.set(v), error: () => undefined });
    this.api.meters().subscribe({ next: v => this.meters.set(v), error: () => undefined });
  }
  parentName(id: number | null): string { return id ? this.zones().find(z => z.id === id)?.nombre ?? `Zona #${id}` : 'Nivel principal'; }
  private done(text: string): void { this.message.set(text); this.error.set(''); this.loadAll(); }
  createZone(): void { this.api.createZone(this.zoneForm).subscribe({ next: () => { this.showZoneForm.set(false); this.done('Zona creada correctamente.'); this.zoneForm = { nombre:'', tipo:'ZONA', zonaPadreId:null, capacidadKw:null }; }, error: e => this.error.set(e.error?.mensaje || 'No se pudo crear la zona.') }); }
  createClient(): void { this.api.createClient(this.clientForm).subscribe({ next: () => { this.showClientForm.set(false); this.done('Cliente creado correctamente.'); this.clientForm = { documento:'', nombre:'', tipo:'RESIDENCIAL', direccion:'' }; }, error: e => this.error.set(e.error?.mensaje || 'No se pudo crear el cliente.') }); }
  createMeter(): void {
    if (!this.meterForm.clienteId || !this.meterForm.zonaId) { this.error.set('Selecciona un cliente y una zona.'); return; }
    this.api.createMeter(this.meterForm).subscribe({ next: () => { this.showMeterForm.set(false); this.done('Medidor creado correctamente.'); this.meterForm = { codigo:'', clienteId:null, zonaId:null }; }, error: e => this.error.set(e.error?.mensaje || 'No se pudo crear el medidor.') });
  }
}
