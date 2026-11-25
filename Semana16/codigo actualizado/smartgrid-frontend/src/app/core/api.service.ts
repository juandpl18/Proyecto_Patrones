import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Plant {
  id: number; nombre: string; tipoFuente: string; capacidadKw: number; voltajeSalida: number;
  tieneBateriaReserva: boolean; ubicacion: string; estado: string; creadoPor?: string; creadoEn?: string;
}
export interface Zone { id: number; nombre: string; tipo: string; zonaPadreId: number | null; capacidadKw: number | null; }
export interface Client { id: number; documento: string; nombre: string; tipo: string; direccion: string; activo: boolean; }
export interface Meter { id: number; codigo: string; clienteId: number; clienteNombre: string; zonaId: number; zonaNombre: string; activo: boolean; }
export interface Reading { id: number; codigoMedidor: string; fechaHora: string; consumoKwh: number; }
export interface AlertItem { id: number; zonaId: number | null; zonaNombre: string | null; tipo: string; severidad: string; mensaje: string; creadaEn: string; atendida: boolean; }
export interface Invoice {
  id: number; clienteId: number; clienteNombre: string; tarifaId: number; tarifaNombre: string; modalidad: string;
  modo: string; periodoInicio: string; periodoFin: string; consumoKwh: number; precioPromedioKwh: number;
  energia: number; total: number; estado: string; fechaVencimiento: string; emitidaEn: string;
  pagadaEn?: string; anuladaEn?: string; motivoAnulacion?: string; lineas: { concepto: string; valor: number }[];
}
export interface Tariff {
  id: number; tipoCliente: string; nombre: string; modalidad: string; precioKwh: number | null;
  vigenteDesde: string; vigenteHasta?: string; activa: boolean;
  tramos: { desdeKwh: number; hastaKwh: number | null; precioKwh: number }[];
  franjas: { nombre: string; horaInicio: number; horaFin: number; precioKwh: number }[];
}
export interface UserItem { id: number; nombre: string; username: string; rol: string; activo: boolean; }
export interface GenerationSummary {
  totalPlantas: number; capacidadInstaladaKw: number; capacidadDisponibleKw: number; produccionEstimadaKw: number;
  plantasPorEstado: Record<string, number>; porFuente: { tipoFuente: string; plantas: number; capacidadKw: number; produccionEstimadaKw: number }[];
}
export interface BalanceResult {
  zonaId: number; zonaNombre: string; desde: string; hasta: string; totalDemandaKw: number;
  totalGeneracionKw: number; balanceTotalKw: number; estado: string;
  zonas: { zonaId: number; nombre: string; tipo: string; demandaKw: number; generacionKw: number; transferenciasNetasKw: number; deslastreKw: number; balanceKw: number; estado: string }[];
}
export interface BalancePlan {
  estrategia: string; zonaId: number; zonaNombre: string; hasta: string; requiereAccion: boolean;
  transferencias: { origenId: number; origenNombre: string; destinoId: number; destinoNombre: string; kw: number }[];
  deslastres: { zonaId: number; zonaNombre: string; kw: number }[]; observaciones: string[];
}
export interface Execution { id: number; zonaId: number; zonaNombre: string; estrategia: string; estado: string; ejecutadaEn: string; acciones: any[]; }

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly base = environment.apiUrl;
  constructor(private http: HttpClient) {}
  private url(path: string): string { return `${this.base}${path}`; }
  private params(values: Record<string, string | number | boolean | null | undefined>): HttpParams {
    let params = new HttpParams();
    Object.entries(values).forEach(([key, value]) => { if (value !== null && value !== undefined && value !== '') params = params.set(key, String(value)); });
    return params;
  }

  // Panel y generación
  generationSummary(): Observable<GenerationSummary> { return this.http.get<GenerationSummary>(this.url('/api/generacion/resumen')); }
  plants(): Observable<Plant[]> { return this.http.get<Plant[]>(this.url('/api/planta')); }
  createPlant(data: Record<string, string | number | boolean>): Observable<Plant> { return this.http.post<Plant>(this.url('/api/planta/crear'), null, { params: this.params(data) }); }
  plantDetail(id: number): Observable<any> { return this.http.get(this.url(`/api/planta/${id}`)); }
  changePlantState(id: number, estado: string, motivo?: string): Observable<any> { return this.http.put(this.url(`/api/planta/${id}/estado`), null, { params: this.params({ estado, motivo }) }); }
  plantProduction(id: number, conditions: Record<string, number>): Observable<any> { return this.http.get(this.url(`/api/planta/${id}/produccion`), { params: this.params(conditions) }); }
  plantHistory(id: number): Observable<any[]> { return this.http.get<any[]>(this.url(`/api/planta/${id}/historial-estados`)); }
  assignPlantZone(id: number, zonaId: number): Observable<any> { return this.http.put(this.url(`/api/planta/${id}/zona`), null, { params: this.params({ zonaId }) }); }

  // Red eléctrica
  zones(): Observable<Zone[]> { return this.http.get<Zone[]>(this.url('/api/zonas')); }
  createZone(data: unknown): Observable<Zone> { return this.http.post<Zone>(this.url('/api/zonas'), data); }
  clients(): Observable<Client[]> { return this.http.get<Client[]>(this.url('/api/clientes')); }
  createClient(data: unknown): Observable<Client> { return this.http.post<Client>(this.url('/api/clientes'), data); }
  meters(): Observable<Meter[]> { return this.http.get<Meter[]>(this.url('/api/medidores')); }
  createMeter(data: unknown): Observable<Meter> { return this.http.post<Meter>(this.url('/api/medidores'), data); }

  // Consumo
  readings(meterId: number): Observable<Reading[]> { return this.http.get<Reading[]>(this.url('/api/lecturas'), { params: this.params({ medidorId: meterId }) }); }
  createReading(data: unknown): Observable<Reading> { return this.http.post<Reading>(this.url('/api/lecturas'), data); }
  meterConsumption(id: number): Observable<any> { return this.http.get(this.url(`/api/medidores/${id}/consumo`)); }
  clientConsumption(id: number): Observable<any> { return this.http.get(this.url(`/api/clientes/${id}/consumo`)); }
  networkTree(): Observable<any[]> { return this.http.get<any[]>(this.url('/api/zonas/arbol')); }

  // Balanceo
  balance(zonaId: number): Observable<BalanceResult> { return this.http.get<BalanceResult>(this.url('/api/balance'), { params: this.params({ zonaId }) }); }
  strategies(): Observable<{ nombre: string; descripcion: string }[]> { return this.http.get<any[]>(this.url('/api/balance/estrategias')); }
  balancePlan(zonaId: number, estrategia: string): Observable<BalancePlan> { return this.http.get<BalancePlan>(this.url('/api/balance/plan'), { params: this.params({ zonaId, estrategia }) }); }
  executeBalance(zonaId: number, estrategia: string): Observable<Execution> { return this.http.post<Execution>(this.url('/api/balance/ejecutar'), null, { params: this.params({ zonaId, estrategia }) }); }
  executions(zonaId?: number): Observable<Execution[]> { return this.http.get<Execution[]>(this.url('/api/balance/ejecuciones'), { params: this.params({ zonaId }) }); }
  revertExecution(id: number): Observable<Execution> { return this.http.post<Execution>(this.url(`/api/balance/ejecuciones/${id}/revertir`), null); }

  // Facturación
  invoices(): Observable<Invoice[]> { return this.http.get<Invoice[]>(this.url('/api/facturas')); }
  simulateInvoice(clienteId: number, desde: string, hasta: string, modo: string): Observable<Invoice> { return this.http.get<Invoice>(this.url('/api/facturas/simular'), { params: this.params({ clienteId, desde, hasta, modo }) }); }
  issueInvoice(clienteId: number, desde: string, hasta: string, modo: string): Observable<Invoice> { return this.http.post<Invoice>(this.url('/api/facturas/emitir'), null, { params: this.params({ clienteId, desde, hasta, modo }) }); }
  payInvoice(id: number): Observable<Invoice> { return this.http.put<Invoice>(this.url(`/api/facturas/${id}/pagar`), null); }
  cancelInvoice(id: number, motivo: string): Observable<Invoice> { return this.http.put<Invoice>(this.url(`/api/facturas/${id}/anular`), null, { params: this.params({ motivo }) }); }
  tariffs(): Observable<Tariff[]> { return this.http.get<Tariff[]>(this.url('/api/tarifas')); }
  createTariff(data: unknown): Observable<Tariff> { return this.http.post<Tariff>(this.url('/api/tarifas'), data); }
  charges(): Observable<any[]> { return this.http.get<any[]>(this.url('/api/cargos')); }
  createCharge(data: unknown): Observable<any> { return this.http.post(this.url('/api/cargos'), data); }
  toggleCharge(id: number, activo: boolean): Observable<any> { return this.http.put(this.url(`/api/cargos/${id}/estado`), null, { params: this.params({ activo }) }); }

  // Administración y alertas
  alerts(): Observable<AlertItem[]> { return this.http.get<AlertItem[]>(this.url('/api/alertas')); }
  attendAlert(id: number): Observable<AlertItem> { return this.http.put<AlertItem>(this.url(`/api/alertas/${id}/atender`), null); }
  users(): Observable<UserItem[]> { return this.http.get<UserItem[]>(this.url('/api/usuarios')); }
  createUser(data: unknown): Observable<UserItem> { return this.http.post<UserItem>(this.url('/api/usuarios'), data); }
  toggleUser(id: number, activo: boolean): Observable<UserItem> { return this.http.put<UserItem>(this.url(`/api/usuarios/${id}/estado`), null, { params: this.params({ activo }) }); }
}
