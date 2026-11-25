import { Component, OnInit, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ApiService, AlertItem } from '../core/api.service';

@Component({
  standalone: true,
  imports: [DatePipe],
  template: `
  <div class="page-heading"><div><div class="eyebrow">SUPERVISIÓN DEL SISTEMA</div><h1>Centro de alertas</h1><p>Monitorea eventos generados por la red y registra su atención.</p></div><button class="btn btn-secondary" (click)="load()">↻ Actualizar</button></div>
  @if(message()){<div class="notice notice-success">{{message()}}</div>} @if(error()){<div class="notice notice-error">{{error()}}</div>}
  <section class="metric-grid compact-metrics"><article class="metric-card"><div class="metric-top"><span>Alertas pendientes</span><span class="metric-icon blue">♧</span></div><strong>{{alerts().length}}</strong><small>Eventos registrados</small></article><article class="metric-card"><div class="metric-top"><span>Severidad crítica</span><span class="metric-icon amber">◷</span></div><strong>{{critical().length}}</strong><small>Prioridad de atención</small></article><article class="metric-card"><div class="metric-top"><span>Advertencias</span><span class="metric-icon red">!</span></div><strong>{{warnings().length}}</strong><small>Severidad advertencia</small></article></section>
  <section class="panel"><div class="panel-heading"><div><div class="eyebrow">EVENTOS DEL SISTEMA</div><h3>Alertas que requieren atención</h3></div><span class="badge-status warning">Solo pendientes</span></div>
    @if(alerts().length){<div class="alert-list">@for(a of alerts();track a.id){<article class="alert-item" [class.attended]="a.atendida"><div class="alert-severity" [class]="severityClass(a.severidad)">{{a.severidad==='CRITICA'?'!':a.severidad==='ADVERTENCIA'?'↑':'i'}}</div><div class="alert-content"><div class="alert-title"><b>{{a.tipo.replace('_',' ')}}</b><span class="badge-status" [class]="severityClass(a.severidad)">{{a.severidad}}</span></div><p>{{a.mensaje}}</p><small>{{a.zonaNombre || 'Sistema general'}} · {{a.creadaEn | date:'dd/MM/yyyy HH:mm'}}</small></div>@if(!a.atendida){<button class="btn btn-secondary btn-small" (click)="attend(a)">Marcar atendida</button>}@else{<span class="attended-label">✓ Atendida</span>}</article>}</div>}@else{<div class="empty-state"><span>✓</span><b>No hay alertas pendientes</b><small>Cuando el sistema genere alertas, aparecerán aquí.</small></div>}
  </section>
  `,
})
export class AlertsPage implements OnInit {
  alerts=signal<AlertItem[]>([]);message=signal('');error=signal('');
  constructor(private api:ApiService){}
  ngOnInit():void{this.load();}
  load():void{this.api.alerts().subscribe({next:v=>this.alerts.set(v),error:e=>this.error.set(e.error?.mensaje||'No se pudieron cargar las alertas.')});}
  pending():AlertItem[]{return this.alerts().filter(a=>!a.atendida);}
  critical():AlertItem[]{return this.alerts().filter(a=>a.severidad==='CRITICA'&&!a.atendida);}
  warnings():AlertItem[]{return this.alerts().filter(a=>a.severidad==='ADVERTENCIA'&&!a.atendida);}
  severityClass(s:string):string{return s==='CRITICA'?'danger':s==='ADVERTENCIA'?'warning':'info';}
  attend(a:AlertItem):void{this.api.attendAlert(a.id).subscribe({next:()=>{this.message.set('Alerta marcada como atendida.');this.load();},error:e=>this.error.set(e.error?.mensaje||'No se pudo atender la alerta.')});}
}
