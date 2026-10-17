# Proyecto de Patrones de Diseño de Software

Los patrones de diseño de software son soluciones reutilizables que permiten abordar problemas recurrentes durante el diseño y desarrollo de aplicaciones. No constituyen código listo para copiar y pegar, sino modelos de diseño que orientan la organización de las clases, la distribución de responsabilidades y la interacción entre los componentes de un sistema.

La aplicación adecuada de estos patrones contribuye a mejorar la mantenibilidad, la reutilización del código, la flexibilidad y la capacidad de evolución del software. Sin embargo, cada patrón debe seleccionarse de acuerdo con las necesidades del sistema, evitando introducir complejidad innecesaria.

Este proyecto tiene como propósito estudiar e implementar progresivamente diferentes patrones de diseño sobre **SmartGrid**, un sistema de gestión de redes eléctricas inteligentes. A través de estas implementaciones se busca comprender su funcionamiento, identificar los problemas que resuelven y evaluar cómo contribuyen a la estructura y organización de una aplicación.

## Organización del proyecto

El desarrollo se organiza por semanas. Cada directorio correspondiente a una semana contiene los avances y evidencias del trabajo realizado, permitiendo conservar un registro de la evolución del proyecto.

- **Proyecto principal:** `smartgrid/smartgrid`
- **Base de datos:** carpeta `Base de datos`
- **Documentación y evidencias:** directorios semanales y recursos incluidos en el repositorio.

Los patrones se incorporan de manera progresiva sobre el proyecto principal, procurando conservar las funcionalidades existentes y mantener una estructura organizada.

## SmartGrid

SmartGrid es un sistema de gestión de redes eléctricas inteligentes orientado a la administración de la infraestructura energética, la gestión del consumo y la integración de fuentes de energía renovable.

El proyecto se desarrolla progresivamente mediante la implementación de patrones de diseño que permiten organizar las responsabilidades del backend, reducir el acoplamiento entre componentes y facilitar el mantenimiento y la incorporación de nuevas funcionalidades.

La aplicación cuenta con un backend desarrollado con **Java y Spring Boot**, una base de datos **PostgreSQL** y una interfaz web desarrollada con **Angular**.

Las funcionalidades y los patrones implementados se documentan a lo largo de este README, incluyendo sus objetivos, las clases involucradas, los diagramas UML y las evidencias de funcionamiento disponibles.

> **Nota:** las instrucciones para descargar, configurar y ejecutar SmartGrid se encuentran al final de este documento.

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

## Patrones de diseño implementados

A lo largo del proyecto se incorporan patrones de diseño seleccionados de acuerdo con las necesidades de SmartGrid. Cada implementación incluye una descripción de su propósito, su aplicación dentro del sistema y los componentes principales que participan en ella.


## Patrón Singleton

### Propósito

El patrón creacional Singleton permite controlar la creación de instancias de una clase cuando se requiere un único objeto compartido para una responsabilidad determinada. Su objetivo es centralizar el acceso a ese objeto y evitar la creación de múltiples instancias independientes.

### Implementación en SmartGrid

En SmartGrid, este patrón se aplica a la clase `SmartGridConfig`, encargada de mantener la configuración general del sistema, como su nombre y su estado de activación.

La clase proporciona un punto de acceso a la configuración para que los componentes que la necesitan consulten o modifiquen sus valores de forma centralizada.

![Implementación de SmartGridConfig](assets/imagen1.png)

En la implementación del patrón se utiliza una instancia compartida, un constructor privado y el método `getInstancia()`, que permite obtener esa instancia sin crear objetos nuevos desde otras clases.

![Implementación del patrón Singleton](assets/imagen2.png)

### Diagrama UML

![Diagrama UML del patrón Singleton](assets/singleton.png)

### Video Patrón Singleton

[![Video del patrón Singleton](https://img.youtube.com/vi/1u5A9hnG09Y/0.jpg)](https://www.youtube.com/watch?v=1u5A9hnG09Y)

---

## Patrón Factory Method

### Propósito

El patrón creacional Factory Method define un método para crear objetos y delega a las implementaciones concretas la decisión de qué tipo de objeto instanciar. Esto permite separar la creación de los objetos de la lógica que los utiliza.

### Implementación en SmartGrid

En SmartGrid, Factory Method se utiliza en el módulo de facturación para generar facturas de diferentes categorías de clientes, como residenciales y comerciales.

La implementación se encuentra organizada en el paquete `com.smartgrid.smartgrid.factory` y se integra con las capas de servicio y controlador.

### Clases involucradas

- `Factura`: interfaz que define el contrato común para los diferentes tipos de factura.
- `FacturaResidencial` y `FacturaComercial`: productos concretos que implementan el comportamiento específico de cada categoría.
- `FacturaFactory`: creador abstracto que declara el método `crearFactura()`.
- `FacturaResidencialFactory` y `FacturaComercialFactory`: creadores concretos que deciden qué implementación de `Factura` instanciar.
- `FacturacionService`: utiliza la fábrica correspondiente para generar la factura y calcular el valor del consumo.
- `FacturacionController`: expone la funcionalidad mediante el endpoint REST `/api/facturacion`.

La interfaz `Factura` establece el contrato común del producto.

![Interfaz Factura](assets/imagen3.png)

Las clases concretas implementan el comportamiento correspondiente a cada tipo de factura.

![Implementación de FacturaResidencial](assets/imagen4.png)

![Implementación de FacturaComercial](assets/imagen5.png)

La clase abstracta `FacturaFactory` define el método de creación.

![Clase abstracta FacturaFactory](assets/imagen6.png)

Las fábricas concretas implementan ese método y devuelven el producto correspondiente. De esta forma, `FacturacionService` puede trabajar con la abstracción sin tener que construir directamente cada tipo de factura.

![Integración con FacturacionService](assets/imagen7.png)

### Funcionamiento

1. El cliente envía una solicitud al endpoint `/api/facturacion`, indicando el tipo de factura y el consumo.
2. El controlador delega el procesamiento a `FacturacionService`.
3. El servicio utiliza la fábrica correspondiente.
4. La fábrica crea una implementación concreta de `Factura`.
5. Se calcula el importe y se devuelve la respuesta en formato JSON.

### Ventajas

La incorporación de un nuevo tipo de factura puede realizarse mediante una nueva implementación de `Factura` y su fábrica correspondiente, reduciendo la necesidad de modificar la lógica que utiliza los productos. La ampliación concreta dependerá de cómo se registre y seleccione la nueva fábrica.

### Diagrama UML

![Diagrama UML de Factory Method](assets/FactoryMethod.png)

### Video patrón Factory Method

[![Video del patrón Factory Method](https://img.youtube.com/vi/Ty08ICiVTJ0/0.jpg)](https://www.youtube.com/watch?v=Ty08ICiVTJ0)

---

## Patrón Builder

### Propósito

El patrón creacional Builder permite construir objetos complejos paso a paso, separando el proceso de construcción de la representación final del objeto. Es especialmente útil cuando una clase contiene atributos obligatorios y opcionales.

### Implementación en SmartGrid

En SmartGrid, Builder se utiliza para construir objetos `PlantaEnergia`. Este patrón permite especificar los datos obligatorios de una planta y configurar opcionalmente su capacidad, batería de reserva y ubicación.

La implementación se encuentra en la clase `PlantaEnergia.java`, que contiene la clase interna estática `PlantaEnergiaBuilder`.

![Implementación de PlantaEnergia](assets/imagen8.png)

### Clases involucradas

- `PlantaEnergia`: representa la planta energética construida y contiene sus atributos.
- `PlantaEnergiaBuilder`: define los datos requeridos, los valores por defecto y los métodos encadenados para configurar las propiedades.
- `PlantaEnergiaService`: coordina la construcción a partir de los parámetros recibidos.
- `PlantaEnergiaController`: expone la operación mediante el endpoint REST `/api/planta/crear`.

El constructor del builder exige los atributos `nombre` y `tipoFuente`. Los métodos `capacidadKw()`, `voltajeSalida()`, `tieneBateriaReserva()` y `ubicacion()` permiten configurar los demás valores antes de invocar `build()`.

![Implementación del servicio Builder](assets/imagen9.png)

El controlador recibe los parámetros de la petición HTTP y delega la construcción al servicio, que devuelve la planta como respuesta JSON.

![Implementación del controlador Builder](assets/imagen10.png)

### Funcionamiento

1. El cliente envía una petición al endpoint `/api/planta/crear`.
2. El servicio crea un `PlantaEnergiaBuilder` con el nombre y el tipo de fuente.
3. Si se reciben valores opcionales, el servicio configura las propiedades correspondientes.
4. Se invoca `build()` para obtener el objeto `PlantaEnergia`.
5. El controlador devuelve el resultado en formato JSON.

### Ventajas

Builder facilita la construcción de plantas con diferentes configuraciones sin tener que definir múltiples constructores. Además, la clase `PlantaEnergia` utiliza atributos finales, por lo que sus datos no pueden modificarse después de construir la instancia mediante los mecanismos habituales de acceso.

**Alcance actual:** la planta se construye en memoria y se devuelve en la respuesta HTTP; esta operación no implica su persistencia en PostgreSQL.

### Diagrama UML

![Diagrama UML del patrón Builder](assets/builder.png)

### Video patrón Builder

[![Video del patrón Builder](https://img.youtube.com/vi/ghf-MfEC9mA/0.jpg)](https://www.youtube.com/watch?v=ghf-MfEC9mA)

---


No todos los patrones de diseño aportan beneficios a todos los sistemas. En SmartGrid también se han evaluado patrones que, por las necesidades actuales del proyecto, no se han incorporado a la implementación.

### Abstract Factory

Abstract Factory permite crear familias de objetos relacionados sin depender de sus clases concretas. No se implementa actualmente porque los módulos de configuración, facturación y construcción de plantas no requieren crear familias de objetos relacionados como una unidad. Añadir una jerarquía de fábricas adicionales incrementaría la complejidad sin resolver una necesidad actual del sistema.

### Prototype

Prototype permite crear objetos mediante la clonación de instancias existentes. No se implementa actualmente porque la construcción de las plantas ya está cubierta por Builder, que permite configurar explícitamente los atributos obligatorios y opcionales. En el alcance actual no existe una necesidad concreta de duplicar plantas a partir de prototipos existentes.



### Patrón Adapter

El patrón Adapter se implementa en Smart Grid dentro del módulo de autenticación. Su objetivo es permitir que el proceso de autenticación trabaje mediante una interfaz común, separando la lógica utilizada para validar los usuarios de los componentes que consumen este servicio.

La aplicación define la interfaz `Autenticacion`, la cual establece el contrato mediante el método `autenticar(String username, String password)`. Esta interfaz permite que el sistema trabaje con una abstracción de autenticación sin depender directamente de la forma en que se almacenan o validan los usuarios.

![imagen 1 patron adapter](assets/imagen11.png)

La implementación concreta se encuentra en `AutenticacionLocalAdapter`, ubicada dentro del paquete `adapter`. Esta clase implementa la interfaz `Autenticacion` y adapta el proceso de autenticación local utilizando `UsuarioRepository` y `PasswordEncoder`.

![imagen 2 patron adapter](assets/imagen13.png)

Cuando el usuario intenta iniciar sesión, el Adapter busca el usuario mediante `UsuarioRepository`, verifica que se encuentre activo y posteriormente comprueba la contraseña utilizando BCrypt. Si las credenciales son correctas, retorna el objeto `Usuario`; de lo contrario, retorna un resultado nulo.

![imagen 3 patron adapter](assets/imagen13.png)

Por su parte, `AutenticacionService` trabaja con la abstracción `Autenticacion` y no necesita conocer directamente la implementación utilizada para realizar la autenticación. De esta manera, el servicio puede utilizar el Adapter sin quedar acoplado directamente al repositorio o a la lógica de validación de contraseñas.

El flujo de autenticación queda organizado de la siguiente manera:

`AuthController → AutenticacionService → AutenticacionLocalAdapter → UsuarioRepository → PostgreSQL`

Esta estructura permite que en el futuro se pueda agregar otra forma de autenticación, como una autenticación mediante un servicio externo, creando un nuevo Adapter que implemente la misma interfaz sin modificar el controlador ni la lógica principal del servicio.

### Patrón Decorator

El patrón Decorator se implementa en Smart Grid como complemento del sistema de autenticación. Su finalidad es permitir agregar una responsabilidad adicional al proceso de inicio de sesión sin modificar directamente la implementación original de la autenticación.

Para esto se utiliza la clase abstracta `AutenticacionDecorator`, que implementa la interfaz `Autenticacion` y mantiene una referencia hacia otra implementación de la misma interfaz. Esto permite envolver el comportamiento existente y agregar nuevas funcionalidades.

![imagen 1 patron decorator](assets/imagen14.png)

La implementación concreta es `AuditoriaLoginDecorator`. Esta clase recibe `AutenticacionLocalAdapter` y ejecuta primero el proceso normal de autenticación. Después de obtener el resultado, agrega la funcionalidad de auditoría registrando en consola si el inicio de sesión fue exitoso o fallido.

Por ejemplo, cuando el usuario `admin` inicia sesión correctamente, se registra:

`[AUDITORIA] Inicio de sesión exitoso: admin`

Si las credenciales son incorrectas, se registra:

`[AUDITORIA] Intento de inicio de sesión fallido: admin`

![imagen 1 patron decorator](assets/imagen15.png)


La principal ventaja de esta implementación es que la auditoría se agrega sin modificar `AutenticacionLocalAdapter`. El Decorator funciona como una capa adicional alrededor del proceso original.

El flujo queda organizado de la siguiente manera:

`AuthController → AutenticacionService → AuditoriaLoginDecorator → AutenticacionLocalAdapter → UsuarioRepository`

De esta manera, si posteriormente se necesita agregar otra responsabilidad al inicio de sesión, como registrar estadísticas o generar otro tipo de registro, se puede implementar mediante otro Decorator sin modificar la lógica original de autenticación.

### Diagrama UML Adapter y Decorator

![diagrama UML patron adapter y decorator](assets/Adapter&Decorator.jpg)

### Video Patrón Adapter y Decorator

[![Video patron Adapter y Decorator](https://img.youtube.com/vi/SI6Rr6OUCIs/0.jpg)](https://www.youtube.com/watch?v=SI6Rr6OUCIs)

---


### Patrón Bridge

El patrón Bridge permite separar una abstracción de su implementación, permitiendo que ambas puedan evolucionar de manera independiente. Es especialmente útil cuando existen dos dimensiones de variación que pueden cambiar por separado y que generarían múltiples combinaciones de clases si permanecieran acopladas.

Después de analizar la arquitectura actual de Smart Grid, se decidió no implementar este patrón debido a que el sistema no presenta actualmente dos jerarquías independientes que necesiten evolucionar de forma separada.

Los componentes implementados, como la autenticación, las facturas, las plantas de energía y la configuración general del sistema, tienen responsabilidades concretas y no requieren separar una abstracción de diferentes implementaciones independientes.

Implementar Bridge en este punto implicaría agregar abstracciones, interfaces e implementaciones adicionales que no solucionarían un problema existente dentro del proyecto.

Por esta razón, se decidió no utilizar este patrón para evitar aumentar innecesariamente la complejidad de la arquitectura.

### Patrón Composite

El patrón Composite permite representar estructuras jerárquicas de objetos en las cuales tanto los elementos individuales como los grupos de elementos pueden ser tratados mediante una misma interfaz.

Un ejemplo común sería una estructura de archivos donde una carpeta puede contener archivos y otras carpetas, permitiendo tratar ambos elementos como componentes de una misma estructura.

En Smart Grid no se presenta actualmente una estructura jerárquica de este tipo. Las entidades y componentes desarrollados, como usuarios, facturas y plantas de energía, funcionan como elementos independientes y no necesitan formar una estructura de árbol donde un objeto contenga otros objetos del mismo tipo.

Por ejemplo, una `PlantaEnergia` no contiene otras plantas que deban ser tratadas como una única estructura, ni una factura contiene otras facturas que necesiten ser administradas mediante una composición jerárquica.

Por esta razón, implementar Composite únicamente para demostrar el patrón generaría clases y estructuras adicionales sin aportar una solución a una necesidad real del sistema.

Se decidió no utilizar este patrón y mantener la arquitectura enfocada en las relaciones y responsabilidades que realmente necesita Smart Grid.

## Patrón Facade 

Se implementó el patrón de diseño estructural **Facade** para simplificar el acceso a las funcionalidades principales del sistema SmartGrid. Este patrón proporciona un punto de entrada unificado que coordina las operaciones de diferentes servicios del backend.

![imagen 1 patron facade](assets/imagen16.png)

El frontend desarrollado en Angular realiza una petición HTTP al backend. El controlador recibe la solicitud y delega la operación a `SmartGridFacade`, que coordina las llamadas a los servicios correspondientes.

La fachada reúne los resultados y permite devolver una respuesta estructurada en formato JSON, que Angular utiliza para actualizar la información del panel sin recargar la página.

### Endpoints implementados

| Método HTTP | Endpoint                 | Función                                                                    |
| ----------- | ------------------------ | -------------------------------------------------------------------------- |
| GET         | `/api/facade/resumen`    | Consulta un resumen del sistema.                                           |
| GET         | `/api/facade/panel`      | Obtiene la información del panel en formato de texto.                      |
| GET         | `/api/facade/panel-json` | Devuelve el estado del sistema y los datos de facturación en formato JSON. |

El endpoint `/api/facade/panel-json` acepta los parámetros `tipo` y `consumo`. Si no se especifican, utiliza los valores predeterminados `RESIDENCIAL` y `350`, respectivamente.

![imagen 2 patron facade](assets/imagen17.png)

**Ejemplo de consulta:**

```http
GET http://localhost:8080/api/facade/panel-json?tipo=RESIDENCIAL&consumo=350
```

**Respuesta esperada:**

```json
{
  "estadoSistema": "SmartGrid está activo",
  "factura": {
    "tipoFactura": "RESIDENCIAL",
    "consumo": 350.0,
    "tarifa": 500.0,
    "total": 175000.0
  }
}
```

### Integración con Angular

Se desarrolló un panel de control que consume el endpoint `/api/facade/panel-json` mediante `HttpClient`.

La interfaz permite:

* Consultar el estado del sistema SmartGrid.
* Seleccionar facturación residencial o comercial.
* Ingresar el consumo energético en kWh.
* Visualizar la tarifa aplicada y el total de la factura.
* Actualizar los resultados sin recargar la página.

![imagen 3 patron facade](assets/imagen18.png)

![imagen 4 patron facade](assets/imagen19.png)

![imagen 5 patron facade](assets/imagen20.png)

### Ventajas

* Simplifica la interacción entre el controlador y los servicios del backend.
* Centraliza la coordinación de las operaciones relacionadas con el panel.
* Reduce el acoplamiento entre los componentes.
* Facilita el mantenimiento y la integración con el frontend.
* Permite reutilizar los servicios sin duplicar su lógica de negocio.

### Diagrama UML Facade 

![imagen 6 patron facade](assets/UmlFacade.png)

La implementación de Facade demuestra cómo una fachada puede ofrecer una interfaz simplificada para acceder a varias funcionalidades de un sistema, manteniendo separadas las responsabilidades de consulta del estado y cálculo de facturación.

### Video Patrón Facade

[![Video patron Adapter y Decorator](https://img.youtube.com/vi/WedsvCXpcdo/0.jpg)](https://www.youtube.com/watch?v=WedsvCXpcdo)

---


## Patrón Flyweight

### Propósito

El patrón estructural Flyweight permite compartir objetos que contienen información común entre múltiples instancias, reduciendo la creación de objetos duplicados y el consumo de memoria.

### Implementación en SmartGrid

En SmartGrid, Flyweight se utiliza para compartir la información común de las fuentes de energía de las plantas. Por ejemplo, varias plantas solares pueden reutilizar el mismo objeto con la descripción y las características de la energía solar.

**Clases involucradas:**

- `FuenteEnergiaFlyweight`: almacena la información compartida de una fuente energética, incluyendo su tipo, descripción y características.
- `FuenteEnergiaFactory`: administra los objetos compartidos mediante un `HashMap` y reutiliza el objeto correspondiente al tipo de fuente solicitado.
- `PlantaEnergia`: integra Flyweight con el patrón Builder y mantiene una referencia al objeto compartido de su fuente energética.
- `FlyweightTest`: comprueba que dos plantas solares reutilizan la misma instancia de `FuenteEnergiaFlyweight`.

![imagen 1 patron Flyweight](assets/imagen21.png)

### Funcionamiento

Cuando se construye una planta, `PlantaEnergia` solicita a `FuenteEnergiaFactory` el objeto correspondiente a su fuente energética. Si ya existe, se reutiliza; de lo contrario, se crea y se almacena para futuras solicitudes.

![imagen 2 patron Flyweight](assets/imagen22.png)

La comparación mediante `==` en la prueba automatizada permite verificar que dos plantas solares hacen referencia al mismo objeto en memoria.

### Prueba realizada

La prueba `FlyweightTest` finalizó correctamente con el resultado **Test passed**, confirmando la reutilización de la instancia para dos plantas solares.

![imagen 3 patron Flyweight](assets/imagen23.png)

**Alcance actual:** las plantas se construyen en memoria y todavía no se persisten en la base de datos.

### Diagrama UML Flyweight

![diagrama UML Flyweight](assets/UmlFlyweight.png)

---

### Video patrón Flayweight

[![Video del patrón Factory Method](https://img.youtube.com/vi/15xdZdC2LHo/0.jpg)](https://www.youtube.com/watch?v=15xdZdC2LHo)

---

## Patrón Proxy

### Propósito

El patrón estructural Proxy proporciona un intermediario que controla el acceso a un objeto o servicio real antes de delegarle una operación.

### Implementación en SmartGrid

En SmartGrid, Proxy controla el acceso a la construcción de plantas energéticas según el rol del usuario. Solamente los usuarios activos con el rol `ADMIN` pueden ejecutar esta operación.

![imagen 1 patron Proxy](assets/imagen24.png)

**Clases involucradas:**

- `PlantaEnergiaController`: recibe la solicitud HTTP y busca al usuario en la base de datos mediante `UsuarioRepository`.
- `PlantaEnergiaProxy`: actúa como intermediario, valida los permisos del usuario y delega la operación al servicio real.
- `AutorizacionService`: comprueba que el usuario esté activo y tenga el rol requerido. Si no cumple las condiciones, genera una respuesta HTTP `403 Forbidden`.
- `PlantaEnergiaService`: construye la planta utilizando el patrón Builder.


### Funcionamiento

1. El controlador recibe la solicitud para construir una planta.
2. Se busca el usuario por su nombre de usuario.
3. El controlador entrega el usuario y los datos de la planta a `PlantaEnergiaProxy`.
4. El Proxy solicita a `AutorizacionService` la validación del rol `ADMIN`.
5. Si el usuario está autorizado, el Proxy delega la construcción a `PlantaEnergiaService`.
6. Si el usuario no está autorizado o está inactivo, la operación se rechaza con HTTP `403 Forbidden`.

![imagen 2 patron Proxy](assets/imagen25.png)

### Pruebas realizadas

| Usuario | Resultado | Descripción |
|---|---|---|
| `admin` | HTTP 200 OK | Se permite construir la planta. |
| `operador` | HTTP 403 Forbidden | Se rechaza la operación por falta de autorización. |

![imagen 3 patron Proxy](assets/imagen26.png)

**Alcance actual:** Proxy controla el acceso a la construcción de plantas, pero estas todavía no se guardan en PostgreSQL. La autorización se basa en el usuario y su rol recuperados de la base de datos; no representa por sí sola un sistema completo de autenticación y seguridad HTTP.

### Diagrama UML Proxy

![UML patron Proxy](assets/UmlProxy.png)

---

### Video patrón Proxy

[![Video del patrón Factory Method](https://img.youtube.com/vi/dnaba2vTqcw/0.jpg)](https://www.youtube.com/watch?v=dnaba2vTqcw)

---

# SmartGrid — Guía de instalación y ejecución

## 1. Descripción del proyecto

SmartGrid es un proyecto académico desarrollado con Java, Spring Boot, PostgreSQL y Angular. Su objetivo es aplicar patrones de diseño de software en un sistema relacionado con la gestión de energía.

El proyecto incluye un backend que expone servicios mediante una API REST, una base de datos PostgreSQL y una interfaz web desarrollada con Angular.

Entre los patrones implementados se encuentran Singleton, Factory Method, Builder, Flyweight, Proxy, Adapter, Decorator y Facade.

## 2. Requisitos previos

Antes de ejecutar el proyecto, se recomienda tener instaladas las siguientes herramientas:

### Backend
- Java JDK 21.
- IntelliJ IDEA o un editor compatible con proyectos Maven.
- Maven, si se desea ejecutarlo desde la terminal y no mediante el Maven Wrapper del proyecto.

### Base de datos
- PostgreSQL.
- pgAdmin 4, recomendado para administrar la base de datos.

### Frontend
- Node.js, compatible con la versión de Angular utilizada.
- npm, incluido normalmente con Node.js.
- Un navegador web moderno.

El proyecto se desarrolló principalmente con IntelliJ IDEA para el backend y Angular CLI para el frontend.

## 3. Descargar el repositorio

El código fuente está disponible en GitHub:

- [Repositorio principal Proyecto_Patrones](https://github.com/juandpl18/Proyecto_Patrones)
- [Backend Spring Boot](https://github.com/juandpl18/Proyecto_Patrones/tree/main/smartgrid/smartgrid)
- [Frontend Angular](https://github.com/juandpl18/Proyecto_Patrones/tree/main/smartgrid-frontend)
- [Archivo SQL de SmartGrid](https://github.com/juandpl18/Proyecto_Patrones/blob/main/Base%20de%20datos/smartgrid.sql)

### Descargar mediante ZIP

1. Abrir el repositorio principal.
2. Seleccionar **Code → Download ZIP**.
3. Extraer el archivo descargado en una carpeta del equipo.

### Descargar mediante Git

También se puede clonar el repositorio:

```bash
git clone https://github.com/juandpl18/Proyecto_Patrones.git
```

La estructura principal es:

```text
Proyecto_Patrones/
├── Base de datos/
│   └── smartgrid.sql
├── smartgrid/
│   └── smartgrid/          # Backend Spring Boot
├── smartgrid-frontend/     # Frontend Angular
└── README.md
```

## 4. Configurar la base de datos PostgreSQL

### 4.1. Crear la base de datos

1. Abrir pgAdmin 4.
2. Conectarse al servidor PostgreSQL.
3. Crear una base de datos llamada `smartgrid`.

La configuración local utilizada originalmente es:

```text
Host: localhost
Puerto: 5432
Base de datos: smartgrid
Usuario: postgres
```

Si el servidor utiliza otro puerto o usuario, será necesario ajustar la configuración.

### 4.2. Importar el archivo SQL

El repositorio incluye el archivo:

```text
Base de datos/smartgrid.sql
```

Este archivo debe contener las instrucciones necesarias para crear las tablas y, si corresponde, insertar los datos de prueba.

Para importarlo:

1. Seleccionar la base de datos `smartgrid` en pgAdmin.
2. Abrir **Query Tool**.
3. Cargar el archivo SQL.
4. Revisar su contenido y ejecutar las instrucciones.

**Importante:** si el archivo contiene un respaldo en formato personalizado de PostgreSQL, no se debe ejecutar directamente como SQL. En ese caso, se debe utilizar la herramienta de restauración apropiada. Si está en formato SQL plano, se puede ejecutar mediante Query Tool.

Antes de continuar, comprobar que las tablas necesarias existan y que los datos de prueba estén disponibles.

## 5. Configurar el backend Spring Boot

### 5.1. Abrir el proyecto

En IntelliJ IDEA:

1. Seleccionar **File → Open**.
2. Abrir la carpeta:

```text
Proyecto_Patrones/smartgrid/smartgrid
```

3. Seleccionar el archivo `pom.xml` si IntelliJ solicita importar el proyecto Maven.
4. Esperar a que se descarguen las dependencias.

### 5.2. Verificar Java 21

En IntelliJ, entrar en:

```text
File → Project Structure → Project
```

Comprobar que el SDK del proyecto sea Java 21.

También se puede ejecutar en la terminal:

```bash
java -version
```

### 5.3. Configurar la conexión a PostgreSQL

Abrir el archivo:

```text
src/main/resources/application.properties
```

Verificar que los datos de conexión coincidan con la configuración local.

Ejemplo:

```properties
spring.application.name=smartgrid

spring.datasource.url=jdbc:postgresql://localhost:5432/smartgrid
spring.datasource.username=postgres
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

En este ejemplo, `DB_PASSWORD` es una variable de entorno que contiene la contraseña de PostgreSQL. Se debe configurar antes de iniciar la aplicación.

No se deben publicar contraseñas reales en GitHub. Si la configuración existente utiliza otro mecanismo, debe conservarse o adaptarse correctamente antes de ejecutar el proyecto.

### 5.4. Iniciar el backend

Localizar la clase principal:

```text
src/main/java/com/smartgrid/smartgrid/SmartgridApplication.java
```

Abrirla y pulsar **Run** en IntelliJ.

También se puede ejecutar desde la carpeta del backend:

```bash
mvn spring-boot:run
```

Si Maven no está instalado, se puede utilizar el Maven Wrapper cuando esté incluido en el repositorio.

El servidor normalmente queda disponible en:

```text
http://localhost:8080
```

Esta dirección corresponde a la raíz del servidor; no necesariamente muestra una página web.

## 6. Configurar el frontend Angular

### 6.1. Abrir el proyecto

Abrir una terminal en la carpeta:

```text
Proyecto_Patrones/smartgrid-frontend
```

Verificar que estén presentes, entre otros, estos archivos:

```text
angular.json
package.json
package-lock.json
tsconfig.json
src/
public/
```

### 6.2. Verificar Node.js y npm

Ejecutar:

```bash
node -v
npm -v
```

Si alguno de los comandos no funciona, instalar Node.js en una versión compatible con Angular.

### 6.3. Instalar las dependencias

Desde la carpeta `smartgrid-frontend`, ejecutar:

```bash
npm install
```

Este comando descarga las dependencias declaradas en `package.json`. No es necesario subir `node_modules` a GitHub.

### 6.4. Compilar el frontend

Ejecutar:

```bash
npx ng build
```

Si la compilación termina correctamente, Angular ha podido construir el proyecto.

Si aparecen errores, se deben corregir antes de considerar terminada la verificación.

### 6.5. Iniciar Angular

Ejecutar:

```bash
npx ng serve
```

Abrir en el navegador la dirección que indique Angular, normalmente:

```text
http://localhost:4200
```

Para probar la aplicación completa, mantener abiertas las terminales del frontend y del backend.

## 7. Conectar Angular con Spring Boot

Angular debe enviar sus solicitudes HTTP a las rutas expuestas por el backend.

Durante el desarrollo local, el backend normalmente utiliza:

```text
http://localhost:8080
```

Y Angular:

```text
http://localhost:4200
```

Revisar los servicios del frontend para comprobar que utilizan las rutas correctas de la API, por ejemplo, las rutas de facturación, plantas o Facade que existan en el código.

Si las solicitudes son bloqueadas por el navegador debido a CORS, se debe revisar la configuración CORS del backend y autorizar el origen de Angular para el entorno de desarrollo.

No se debe asumir que todas las rutas tienen el mismo prefijo. Las URL de los controladores deben coincidir con las utilizadas por los servicios Angular.

## 8. Probar los patrones de diseño

Los siguientes ejemplos corresponden a las rutas y operaciones implementadas en el proyecto. Si algún controlador ha cambiado, se debe utilizar la ruta definida actualmente en el código.

### 8.1. Singleton

El patrón Singleton se utiliza para mantener una instancia compartida de la configuración del sistema.

Se puede comprobar mediante el endpoint de estado definido por `SmartGridController` o el controlador equivalente del proyecto.

### 8.2. Factory Method

El patrón Factory Method permite seleccionar la fábrica que crea el tipo de factura solicitado.

En la implementación del proyecto, la ruta utilizada es `/api/facturacion`. Revisar los parámetros que recibe el controlador antes de enviar la solicitud desde Postman.

### 8.3. Builder

El patrón Builder permite construir objetos `PlantaEnergia` indicando los atributos obligatorios y los opcionales.

La ruta es:

```text
POST /api/planta/crear
```

El controlador utiliza los parámetros de la solicitud, busca el usuario y delega la autorización al Proxy. Para una prueba autorizada se puede utilizar, por ejemplo, un usuario administrador existente:

```text
http://localhost:8080/api/planta/crear?username=admin&nombre=PlantaSolar&fuente=SOLAR&capacidad=1000&bateria=true&ubicacion=Bucaramanga
```

Si el usuario tiene permisos, se devuelve el objeto construido en formato JSON. En la implementación actual, esta operación construye el objeto en memoria; no significa que la planta se guarde en PostgreSQL.

### 8.4. Flyweight

El patrón Flyweight permite compartir objetos que contienen información común de las fuentes de energía.

La prueba automatizada `FlyweightTest` comprueba que dos plantas con la misma fuente solar reutilizan la misma instancia compartida.

### 8.5. Proxy

El patrón Proxy controla el acceso a la construcción de plantas mediante la validación del rol del usuario.

Para probarlo, se puede enviar la misma solicitud con un usuario administrador y luego con un usuario que no tenga el rol requerido. La solicitud no autorizada debe rechazarse con un estado HTTP `403 Forbidden`.

Esta validación es una demostración académica basada en el usuario enviado en la solicitud; no reemplaza un sistema completo de autenticación para producción.

### 8.6. Adapter y Decorator

Estos patrones se aplican al módulo de inicio de sesión y auditoría. Para comprobarlos, ejecutar el flujo de inicio de sesión disponible en el proyecto y revisar el resultado y los registros de auditoría que genera la implementación.

### 8.7. Facade

El patrón Facade proporciona un punto de acceso simplificado a varias operaciones del sistema.

Las rutas implementadas incluyen:

```text
/api/facade/resumen
/api/facade/panel
/api/facade/panel-json
```

Se pueden probar desde Postman o desde el frontend, según la integración disponible.

## 9. Estructura principal del backend

```text
smartgrid/
├── src/
│   ├── main/
│   │   ├── java/com/smartgrid/smartgrid/
│   │   │   ├── builder/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── factory/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── pom.xml
└── README.md
```

La estructura puede variar según las clases y paquetes presentes en la versión actual del repositorio.

## 10. Problemas frecuentes

**Error de conexión con PostgreSQL**
- Comprobar que PostgreSQL esté iniciado.
- Verificar la base de datos, el puerto, el usuario y la contraseña.
- Comprobar que las tablas requeridas existan.

**Error al compilar Spring Boot**
- Verificar Java 21.
- Esperar a que Maven descargue las dependencias.
- Revisar los mensajes de error de IntelliJ.

**Error al ejecutar Angular**
- Verificar la versión de Node.js.
- Ejecutar `npm install`.
- Revisar los errores de `npx ng build`.

**Angular no puede consumir la API**
- Comprobar que Spring Boot esté ejecutándose.
- Revisar las URL de los servicios Angular.
- Revisar la configuración CORS y los errores de la pestaña Network del navegador.

**Error HTTP 403 al crear una planta**
- Verificar que el usuario exista.
- Comprobar que esté activo y tenga el rol de administrador requerido.

## 11. Resumen de ejecución

```text
1. Descargar el repositorio
          ↓
2. Crear y preparar PostgreSQL
          ↓
3. Importar smartgrid.sql si corresponde
          ↓
4. Configurar application.properties
          ↓
5. Iniciar Spring Boot en el puerto 8080
          ↓
6. Abrir smartgrid-frontend
          ↓
7. Ejecutar npm install
          ↓
8. Ejecutar npx ng build
          ↓
9. Ejecutar npx ng serve
          ↓
10. Abrir http://localhost:4200
          ↓
11. Probar la conexión con el backend y las funciones
```

Con estos pasos se puede preparar el entorno local de SmartGrid, ejecutar el backend y la interfaz Angular y comprobar los patrones de diseño implementados.


