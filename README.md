# Proyecto_Patrones
Los patrones de software son soluciones reutilizables para problemas comunes que aparecen durante el diseño y desarrollo de aplicaciones. No representan código listo para copiar y pegar, sino que funcionan como guías o modelos de diseño que permiten organizar el software de una manera más eficiente, mantenible y escalable.

La implementación de patrones de software permite mejorar la organización del código, resolver problemas recurrentes y facilitar el mantenimiento y la evolución de los sistemas. Por esta razón, en este proyecto se implementarán diferentes patrones de software sobre el proyecto Smart Grid, con el propósito de mejorar progresivamente su estructura, calidad y organización.

## Smart Grid

Smart Grid es un sistema de gestión de redes eléctricas inteligentes enfocado en el monitoreo y administración del consumo energético. El proyecto será actualizado progresivamente mediante la implementación de diferentes patrones de software, buscando mejorar la calidad del código, reducir el acoplamiento entre sus componentes y facilitar su mantenimiento y extensión. 

### Objetivo general
Desarrollar un sistema de gestión de redes inteligentes (Smart Grid) que permita monitorear y administrar el consumo energético en tiempo real, optimizar la distribución de la energía mediante el balanceo de cargas, integrar fuentes de energía renovable y gestionar un sistema de facturación dinámica.

### Objetivos específicos
•	Implementar un sistema de monitoreo del consumo energético en tiempo real que permita visualizar y registrar el uso de energía de los usuarios 

•	Desarrollar mecanismos de balanceo de carga y gestión de picos de consumo

•	Integrar fuentes de energía renovable como energía solar o eólica, permitiendo registrar y gestionar la energía generada y su incorporación a la red.

•	Diseñar un sistema de facturación dinámica que permita calcular el costo del consumo energético de acuerdo con variables como el horario, la demanda y el consumo registrado.

•	Desarrollar un panel de gestión y visualización que permita consultar información sobre consumo, generación, demanda, costos y estado general de la red.

### Patron Singleton

El sistema implementará el patrón de diseño Singleton, utilizado para garantizar que determinados componentes de la Smart Grid cuenten con una única instancia y puedan ser gestionados de manera centralizada.

En nuestro proyecto Smart Grid lo utilizamos en la clase SmartGridConfig, porque queremos tener una única configuración general del sistema. Por ejemplo, el nombre del sistema, si está activo o inactivo y posteriormente parámetros como la tarifa eléctrica o los límites de consumo

![imagen 1 SmartGridConfig](assets/imagen1.png)

La parte del código que se ve afectada principalmente es la clase SmartGridConfig. Ahí se implementa el Singleton mediante una variable estática que almacena la instancia, un constructor privado que evita crear objetos desde otras clases y el método getInstancia(), que permite obtener siempre la misma instancia.

![imagen 2 patron singleton](assets/imagen2.png)

### Video Patrón Singleton
[![Video patron singleton](https://img.youtube.com/vi/1u5A9hnG09Y/0.jpg)](https://www.youtube.com/watch?v=1u5A9hnG09Y)

### Patron factory method

El segundo patrón que implementamos en nuestro proyecto SmartGrid es el patrón Factory Method, aplicado dentro del módulo de facturación. Toda la estructura de este patrón se encuentra organizada dentro del paquete com.smartgrid.smartgrid.factory, comunicándose directamente con la capa de service y controller.

La idea principal es que SmartGrid pueda manejar diferentes tipos de facturas (como residenciales y comerciales) con sus respectivas formas de calcular tarifas, sin acoplar la lógica de negocio a clases concretas.

Para implementar el patrón, primero definimos la interfaz Factura dentro de la carpeta factory, la cual actúa como el Producto Abstracto estableciendo el contrato general mediante el método generarFactura().

![imagen 1 factory method](assets/imagen3.png)

A partir de esta interfaz, creamos FacturaResidencial y FacturaComercial, que representan los Productos Concretos. En estas clases se implementa la lógica específica de tarifa y formato según la categoría del usuario.

![imagen 2 factory method](assets/imagen4.png)

![imagen 3 factory method](assets/imagen5.png)

Luego, creamos la clase abstracta FacturaFactory, que cumple el rol de Creador Abstracto. Aquí se encuentra el núcleo del patrón: el método abstracto crearFactura(), que es formalmente el Factory Method.

![imagen 4 factory method](assets/imagen6.png)

Este método define qué objeto de tipo Factura debe ser creado, pero deja que las clases hijas (Creadores Concretos) decidan exactamente qué implementación instanciar. Por ejemplo, tenemos FacturaResidencialFactory, que cuando ejecuta crearFactura() retorna una FacturaResidencial, y tenemos FacturaComercialFactory, que retorna una FacturaComercial.

Por su parte, en el servicio FacturacionService (ubicado en el paquete service) no creamos directamente las facturas residenciales o comerciales. El servicio opera como cliente del patrón trabajando con la abstracción FacturaFactory mediante la inyección de dependencias de Spring.

![imagen 5 factory method](assets/imagen7.png)

Esta lógica se conecta con el exterior a través de FacturacionController (en el paquete controller), el cual expone el endpoint REST /api/facturacion. Al ingresar una petición HTTP especificando el tipo de cliente y el consumo, el controlador transfiere los datos a FacturacionService, el cual busca la fábrica correspondiente, ejecuta su Factory Method y retorna el cálculo en formato JSON.

De esta manera, si en el futuro necesitamos agregar una factura industrial, simplemente creamos FacturaIndustrial y FacturaIndustrialFactory dentro del paquete factory, sin necesidad de modificar la lógica de FacturacionService ni del controlador. Así, Factory Method logra separar por completo la creación de los objetos de su utilización, haciendo que el sistema sea mucho más fácil de ampliar y mantener.

### Video Patrón Factory Method

[![Video patron singleton](https://img.youtube.com/vi/Ty08ICiVTJ0/0.jpg)](https://www.youtube.com/watch?v=Ty08ICiVTJ0)

Este módulo incorpora el patrón creacional Builder para gestionar la construcción flexible inmutable de objetos de infraestructura en la red eléctrica, como plantas de energía. Su adición permite instanciar componentes de red diferenciando estrictamente los datos obligatorios de los opcionales. De esta forma, el proyecto evita la creación de constructores recargados o el envío repetitivo de valores nulos, al mismo tiempo que garantiza la inmutabilidad de los datos en memoria y expone un servicio REST preparado para conectarse con Angular sin requerir cambios en la base de datos.

La aplicación del patrón se realiza definiendo la clase principal PlantaEnergia con un constructor privado que incorpora dentro una clase estática estandarizada denominada PlantaEnergiaBuilder. El constructor del builder exige obligatoriamente los parámetros nombre y tipoFuente, mientras que los métodos encadenados como capacidadKw o ubicacion configuran las propiedades opcionales antes de ejecutar el método final build para obtener la instancia definitiva.

En cuanto al detalle del código, el archivo PlantaEnergia.java define la estructura de datos con atributos privados y finales para asegurar su inmutabilidad, conteniendo la clase interna PlantaEnergiaBuilder que asigna valores por defecto a las propiedades opcionales y proporciona la interfaz de encadenamiento. 

![imagen 1 metodo builder](assets/imagen8.png)

Por su parte, el archivo PlantaEnergiaService.java se encarga de la orquestación de negocio al recibir los valores de la petición, inicializar el builder con los campos requeridos, evaluar qué atributos opcionales fueron enviados y retornar la planta construida.

![imagen 2 metodo builder](assets/imagen9.png)

Finalmente, el archivo PlantaEnergiaController.java habilita el punto de entrada REST en la ruta /api/planta/crear con soporte para peticiones cruzadas mediante la anotación CrossOrigin. Este controlador recibe las solicitudes HTTP procesando los parámetros obligatorios y opcionales, delega la construcción al servicio de negocio y devuelve la respuesta en un objeto estructurado en formato JSON listo para el consumo del cliente

![imagen 3 metodo builder](assets/imagen10.png)
