# SmartGrid Frontend

Interfaz web de SmartGrid para la gestión de redes eléctricas inteligentes. Está construida con Angular standalone components y consume la API REST del backend Spring Boot.

## Módulos disponibles

| Módulo | Funcionalidades conectadas | Endpoints principales |
|---|---|---|
| Resumen general | Indicadores de generación, capacidad, zonas, alertas y plantas recientes | `/api/generacion/resumen`, `/api/planta`, `/api/zonas`, `/api/alertas` |
| Generación | Registro de plantas, consulta, detalle técnico, estimación de producción y cambio de estado | `/api/planta`, `/api/planta/crear`, `/api/planta/{id}`, `/api/planta/{id}/produccion`, `/api/planta/{id}/estado` |
| Red eléctrica | Zonas, clientes y medidores | `/api/zonas`, `/api/clientes`, `/api/medidores` |
| Consumo | Registro y consulta de lecturas, resumen de consumo por medidor y visualización de lecturas | `/api/lecturas`, `/api/medidores/{id}/consumo` |
| Balanceo de cargas | Diagnóstico, simulación, ejecución, historial y reversión | `/api/balance`, `/api/balance/plan`, `/api/balance/ejecutar`, `/api/balance/ejecuciones` |
| Facturación | Simulación y emisión de facturas, pago, tarifas planas/escalonadas/horarias y cargos adicionales | `/api/facturas`, `/api/tarifas`, `/api/cargos` |
| Alertas | Consulta de alertas pendientes y atención | `/api/alertas` |
| Usuarios | Creación, consulta, roles y activación/desactivación | `/api/usuarios` |

## Requisitos

- Node.js 20.19+ o 22.12+.
- npm 10 o compatible.
- Backend SmartGrid ejecutándose.
- PostgreSQL configurado según `application.properties` y las migraciones Flyway del backend.

## Instalación y ejecución

1. Abre una terminal dentro de `smartgrid-frontend`.
2. Instala las dependencias:

   ```bash
   npm install
   ```

3. Inicia Angular:

   ```bash
   npm start
   ```

4. Abre [http://localhost:4200](http://localhost:4200).

El backend debe estar disponible en `http://localhost:8080`. La URL se configura en:

```text
src/environments/environment.ts
```

Si el backend se ejecuta en otra dirección o puerto, modifica `apiUrl`.

## Autenticación y permisos

- El inicio de sesión utiliza `POST /api/auth/login`.
- El token JWT se conserva en `localStorage` durante su vigencia.
- Un interceptor adjunta `Authorization: Bearer <token>` a las solicitudes.
- Las rutas protegidas requieren sesión.
- El módulo de usuarios y las acciones administrativas se muestran únicamente al rol `ADMIN`.
- Las acciones operativas disponibles para `OPERADOR` dependen de las reglas de autorización del backend.
- El backend sigue siendo la autoridad final para validar permisos. Ocultar controles en Angular no sustituye la seguridad del servidor.

## Estructura del frontend

```text
src/
├── app/
│   ├── core/
│   │   ├── api.service.ts          # Contratos y llamadas REST
│   │   ├── auth.service.ts         # Sesión, JWT y roles
│   │   ├── auth.guard.ts           # Protección de rutas
│   │   └── auth.interceptor.ts     # Envío automático del token
│   ├── layout/
│   │   └── app-layout.component.ts # Menú lateral y encabezado
│   ├── pages/
│   │   ├── login.page.ts
│   │   ├── dashboard.page.ts
│   │   ├── plants.page.ts
│   │   ├── network.page.ts
│   │   ├── consumption.page.ts
│   │   ├── balance.page.ts
│   │   ├── billing.page.ts
│   │   ├── alerts.page.ts
│   │   └── users.page.ts
│   ├── app.component.ts
│   ├── app.config.ts
│   └── app.routes.ts
├── environments/
│   └── environment.ts
├── favicon.svg
├── index.html
└── styles.scss
```

## Consideraciones de integración

- El frontend se construyó a partir de los controladores, DTO, roles y rutas encontrados en el backend entregado.
- No crea tablas ni modifica migraciones. La persistencia pertenece al backend y a Flyway.
- El registro de plantas usa los parámetros que espera `POST /api/planta/crear`.
- La lista de alertas del backend devuelve alertas pendientes; por eso la interfaz no inventa un historial de alertas atendidas.
- Las tarifas escalonadas deben tener tramos continuos desde `0 kWh` y un último tramo abierto. Las tarifas horarias se configuran mediante franjas.
- El balanceo se muestra en dos etapas: simulación (consulta) y ejecución (operación persistida). La ejecución puede revertirse desde el historial.
- El proyecto original del backend no se modifica con este paquete.

## Compilación de producción

```bash
npm run build
```

Los archivos compilados se generan en `dist/`.
