import { Component, OnInit, signal } from '@angular/core';
import { DatePipe, DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService, Meter, Reading } from '../core/api.service';

@Component({
  standalone: true,
  imports: [FormsModule, DatePipe, DecimalPipe],
  template: `
  <div class="page-heading"><div><div class="eyebrow">MEDICIÓN Y DEMANDA</div><h1>Consumo energético</h1><p>Registra lecturas y consulta el comportamiento energético de los medidores.</p></div><button class="btn btn-primary" (click)="showForm.set(!showForm())">＋ Registrar lectura</button></div>
  @if(message()){<div class="notice notice-success">{{message()}}</div>} @if(error()){<div class="notice notice-error">{{error()}}</div>}
  <section class="metric-grid compact-metrics"><article class="metric-card"><div class="metric-top"><span>Medidores activos</span><span class="metric-icon blue">▥</span></div><strong>{{activeMetersCount()}}</strong><small>Equipos de medición</small></article><article class="metric-card"><div class="metric-top"><span>Lecturas consultadas</span><span class="metric-icon green">⌁</span></div><strong>{{readings().length}}</strong><small>Registros del medidor seleccionado</small></article><article class="metric-card"><div class="metric-top"><span>Consumo consultado</span><span class="metric-icon violet">ϟ</span></div><strong>{{consumptionSummary()?.consumoKwh ?? totalConsumption() | number:'1.0-3'}} <small>kWh</small></strong><small>Acumulado del período disponible</small></article></section>
  @if(showForm()){<section class="panel form-panel"><div class="panel-heading"><div><div class="eyebrow">NUEVA MEDICIÓN</div><h3>Registrar lectura de consumo</h3></div><button class="icon-button" (click)="showForm.set(false)">×</button></div><form class="form-grid" (ngSubmit)="register()"><div class="field"><label>Medidor *</label><select name="meter" [(ngModel)]="form.codigoMedidor" required><option value="">Selecciona un medidor</option>@for(m of meters();track m.id){<option [value]="m.codigo">{{m.codigo}} · {{m.clienteNombre}}</option>}</select></div><div class="field"><label>Fecha y hora *</label><input type="datetime-local" name="fecha" [(ngModel)]="form.fechaLocal" required></div><div class="field"><label>Consumo (kWh) *</label><input type="number" name="consumo" min="0" step="0.001" [(ngModel)]="form.consumoKwh" required></div><div class="form-actions"><button type="button" class="btn btn-ghost" (click)="showForm.set(false)">Cancelar</button><button class="btn btn-primary" [disabled]="saving()">{{saving()?'Guardando…':'Guardar lectura'}}</button></div></form></section>}
  <section class="panel table-panel"><div class="panel-heading"><div><div class="eyebrow">HISTORIAL DE MEDICIÓN</div><h3>Lecturas registradas</h3></div><div class="table-tools"><select [(ngModel)]="selectedMeter" (ngModelChange)="loadReadings()"><option [ngValue]="null">Selecciona un medidor</option>@for(m of meters();track m.id){<option [ngValue]="m.id">{{m.codigo}} · {{m.clienteNombre}}</option>}</select><button class="btn btn-secondary btn-small" (click)="loadReadings()" [disabled]="!selectedMeter">Actualizar</button></div></div>
    @if(!selectedMeter){<div class="empty-state"><span>▥</span><b>Selecciona un medidor</b><small>Elige un medidor para consultar sus lecturas de consumo.</small></div>}
    @else if(readings().length){<div class="consumption-chart"><div class="chart-caption"><b>Consumo por lectura</b><small>Últimos registros del medidor seleccionado</small></div><div class="bar-chart">@for(r of readings().slice(-8);track r.id){<div class="bar-column"><div class="bar-value">{{r.consumoKwh | number:'1.0-2'}}</div><div class="bar-track"><span [style.height.%]="barHeight(r.consumoKwh)"></span></div><small>{{r.fechaHora | date:'dd/MM'}}</small></div>}</div></div><div class="table-wrap"><table><thead><tr><th>FECHA Y HORA</th><th>MEDIDOR</th><th>CONSUMO</th></tr></thead><tbody>@for(r of readings();track r.id){<tr><td>{{r.fechaHora | date:'dd/MM/yyyy, HH:mm'}}</td><td><b>{{r.codigoMedidor}}</b></td><td><b>{{r.consumoKwh | number:'1.0-3'}} kWh</b></td></tr>}</tbody></table></div>}
    @else{<div class="empty-state"><span>⌁</span><b>No hay lecturas para este medidor</b><small>Registra una lectura para comenzar el historial.</small></div>}
  </section>
  `,
})
export class ConsumptionPage implements OnInit {
  meters = signal<Meter[]>([]); readings = signal<Reading[]>([]); consumptionSummary = signal<any|null>(null);
  showForm = signal(false); saving = signal(false); message = signal(''); error = signal('');
  selectedMeter: number | null = null;
  form: any = { codigoMedidor: '', fechaLocal: new Date(Date.now()-new Date().getTimezoneOffset()*60000).toISOString().slice(0,16), consumoKwh: 0 };
  constructor(private api: ApiService) {}
  ngOnInit(): void { this.api.meters().subscribe({ next: v => { this.meters.set(v); if(v.length){ this.selectedMeter=v[0].id; this.loadReadings(); } }, error: e => this.error.set(e.error?.mensaje || 'No se pudieron cargar los medidores.') }); }
  loadReadings(): void {
    if(!this.selectedMeter){this.readings.set([]);this.consumptionSummary.set(null);return;}
    this.api.readings(this.selectedMeter).subscribe({next:v=>this.readings.set(v),error:e=>this.error.set(e.error?.mensaje || 'No se pudieron consultar las lecturas.')});
    this.api.meterConsumption(this.selectedMeter).subscribe({next:v=>this.consumptionSummary.set(v),error:()=>this.consumptionSummary.set(null)});
  }
  totalConsumption(): number { return this.readings().reduce((sum,r)=>sum+Number(r.consumoKwh||0),0); }
  activeMetersCount(): number { return this.meters().filter(m=>m.activo).length; }
  barHeight(value: number): number { const max = Math.max(...this.readings().map(r => Number(r.consumoKwh || 0)), 1); return Math.max(5, value / max * 100); }
  register(): void {
    this.error.set(''); this.message.set('');
    if(!this.form.codigoMedidor || !this.form.fechaLocal || this.form.consumoKwh < 0){this.error.set('Completa los campos con valores válidos.');return;}
    this.saving.set(true);
    const payload={ codigoMedidor:this.form.codigoMedidor, fechaHora:new Date(this.form.fechaLocal).toISOString(), consumoKwh:this.form.consumoKwh };
    this.api.createReading(payload).subscribe({next:()=>{this.saving.set(false);this.showForm.set(false);this.message.set('Lectura registrada correctamente.');this.form.consumoKwh=0;this.loadReadings();},error:e=>{this.saving.set(false);this.error.set(e.error?.mensaje || 'No se pudo registrar la lectura.')}});
  }
}
