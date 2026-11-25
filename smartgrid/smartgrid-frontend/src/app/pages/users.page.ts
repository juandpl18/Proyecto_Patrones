import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService, UserItem } from '../core/api.service';

@Component({
  standalone: true,
  imports: [FormsModule],
  template: `
  <div class="page-heading"><div><div class="eyebrow">CONTROL DE ACCESO</div><h1>Usuarios del sistema</h1><p>Administra las cuentas, los roles y el acceso a SmartGrid.</p></div><button class="btn btn-primary" (click)="showForm.set(!showForm())">＋ Crear usuario</button></div>
  @if(message()){<div class="notice notice-success">{{message()}}</div>} @if(error()){<div class="notice notice-error">{{error()}}</div>}
  @if(showForm()){<section class="panel form-panel"><div class="panel-heading"><div><div class="eyebrow">NUEVA CUENTA</div><h3>Registrar usuario</h3></div><button class="icon-button" (click)="showForm.set(false)">×</button></div><form class="form-grid" (ngSubmit)="create()"><div class="field"><label>Nombre completo *</label><input name="nombre" [(ngModel)]="form.nombre" required></div><div class="field"><label>Usuario *</label><input name="username" [(ngModel)]="form.username" required></div><div class="field"><label>Contraseña *</label><input type="password" name="password" [(ngModel)]="form.password" required minlength="4"></div><div class="field"><label>Rol *</label><select name="rol" [(ngModel)]="form.rol"><option value="OPERADOR">Operador</option><option value="ADMIN">Administrador</option></select></div><div class="form-actions"><button type="button" class="btn btn-ghost" (click)="showForm.set(false)">Cancelar</button><button class="btn btn-primary" [disabled]="saving()">Crear cuenta</button></div></form><p class="form-hint">Las contraseñas se procesan en el backend. No se muestran en el listado.</p></section>}
  <section class="metric-grid compact-metrics"><article class="metric-card"><div class="metric-top"><span>Usuarios registrados</span><span class="metric-icon blue">♙</span></div><strong>{{users().length}}</strong><small>Cuentas del sistema</small></article><article class="metric-card"><div class="metric-top"><span>Administradores</span><span class="metric-icon violet">◆</span></div><strong>{{countRole('ADMIN')}}</strong><small>Control total de módulos</small></article><article class="metric-card"><div class="metric-top"><span>Operadores</span><span class="metric-icon green">⌁</span></div><strong>{{countRole('OPERADOR')}}</strong><small>Operación y supervisión</small></article></section>
  <section class="panel table-panel"><div class="panel-heading"><div><div class="eyebrow">DIRECTORIO DE ACCESO</div><h3>Usuarios registrados</h3></div><input class="search-input" placeholder="Buscar usuario…" [(ngModel)]="search"></div>
  @if(users().length){<div class="table-wrap"><table><thead><tr><th>USUARIO</th><th>NOMBRE</th><th>ROL</th><th>ESTADO</th><th>ACCIÓN</th></tr></thead><tbody>@for(u of filtered();track u.id){<tr><td><b>{{u.username}}</b><small class="cell-sub">ID #{{u.id}}</small></td><td>{{u.nombre}}</td><td><span class="source-pill">{{u.rol}}</span></td><td><span class="badge-status" [class]="u.activo?'success':'danger'">{{u.activo?'ACTIVO':'INACTIVO'}}</span></td><td><button class="table-action" (click)="toggle(u)">{{u.activo?'Desactivar':'Activar'}}</button></td></tr>}</tbody></table></div>}@else{<div class="empty-state"><span>♙</span><b>No hay usuarios disponibles</b><small>Los usuarios creados aparecerán en este directorio.</small></div>}</section>
  `,
})
export class UsersPage implements OnInit {
  users=signal<UserItem[]>([]);showForm=signal(false);saving=signal(false);message=signal('');error=signal('');search='';
  form={nombre:'',username:'',password:'',rol:'OPERADOR'};
  constructor(private api:ApiService){}
  ngOnInit():void{this.load();}
  load():void{this.api.users().subscribe({next:v=>this.users.set(v),error:e=>this.error.set(e.error?.mensaje||'No se pudieron cargar los usuarios.')});}
  filtered():UserItem[]{const q=this.search.toLowerCase();return this.users().filter(u=>`${u.nombre} ${u.username} ${u.rol}`.toLowerCase().includes(q));}
  countRole(role:string):number{return this.users().filter(u=>u.rol===role).length;}
  create():void{this.saving.set(true);this.api.createUser(this.form).subscribe({next:()=>{this.saving.set(false);this.showForm.set(false);this.message.set('Usuario creado correctamente.');this.form={nombre:'',username:'',password:'',rol:'OPERADOR'};this.load();},error:e=>{this.saving.set(false);this.error.set(e.error?.mensaje||'No se pudo crear el usuario.');}});}
  toggle(u:UserItem):void{this.api.toggleUser(u.id,!u.activo).subscribe({next:()=>{this.message.set('Estado del usuario actualizado.');this.load();},error:e=>this.error.set(e.error?.mensaje||'No se pudo cambiar el estado.')});}
}
