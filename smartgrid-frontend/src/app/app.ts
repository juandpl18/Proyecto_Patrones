import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Component, ChangeDetectorRef } from '@angular/core';

interface FacturaResultado {
  tipoFactura: string;
  consumo: number;
  tarifa: number;
  total: number;
}

interface PanelResumenResponse {
  estadoSistema: string;
  factura: FacturaResultado;
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  private readonly apiUrl = 'http://localhost:8080/api/facade';

  tipo = 'RESIDENCIAL';
  consumo = 350;

  datos: PanelResumenResponse | null = null;
  cargando = false;
  error = '';

  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef,
  ) {}

  consultar(): void {
    if (this.consumo < 0 || !Number.isFinite(this.consumo)) {
      this.error = 'Ingresa un consumo válido.';
      return;
    }

    this.cargando = true;
    this.error = '';

    this.http
      .get<PanelResumenResponse>(`${this.apiUrl}/panel-json`, {
        params: {
          tipo: this.tipo,
          consumo: this.consumo,
        },
      })
      .subscribe({
        next: (respuesta) => {
          console.log('Respuesta recibida:', respuesta);
          this.datos = respuesta;
          this.cargando = false;
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Error al consultar:', err);
          this.error =
            'No fue posible conectar con SmartGrid. Verifica que Spring Boot esté ejecutándose.';
          this.cargando = false;
          this.cdr.detectChanges();
        },
      });
  }
}
