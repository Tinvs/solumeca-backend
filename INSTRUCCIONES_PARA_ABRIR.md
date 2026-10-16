# SOLUMECA — código y guía para ejecutar

Este paquete contiene el código fuente del sitio web y del sistema de gestión de SOLUMECA. Hay dos maneras de revisarlo:

## Opción 1: Abrir las páginas HTML

1. Descomprime la carpeta del proyecto.
2. Abre `src/main/resources/static/index.html` con Chrome, Edge o Firefox.
3. Usa el menú de la página para navegar a las otras páginas `.html`.

Esta opción sirve para revisar el diseño y el contenido del sitio público. Las imágenes, estilos y scripts están incluidos en las carpetas `assets`, `css` y `js`.

**Importante:** abrir un HTML directamente no inicia el servidor. Por eso, el inicio de sesión, los paneles internos, la base de datos, el formulario de contacto y la gestión de maquinaria, repuestos y mantenimientos no estarán conectados. Esas funciones requieren la opción 2.

## Opción 2: Ejecutar el sistema completo

### Requisitos

- JDK **21**.
- MySQL Server.
- Apache Maven 3.8 o superior.
- Un computador con conexión a MySQL local.

El proyecto usa Java 21 según `pom.xml` y `Dockerfile`. El README original menciona Java 17; para esta versión del código, usa Java 21.

### Preparar la base de datos

1. Inicia MySQL.
2. Abre `database-schema.sql` en MySQL Workbench o phpMyAdmin y ejecútalo. El script crea `solumeca_db`, sus tablas e inserta usuarios iniciales de demostración.
3. Abre `src/main/resources/application.properties` y configura la URL, el usuario y la contraseña de MySQL para ese computador. No compartas contraseñas reales en el paquete.

Hibernate puede actualizar algunas tablas al iniciar la aplicación. Para un entorno real, revisa el esquema y configura credenciales seguras antes de usarlo.

### Iniciar la aplicación

Abre una terminal en la carpeta del proyecto, donde está `pom.xml`, y ejecuta:

```text
mvn spring-boot:run
```

Luego abre `http://localhost:8080`. Para detener el servidor, vuelve a la terminal y presiona `Ctrl+C`.

El proyecto no incluye Maven Wrapper (`mvnw`), así que Maven debe estar instalado y disponible como comando `mvn`.

### Ver el sitio desde otro dispositivo

Para abrir el sistema completo desde otro equipo o celular, ambos deben tener acceso a la misma red. Ejecuta la aplicación en el computador servidor, permite el puerto `8080` en el firewall y entra desde el otro dispositivo a:

```text
http://DIRECCION-IP-DEL-COMPUTADOR:8080
```

Sustituye `DIRECCION-IP-DEL-COMPUTADOR` por la dirección local del computador donde se ejecuta la aplicación. MySQL también debe estar disponible para la aplicación. No expongas el servidor a Internet sin configurar previamente seguridad, credenciales y permisos de red.

## Qué incluye el proyecto

- `src/main/resources/static/`: páginas públicas HTML, CSS, JavaScript e imágenes.
- `src/main/resources/templates/`: vistas y paneles internos generados con Thymeleaf.
- `src/main/java/`: backend Java y lógica de autenticación, mantenimiento e inventarios.
- `database-schema.sql`: creación de la base de datos y tablas iniciales.
- `pom.xml`: dependencias y configuración de compilación Maven.
- `Dockerfile`: instrucciones para construir una imagen de la aplicación.

El envío de correos del formulario de contacto requiere una cuenta SMTP configurada. Las funciones internas requieren iniciar la aplicación y conectarla a MySQL.
