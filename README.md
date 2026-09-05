# Proyecto_Patrones
Los patrones de software son soluciones reutilizables a problemas comunes que aparecen al diseñar y desarrollar aplicaciones. No son código listo para copiar y pegar, sino guías o plantillas que ayudan a organizar el software de una manera más eficiente, mantenible y escalable.

con esto los patrones de software ayudan a mejorar la organización de código, resolver problemas recurrentes o que ocurren frecuentemente, con esto ayudando a facilitar el mantenimiento de nuestro sistema. Con esto se planean implementar patrones de software a el proyecto Smart Grid

Smart Grid es un proyecto el cual se enfoca en un sistema de gestión de redes inteligentes, cuyo código se ira actualizando con ayuda de los patrones de software para mejorar la calidad de código y organización.  

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

### Bibliografia basica:
https://profile.es/blog/patrones-de-diseno-de-software/
