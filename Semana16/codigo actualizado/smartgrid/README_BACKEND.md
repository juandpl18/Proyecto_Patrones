# SmartGrid — Backend

Backend de **SmartGrid**, un sistema de gestión de redes eléctricas inteligentes desarrollado con Java y Spring Boot. Expone una API REST para administrar usuarios, infraestructura de generación, red eléctrica, consumo, balanceo de cargas, facturación y alertas.

El proyecto aplica patrones de diseño para separar responsabilidades, facilitar el mantenimiento y permitir que la solución evolucione progresivamente.

---

## 1. Tecnologías

| Tecnología | Uso |
|---|---|
| Java 21 | Lenguaje y plataforma de ejecución |
| Spring Boot 4.1.1 | Aplicación y configuración principal |
| Spring Web MVC | Controladores y API REST |
| Spring Data JPA | Persistencia y acceso a datos |
| PostgreSQL | Base de datos relacional |
| Flyway | Versionado y migraciones del esquema |
| Spring Security | Protección de endpoints |
| JWT | Autenticación sin sesión de servidor |
| Maven | Dependencias, compilación y pruebas |
| Lombok | Reducción de código repetitivo en las clases que lo utilizan |

---

## 2. Estructura del proyecto

El código fuente principal está en:

```text
src/
├── main/
│   ├── java/com/smartgrid/smartgrid/
│   │   ├── adapter/       # Adaptación de la autenticación
│   │   ├── balanceo/      # Análisis y planificación del balanceo
│   │   ├── builder/       # Construcción de objetos de planta
│   │   ├── chain/         # Cadenas de validación y reglas de alivio
│   │   ├── command/       # Comandos para operaciones
│   │   ├── composite/     # Estructura jerárquica de la red
│   │   ├── config/        # Seguridad, JWT y configuración
│   │   ├── controller/    # Endpoints REST
│   │   ├── decorator/    # Extensión de comportamientos
│   │   ├── dto/           # Objetos de entrada y salida de la API
│   │   ├── exception/     # Manejo de excepciones
│   │   ├── facade/        # Acceso simplificado a operaciones del sistema
│   │   ├── factory/       # Creación de objetos mediante fábricas
│   │   ├── facturacion/   # Componentes del cálculo de facturas
│   │   ├── flyweight/     # Datos compartidos de fuentes de energía
│   │   ├── model/         # Entidades y enumeraciones del dominio
│   │   ├── observer/      # Notificación de eventos
│   │   ├── repository/    # Repositorios Spring Data JPA
│   │   ├── service/       # Reglas y casos de uso
│   │   ├── state/         # Estados y comportamiento de plantas
│   │   ├── strategy/      # Algoritmos intercambiables
│   │   └── template/      # Estructuras comunes de procesos
│   └── resources/
│       ├── application.properties
│       └── db/migration/  # Migraciones SQL de Flyway
└── test/
    └── java/com/smartgrid/smartgrid/ # Pruebas automatizadas
```

### Responsabilidad de las capas

- **Controller:** recibe solicitudes HTTP, obtiene los datos de entrada y devuelve respuestas.
- **DTO:** define los datos que la API recibe o entrega; evita exponer directamente las entidades cuando no corresponde.
- **Service:** concentra las reglas de negocio y coordina las operaciones.
- **Repository:** realiza operaciones de persistencia mediante Spring Data JPA.
- **Model:** contiene las entidades del dominio y enumeraciones.
- **Config:** configura seguridad, JWT, contraseñas y parámetros de la aplicación.
- **Migraciones:** definen la estructura y evolución de PostgreSQL mediante scripts versionados.

---

## 3. Módulos funcionales

### Autenticación y usuarios
Permite iniciar sesión, generar tokens JWT y administrar usuarios, roles y estado de las cuentas.

Clases principales: `AuthController`, `UsuarioController`, `AutenticacionService`, `TokenService` y `UsuarioService`.

### Red eléctrica
Administra la estructura jerárquica de zonas, los clientes y los medidores asociados.

Clases principales: `ZonaController`, `ClienteController`, `MedidorController` y sus respectivos servicios y repositorios.

### Consumo energético
Registra lecturas de consumo y permite consultar información de medidores y clientes.

Clases principales: `LecturaController`, `LecturaService`, `ConsumoService` y `LecturaConsumoRepository`.

### Generación eléctrica
Administra plantas de generación, sus fuentes, capacidades, estados, producción e historial de cambios.

Clases principales: `PlantaEnergiaController`, `PlantaOperacionController`, `GeneracionController`, `PlantaGeneracionService` y `PlantaOperacionService`.

### Balanceo de cargas
Analiza la demanda y generación por zona, prepara planes de balanceo, ejecuta acciones y permite consultar o revertir ejecuciones.

Clases principales: `BalanceController`, `BalanceoService`, `BalanceService` y los componentes del paquete `balanceo`.

### Tarifas y facturación
Administra tarifas, modalidades, tramos, franjas horarias y cargos adicionales. Permite simular, emitir, consultar, pagar y anular facturas, además de emitir lotes y marcar facturas vencidas.

Clases principales: `TarifaController`, `FacturaController`, `FacturacionController`, `CargoController`, `TarifaService`, `FacturaService` y `FacturacionService`.

### Alertas
Permite consultar alertas del sistema y registrar su atención.

Clase principal: `AlertaController` y `AlertaService`.

### Panel y fachada
Ofrece consultas resumidas para el panel y operaciones de consulta agrupadas.

Clases principales: `SmartGridController` y `SmartGridFacadeController`.

---

## 4. Patrones de diseño presentes

Los patrones se encuentran distribuidos en paquetes especializados y también colaboran con los módulos funcionales.

| Patrón | Paquete principal | Aplicación dentro del proyecto |
|---|---|---|
| Adapter | `adapter` | Adapta la autenticación local a la interfaz de autenticación utilizada por el sistema. |
| Builder | `builder` | Construye objetos de planta de generación configurando sus atributos. |
| Chain of Responsibility | `chain` | Encadena validaciones de plantas y reglas para procesos de alivio. |
| Command | `command` | Encapsula operaciones de creación de plantas y acciones de balanceo. |
| Composite | `composite` | Representa la jerarquía de zonas y componentes de la red eléctrica. |
| Decorator | `decorator` y `service` | Añade comportamientos a la autenticación y al cálculo de facturación. |
| Facade | `facade` | Agrupa operaciones y ofrece una interfaz simplificada para consultas del sistema. |
| Factory Method | `factory` | Centraliza la creación de tipos de factura. |
| Flyweight | `flyweight` | Comparte información común de las fuentes de energía. |
| Observer | `observer` | Permite reaccionar a eventos y notificar situaciones del dominio. |
| State | `state` | Modela estados de las plantas y el comportamiento asociado. |
| Strategy | `strategy` | Encapsula variantes de algoritmos, entre ellas cálculos y estrategias operativas. |
| Template Method | `template` | Define una estructura común para procesos con pasos especializados. |

Para conocer la implementación concreta, revisar las clases de cada paquete y las pruebas correspondientes. La presencia de un patrón no significa que todos los módulos deban utilizarlo.

---

## 5. Requisitos previos

Antes de ejecutar el backend se necesita:

- JDK 21.
- PostgreSQL instalado y en ejecución.
- Una base de datos llamada `smartgrid` (o ajustar la variable `DB_URL`).
- Maven instalado, o utilizar el Maven Wrapper incluido (`mvnw` / `mvnw.cmd`).
- IDE recomendado: IntelliJ IDEA o Visual Studio Code con soporte para Java.

---

## 6. Configuración de PostgreSQL

Crear la base de datos si todavía no existe:

```sql
CREATE DATABASE smartgrid;
```

La configuración predeterminada de `application.properties` utiliza:

- URL: `jdbc:postgresql://localhost:5432/smartgrid`
- Usuario: `postgres`
- Contraseña: variable de entorno `DB_PASSWORD` (por defecto vacía).

Se recomienda definir las credenciales mediante variables de entorno y no escribir contraseñas reales en el repositorio.

### Variables de entorno

| Variable | Valor de ejemplo | Descripción |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/smartgrid` | URL JDBC de PostgreSQL |
| `DB_USER` | `postgres` | Usuario de la base de datos |
| `DB_PASSWORD` | (tu contraseña local) | Contraseña de PostgreSQL |
| `JWT_SECRET` | (secreto propio de al menos 32 bytes) | Clave para firmar los JWT |

En Windows PowerShell, para la terminal actual:

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/smartgrid"
$env:DB_USER="postgres"
$env:DB_PASSWORD="TU_CLAVE_POSTGRES"
$env:JWT_SECRET="CAMBIA_ESTE_SECRETO_POR_UNO_PROPIO_DE_AL_MENOS_32_BYTES"
```

En IntelliJ IDEA también se pueden definir estas variables en **Run > Edit Configurations > Environment variables**.

> El secreto JWT incluido como valor alternativo en la configuración es únicamente para desarrollo. Debe reemplazarse por un secreto privado antes de cualquier despliegue.

---

## 7. Migraciones de base de datos

Flyway administra el esquema mediante los archivos de `src/main/resources/db/migration`.

| Migración | Contenido |
|---|---|
| `V1__usuario.sql` | Tabla de usuarios. |
| `V2__modelo_energetico.sql` | Zonas, clientes, medidores, lecturas, plantas, tarifas, facturas, eventos de demanda y alertas. Incluye tarifas iniciales. |
| `V3__historial_estado_planta.sql` | Historial de cambios de estado de las plantas. |
| `V4__balanceo_de_cargas.sql` | Ejecuciones y acciones de balanceo. |
| `V5__facturacion.sql` | Modalidades de tarifa, tramos, franjas, cargos adicionales y detalles de factura. |

La configuración actual usa `spring.jpa.hibernate.ddl-auto=validate`: Hibernate valida que las entidades coincidan con el esquema; Flyway se encarga de aplicar las migraciones.

La configuración también tiene `baseline-on-migrate=true` y `baseline-version=1` para contemplar una base existente que ya tenga la tabla `usuario`. Antes de conectar una base con datos importantes, realizar una copia de seguridad y verificar su estado de migración. No borrar ni modificar manualmente el historial de Flyway para forzar una actualización.

---

## 8. Ejecutar el backend

Desde la carpeta que contiene `pom.xml`:

### Windows

```powershell
.\mvnw.cmd spring-boot:run
```

### Linux / macOS

```bash
./mvnw spring-boot:run
```

También se puede ejecutar desde IntelliJ la clase principal:

```text
com.smartgrid.smartgrid.SmartgridApplication
```

La aplicación utiliza el puerto predeterminado de Spring Boot, **8080**, salvo que se configure otro.

---

## 9. Autenticación y seguridad

La autenticación se realiza mediante:

```http
POST /api/auth/login
Content-Type: application/json
```

Ejemplo de cuerpo:

```json
{
  "username": "admin",
  "password": "TU_CONTRASENA"
}
```

La respuesta exitosa contiene el usuario, el rol, el token y el tiempo de expiración. El token JWT debe enviarse en las solicitudes protegidas:

```http
Authorization: Bearer <token>
```

La configuración actual permite el acceso sin token a `/api/auth/**` y exige autenticación para las demás rutas. El token incluye el rol del usuario, que Spring Security convierte en autoridades con prefijo `ROLE_`.

Los servicios también contienen verificaciones de permisos para operaciones específicas. No se debe asumir que ocultar una opción en el frontend sustituye la autorización del backend.

---

## 10. Endpoints REST disponibles

Los siguientes son los mappings definidos en los controladores del proyecto. Las rutas protegidas requieren un JWT válido.

| Módulo | Método | Endpoint |
|---|---|---|
| Autenticación | POST | `/api/auth/login` |
| Usuarios | POST | `/api/usuarios` |
| Usuarios | GET | `/api/usuarios` |
| Usuarios | PUT | `/api/usuarios/{id}/estado` |
| Zonas | POST | `/api/zonas` |
| Zonas | GET | `/api/zonas` |
| Zonas | GET | `/api/zonas/arbol` |
| Clientes | POST | `/api/clientes` |
| Clientes | GET | `/api/clientes` |
| Clientes | GET | `/api/clientes/{id}/consumo` |
| Medidores | POST | `/api/medidores` |
| Medidores | GET | `/api/medidores` |
| Medidores | GET | `/api/medidores/{id}/consumo` |
| Lecturas | POST | `/api/lecturas` |
| Lecturas | GET | `/api/lecturas` |
| Plantas | POST | `/api/planta/crear` |
| Plantas | GET | `/api/planta` |
| Operación de planta | GET | `/api/planta/{id}` |
| Operación de planta | PUT | `/api/planta/{id}/estado` |
| Operación de planta | GET | `/api/planta/{id}/historial-estados` |
| Operación de planta | PUT | `/api/planta/{id}/zona` |
| Producción | GET | `/api/planta/{id}/produccion` |
| Generación | GET | `/api/generacion/resumen` |
| Balanceo | GET | `/api/balance` |
| Balanceo | GET | `/api/balance/estrategias` |
| Balanceo | GET | `/api/balance/plan` |
| Balanceo | POST | `/api/balance/ejecutar` |
| Balanceo | POST | `/api/balance/ejecuciones/{id}/revertir` |
| Balanceo | GET | `/api/balance/ejecuciones` |
| Balanceo | GET | `/api/balance/ejecuciones/{id}` |
| Balanceo | GET | `/api/balance/eventos` |
| Tarifas | POST | `/api/tarifas` |
| Tarifas | GET | `/api/tarifas` |
| Tarifas | GET | `/api/tarifas/vigente` |
| Cargos | POST | `/api/cargos` |
| Cargos | GET | `/api/cargos` |
| Cargos | PUT | `/api/cargos/{id}/estado` |
| Facturas | POST | `/api/facturas/emitir` |
| Facturas | POST | `/api/facturas/emitir-lote` |
| Facturas | GET | `/api/facturas/simular` |
| Facturas | GET | `/api/facturas` |
| Facturas | GET | `/api/facturas/{id}` |
| Facturas | PUT | `/api/facturas/{id}/pagar` |
| Facturas | PUT | `/api/facturas/{id}/anular` |
| Facturas | POST | `/api/facturas/marcar-vencidas` |
| Facturación | GET | `/facturacion/generar` |
| Alertas | GET | `/api/alertas` |
| Alertas | PUT | `/api/alertas/{id}/atender` |
| Panel / fachada | GET | `/api/facade/resumen` |
| Panel / fachada | GET | `/api/facade/panel` |
| Panel / fachada | GET | `/api/facade/panel-json` |
| Sistema | GET | `/smartgrid/estado` |
| Sistema | GET | `/smartgrid/singleton` |

Para conocer los parámetros, DTO requeridos y reglas de negocio de cada operación, consultar el método correspondiente en su controlador y los DTO asociados. Los mappings indican las rutas declaradas; no implican que todas las operaciones acepten los mismos parámetros ni que todos los roles puedan ejecutarlas.

---

## 11. Pruebas

El proyecto incluye pruebas automatizadas para servicios, patrones y componentes del dominio. Para ejecutarlas:

### Windows

```powershell
.\mvnw.cmd test
```

### Linux / macOS

```bash
./mvnw test
```

Entre las pruebas existentes se encuentran:

- `SmartgridApplicationTests`
- `TokenServiceTest`
- `UsuarioServiceTest`
- `PlantaGeneracionServiceTest`
- `PlantaOperacionServiceTest`
- `PlantaEnergiaValidacionTest`
- `FlyweightTest`
- `ComponenteRedCompositeTest`
- `CadenaAlivioTest`
- `BalanceoServiceTest`
- `BalanceoStrategyTest`
- `TarifaServiceTest`
- `TarifaStrategyTest`
- `FacturaServiceTest`
- `LecturaServiceTest`

También se pueden verificar los endpoints con Postman o el archivo `requests.http` incluido en el proyecto. Para rutas protegidas, iniciar sesión primero y utilizar el JWT recibido.

---

## 12. Integración con el frontend

El frontend Angular se ejecuta normalmente en:

```text
http://localhost:4200
```

El backend se ejecuta normalmente en:

```text
http://localhost:8080
```

La propiedad `smartgrid.cors.allowed-origins` define los orígenes autorizados para solicitudes desde el navegador. Su valor predeterminado es `http://localhost:4200`.

Si el frontend se ejecuta en otro origen o puerto, actualizar esa propiedad mediante la configuración correspondiente.

---

## 13. Recomendaciones de desarrollo

1. Mantener las reglas de negocio dentro de los servicios y no concentrarlas en los controladores.
2. Utilizar DTO para las solicitudes y respuestas de la API.
3. Crear cambios en la base de datos mediante nuevas migraciones Flyway; no editar migraciones que ya se hayan aplicado en otros entornos.
4. No almacenar contraseñas en texto plano ni publicar secretos JWT o credenciales de PostgreSQL.
5. Probar tanto los casos exitosos como los errores de validación y autorización.
6. Mantener actualizada esta documentación cuando se agreguen endpoints, módulos o migraciones.

---

## 14. Objetivos del proyecto

### Objetivo general

Desarrollar progresivamente un sistema de gestión de redes eléctricas inteligentes que permita administrar información relacionada con el consumo energético, la infraestructura de generación, las fuentes de energía renovable y la facturación, utilizando patrones de diseño de software para mejorar la organización, mantenibilidad y extensibilidad de la aplicación.

### Objetivos específicos

- Diseñar mecanismos para consultar y visualizar información relacionada con el consumo energético.
- Establecer una estructura que permita incorporar funcionalidades de gestión de la demanda y balanceo de cargas.
- Modelar la infraestructura de generación eléctrica y facilitar la representación de fuentes renovables, como la solar y la eólica.
- Implementar mecanismos de facturación que permitan calcular los costos del consumo de acuerdo con las tarifas y los parámetros definidos.
- Desarrollar un panel web para consultar información y funcionalidades disponibles en el sistema.
- Aplicar y documentar patrones de diseño que favorezcan la separación de responsabilidades, la reutilización del código y la evolución de la arquitectura.
- Verificar las implementaciones mediante pruebas y solicitudes a los endpoints REST, documentando los resultados obtenidos.
