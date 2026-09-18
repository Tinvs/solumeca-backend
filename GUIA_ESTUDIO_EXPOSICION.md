# Guia de estudio para exponer SOLUMECA S.A.S.

## 1. Idea general del proyecto

SOLUMECA es una aplicacion web para una empresa de soluciones mecanicas, mantenimiento de maquinaria, obras civiles y proyectos metalmecanicos.

El proyecto tiene dos partes:

- **Sitio publico:** paginas informativas para mostrar la empresa, sus servicios, proyectos y datos de contacto.
- **Sistema interno:** registro de usuarios, inicio de sesion por roles, gestion de maquinaria, registro y seguimiento de mantenimientos, carga de evidencias e informes.

La tecnologia principal del backend es **Java 21 con Spring Boot 3.3.4**. El frontend publico utiliza HTML, CSS y JavaScript. La informacion persistente se guarda en MySQL.

Una forma corta de presentar el proyecto es:

> "Construimos una aplicacion web full stack. El usuario interactua con HTML, CSS y JavaScript; Spring Boot recibe las solicitudes, aplica seguridad y reglas de negocio; JPA/Hibernate conecta las clases Java con las tablas de MySQL."

---

## 2. Tecnologias utilizadas

### Backend

- **Java 21:** lenguaje principal.
- **Spring Boot:** crea y configura la aplicacion web.
- **Spring Web / Spring MVC:** recibe peticiones HTTP y dirige cada ruta a un controlador.
- **Spring Data JPA:** permite consultar y guardar datos usando interfaces Java.
- **Hibernate:** implementacion de JPA que traduce objetos Java a SQL.
- **Spring Security:** login, sesiones, roles y proteccion de rutas.
- **Thymeleaf:** genera paginas HTML dinamicas usando datos enviados desde Java.
- **Spring Validation:** valida los datos recibidos en el formulario de contacto.
- **Spring Mail:** envia los mensajes del formulario de contacto por correo.
- **MySQL:** base de datos relacional.
- **Maven:** administra dependencias y construye el proyecto.

### Frontend

- **HTML5:** estructura y contenido.
- **CSS3:** colores, tipografia, distribucion, responsive design, animaciones y estados visuales.
- **JavaScript:** interaccion, validacion del formulario, peticiones `fetch`, modales, galeria, menu movil y animaciones.
- **Thymeleaf:** se usa en las vistas internas que necesitan datos del servidor.

Las dependencias se declaran en `pom.xml`. Maven descarga las librerias y Spring Boot inicia la aplicacion con ellas.

---

## 3. Estructura del proyecto

```text
src/main/java/com/solumeca/
  SolumecaApplication.java       Punto de entrada
  config/                         Seguridad e inicializacion
  controller/                     Rutas y peticiones HTTP
  model/                          Entidades/tablas
  repository/                    Acceso a datos
  service/                       Logica de servicios

src/main/resources/
  application.properties         Conexion y configuracion
  static/                        HTML, CSS, JS, imagenes y videos
  templates/                     Vistas Thymeleaf

uploads/mantenimientos/          Archivos subidos por usuarios
 database-schema.sql             Estructura inicial de MySQL
```

Spring Boot sirve automaticamente los archivos de `src/main/resources/static`. Por eso `index.html`, `contacto.html`, `css/styles.css`, `js/script.js` e imagenes pueden abrirse desde el mismo servidor.

Las vistas que estan en `templates` no se abren como archivos estaticos directamente. Un controlador las prepara, Thymeleaf reemplaza las expresiones `th:*` y luego devuelve el HTML al navegador.

---

## 4. Como inicia Java la aplicacion

El archivo `SolumecaApplication.java` contiene:

```java
@SpringBootApplication
public class SolumecaApplication {
    public static void main(String[] args) {
        SpringApplication.run(SolumecaApplication.class, args);
    }
}
```

### Que significa cada parte

- `public class SolumecaApplication`: define la clase principal.
- `public static void main`: es el metodo que Java ejecuta primero.
- `@SpringBootApplication`: combina configuracion, auto-configuracion y escaneo de componentes.
- `SpringApplication.run(...)`: levanta el servidor embebido y crea el contexto de Spring.

Cuando la aplicacion inicia, Spring busca clases con anotaciones como `@Controller`, `@Service`, `@Repository`, `@Configuration` y `@Entity`. Esas clases se convierten en componentes administrados por Spring, llamados beans.

---

## 5. Conceptos de Java que debo saber explicar

### Clase y objeto

Una clase es una plantilla. Por ejemplo, `Maquinaria` define los datos de una maquina. Un objeto es una instancia concreta de esa clase, como una excavadora con marca, modelo y numero de serie.

### Atributos

Son los datos que tiene un objeto:

```java
private String nombre;
private String marca;
private String modelo;
```

El modificador `private` aplica encapsulamiento. El acceso se realiza mediante getters y setters.

### Metodos

Son comportamientos de una clase. Por ejemplo, `guardar`, `listar` y `eliminar` son metodos de los controladores.

### Inyeccion de dependencias

En vez de crear manualmente un repositorio con `new`, Spring lo crea y lo inyecta:

```java
@Autowired
private UsuarioRepository usuarioRepository;
```

Asi el controlador puede utilizar el repositorio sin encargarse de construirlo. En `ContactoController` tambien se usa inyeccion por constructor para recibir `JavaMailSender`.

### Interfaces

`UsuarioRepository` y `MantenimientoRepository` son interfaces. Al extender `JpaRepository`, reciben metodos como `save`, `findAll`, `findById` y `deleteById` sin escribir manualmente el SQL basico.

### Optional

`findByUsername` devuelve `Optional<Usuario>`. Esto expresa que puede existir o no existir un usuario:

```java
usuarioRepository.findByUsername(username)
    .orElseThrow(() -> new UsernameNotFoundException(...));
```

### Excepciones

Se usan para manejar errores, por ejemplo usuario inexistente, archivo no encontrado o fallo de correo. `try/catch` permite responder adecuadamente cuando una operacion falla.

### Record

El contacto usa un `record`:

```java
public record ContactRequest(
    @NotBlank String name,
    @NotBlank @Email String email,
    String phone,
    @NotBlank String message) {}
```

Es una clase compacta para transportar datos inmutables recibidos en JSON.

### Lambda y streams

Se usan para recorrer colecciones de forma declarativa. Por ejemplo, los archivos de evidencia se filtran, transforman y unen con `stream`, `filter`, `map` y `collect`.

### Enumerares y switch moderno

El `switch` de `RoleBasedAuthenticationSuccessHandler` decide el panel segun la autoridad del usuario y usa la sintaxis moderna `case ... ->`.

---

## 6. Modelo de capas

El flujo recomendado para entender el backend es:

```text
Navegador
   |
   | HTTP: GET, POST
   v
Controller
   |
   | aplica reglas y prepara datos
   v
Repository
   |
   | JPA/Hibernate genera SQL
   v
MySQL
```

En este proyecto algunos controladores realizan directamente la operacion sobre el repositorio. La clase `UsuarioDetailsService` funciona como servicio especializado para que Spring Security pueda cargar usuarios.

### Controller

Recibe solicitudes y devuelve:

- una vista, por ejemplo `admin-dashboard`;
- una redireccion, por ejemplo `redirect:/admin/maquinaria`;
- JSON, usando `@RestController`;
- un archivo, usando `ResponseEntity<Resource>`.

### Model

Representa los datos del dominio y las tablas. Las entidades son `Usuario`, `Maquinaria` y `Mantenimiento`.

### Repository

Es la puerta de acceso a MySQL. `JpaRepository<Entidad, Long>` significa que trabaja con una entidad y una clave primaria de tipo `Long`.

---

## 7. Entidades y relacion con MySQL

### Usuario

`Usuario.java` tiene `@Entity` y `@Table(name = "usuarios")`. Sus atributos se convierten en columnas:

- `id`: clave primaria autogenerada.
- `username`: obligatorio y unico.
- `email`: unico.
- `password`: obligatorio.
- `rol`: define los permisos del usuario.

`@Id` identifica la clave primaria y `@GeneratedValue(strategy = GenerationType.IDENTITY)` indica que MySQL genera el id automaticamente.

### Maquinaria

Representa la tabla `maquinaria`:

- nombre;
- marca;
- modelo;
- numero de serie;
- estado.

`numeroSerie` se enlaza con la columna SQL `numero_serie` mediante `@Column(name = "numero_serie")`.

### Mantenimiento

Representa una orden de mantenimiento:

- `maquinariaId`: maquina asociada;
- `solicitante`;
- `tipo`: preventivo, predictivo o correctivo;
- `descripcion`;
- `estado`;
- `tecnicoAsignado`;
- `fecha`;
- nombres de archivos de evidencia;
- nombre del informe.

`LocalDate` representa solo la fecha, no la hora.

En el esquema SQL `maquinaria_id` tiene una clave foranea hacia `maquinaria(id)`. En la clase Java se maneja como `Long maquinariaId`, no como una relacion JPA `@ManyToOne`. Por eso en la exposicion conviene decir: **la integridad de la relacion la declara MySQL mediante la clave foranea, mientras que el codigo Java guarda el id de la maquina**.

### JPA y ddl-auto

En `application.properties` aparece:

```properties
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

- `update` permite que Hibernate actualice la estructura compatible sin borrar los datos.
- `show-sql=true` muestra las consultas SQL en consola, util para aprender y depurar.

En un sistema real de produccion se suelen usar migraciones controladas y no depender solo de `update`.

---

## 8. Conexion con MySQL

La configuracion se encuentra en `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/solumeca_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

### Como se interpreta

- `localhost`: MySQL esta en el mismo computador.
- `3306`: puerto usual de MySQL.
- `solumeca_db`: nombre de la base de datos.
- `username` y `password`: credenciales de MySQL.
- `mysql-connector-j`: controlador JDBC que permite que Java hable con MySQL.

El archivo `database-schema.sql` crea la base, las tablas, indices, clave foranea y usuarios iniciales.

### Flujo de una consulta

1. El usuario solicita una pagina.
2. El controlador llama, por ejemplo, `maquinariaRepository.findAll()`.
3. Spring Data JPA interpreta la llamada.
4. Hibernate genera un `SELECT`.
5. MySQL devuelve los registros.
6. JPA convierte cada fila en un objeto Java.
7. El controlador agrega esos objetos al `Model`.
8. Thymeleaf los imprime en la tabla HTML.

### Metodos derivados

En `MantenimientoRepository`:

```java
List<Mantenimiento> findBySolicitanteOrderByFechaDesc(String solicitante);
List<Mantenimiento> findAllByOrderByFechaDesc();
```

Spring entiende el nombre del metodo. El primero filtra por solicitante y ordena por fecha descendente; el segundo trae todos ordenados de la fecha mas reciente a la mas antigua.

---

## 9. Seguridad, login y roles

### Rutas publicas y privadas

`SecurityConfig.java` define que rutas puede visitar cada usuario:

- publicas: inicio, paginas informativas, CSS, JavaScript, imagenes, registro, contacto y consulta de sesion;
- `/admin/**`: requiere rol `ADMIN`;
- `/cliente/**`: requiere rol `CLIENTE`;
- `/tecnico/**`: requiere rol `TECNICO`;
- `/supervisor/**`: requiere rol `SUPERVISOR`;
- `/encargado/**`: requiere rol `ENCARGADO`;
- mantenimientos: dependen del tipo de usuario.

`hasRole("ADMIN")` busca internamente una autoridad llamada `ROLE_ADMIN`.

### Como se autentica un usuario

1. El formulario de `login.html` envia `POST /login` con `username` y `password`.
2. Spring Security intercepta la peticion.
3. `UsuarioDetailsService` busca el usuario en `UsuarioRepository`.
4. `PasswordEncoder` compara la contraseña escrita con el hash BCrypt guardado.
5. Si coincide, Spring crea la sesion autenticada.
6. `RoleBasedAuthenticationSuccessHandler` lee el rol.
7. El usuario es enviado a su dashboard.

### BCrypt

Las contraseñas no se deben guardar como texto plano. `BCryptPasswordEncoder` convierte la contraseña en un hash. El hash no se descifra: se compara usando el encoder.

`DefaultUsersInitializer` crea usuarios de prueba al iniciar, solo si no existen, y codifica sus contraseñas antes de guardarlas.

### Sesiones

La configuracion usa sesion si es necesaria, migra la sesion despues del login para reducir riesgos de fijacion de sesion y permite una sesion maxima por usuario.

### CSRF

Los formularios Thymeleaf de operaciones internas incluyen el token CSRF oculto. Esto ayuda a evitar que otra pagina envie acciones no autorizadas usando una sesion existente.

### Redireccion por rol

El handler usa el primer authority:

```java
case "ROLE_ADMIN" -> "/admin/dashboard";
case "ROLE_CLIENTE" -> "/cliente/dashboard";
case "ROLE_TECNICO" -> "/tecnico/dashboard";
```

Esto permite que todos usen el mismo formulario de login, pero cada uno termine en su panel.

---

## 10. Registro de clientes

`RegistroController` maneja `GET /registro` y `POST /registro`.

Cuando llega el formulario:

1. limpia espacios del nombre;
2. convierte el correo a minusculas;
3. valida que el nombre tenga entre 3 y 50 caracteres;
4. valida el formato del correo con una expresion regular;
5. exige minimo 8 caracteres de contraseña;
6. verifica que no exista el username;
7. verifica que no exista el correo;
8. crea un `Usuario`;
9. guarda la contraseña usando BCrypt;
10. asigna automaticamente el rol `CLIENTE`;
11. guarda con `usuarioRepository.save(usuario)`;
12. redirige al login indicando que el registro fue exitoso.

El HTML muestra los errores leyendo el parametro `error` de la URL.

---

## 11. Sitio publico: HTML, CSS y JavaScript

### HTML

Las paginas publicas son:

- `index.html`: portada, video o imagen principal, propuesta de valor y botones.
- `nosotros.html`: informacion de la empresa.
- `servicios.html`: servicios y modales con mas informacion.
- `proyectos.html`: galeria de imagenes y videos.
- `contacto.html`: datos de contacto y formulario.
- `login.html`: acceso al sistema.
- `registro.html`: creacion de cuentas.

Se usa HTML semantico con `header`, `nav`, `main`, `section`, `footer`, formularios, labels y enlaces.

En `index.html`, el hero contiene video, imagen alternativa, texto principal y botones. La navegacion apunta a cada pagina. El footer centraliza enlaces, correo, telefono y ubicacion.

### CSS

`styles.css` define variables en `:root` para mantener una identidad visual consistente:

- colores oscuros tipo acero;
- naranja y amarillo de seguridad;
- fondos claros tipo concreto;
- tipografias Oswald, Inter e IBM Plex Mono;
- ancho maximo del contenido;
- radios y transiciones.

Tambien define estilos reutilizables para botones, navbar, tarjetas, formularios, tablas, paneles, modales y layouts responsive.

La interfaz se adapta a movil con media queries. El menu hamburguesa aparece en pantallas pequenas. `prefers-reduced-motion` reduce animaciones si el usuario lo solicita.

### JavaScript general

`script.js` espera `DOMContentLoaded` y comprueba si cada elemento existe antes de agregar eventos. Asi el mismo archivo puede cargarse en varias paginas sin fallar si una pagina no tiene, por ejemplo, galeria o formulario.

Funciones principales:

- selector visual de perfiles en el login;
- lectura de parametros de URL para mostrar errores o registro exitoso;
- carrusel de tarjetas;
- consulta de `/api/session` para cambiar el enlace de login por el usuario autenticado;
- barra de progreso de lectura;
- efecto parallax en imagenes;
- inclinacion 3D y spotlight en tarjetas;
- navbar que cambia al hacer scroll;
- resaltado de la pagina actual;
- menu hamburguesa para movil;
- aparicion progresiva usando `IntersectionObserver`;
- contadores animados con `requestAnimationFrame`;
- modales de servicios;
- lightbox de imagenes y videos;
- cierre con `Escape`;
- boton volver arriba;
- año actual automatico en el footer.

---

## 12. Formulario de contacto: frontend a Java

Este es un ejemplo importante para exponer la conexion entre capas.

### En el navegador

`contacto.html` contiene campos de nombre, correo, telefono y mensaje. `script.js` valida nombre, correo y mensaje antes de enviar.

Si todo es valido, JavaScript hace:

```javascript
fetch('/api/contacto', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ name, email, phone, message })
});
```

### En Java

`ContactoController` recibe el JSON en un `ContactRequest` con `@RequestBody`.

Las anotaciones `@NotBlank`, `@Email` y `@Size` validan los datos. `@Valid` activa esas validaciones.

Luego:

1. crea un `SimpleMailMessage`;
2. coloca como destinatario el correo configurado;
3. coloca el correo del visitante como respuesta;
4. arma asunto y contenido;
5. utiliza `JavaMailSender` para enviar;
6. devuelve JSON con mensaje de exito.

Si ocurre una excepcion, responde HTTP 503 con un mensaje de configuracion de correo.

Este flujo no guarda el contacto en MySQL: lo envia por correo.

---

## 13. Endpoint de sesion

`SessionController` expone `GET /api/session`.

- si no hay usuario autenticado, responde `{"authenticated": false}`;
- si hay usuario, responde autenticado, username y rol.

El JavaScript publico usa `fetch('/api/session')`. Si encuentra una sesion, cambia el enlace de "Iniciar sesion" por el nombre del usuario y lo dirige al dashboard correcto.

---

## 14. CRUD de maquinaria

`MaquinariaController` usa la ruta base `/admin/maquinaria`.

### Consultar

`GET /admin/maquinaria` llama `findAll`, agrega la lista al modelo y devuelve `admin/maquinaria-lista`.

### Registrar

1. `GET /admin/maquinaria/nueva` crea una `Maquinaria` vacia.
2. Thymeleaf muestra el formulario.
3. `POST /admin/maquinaria` recibe el objeto con `@ModelAttribute`.
4. se guarda con `maquinariaRepository.save(maquina)`.
5. se redirige al listado.

### Modificar

1. `GET /admin/maquinaria/{id}/editar` busca por id.
2. carga los datos actuales en el formulario.
3. `POST /admin/maquinaria/{id}` recibe los cambios.
4. fija el id y guarda el objeto.

### Eliminar

`POST /admin/maquinaria/{id}/eliminar` llama `deleteById` y vuelve al listado.

La autorizacion de `/admin/**` la aplica Spring Security, por lo que no depende solo de ocultar botones en HTML.

---

## 15. Mantenimientos y ordenes de trabajo

`MantenimientoController` usa `/mantenimientos`.

### Crear una orden operativa

Un usuario autorizado abre `/mantenimientos/nuevo`. El controlador carga las maquinas desde MySQL y las envia a Thymeleaf.

Al enviar el formulario:

1. recibe los campos con `@ModelAttribute`;
2. recibe evidencias con `MultipartFile[]`;
3. recibe un informe con `MultipartFile`;
4. coloca la fecha actual si falta;
5. coloca estado `Pendiente` si falta;
6. crea la carpeta de subidas;
7. guarda los archivos con nombre UUID;
8. guarda en la entidad los nombres generados;
9. guarda el mantenimiento en MySQL;
10. redirige al listado.

### Solicitud de cliente

Un cliente entra por `/mantenimientos/cliente/nueva`.

El servidor establece el solicitante usando `authentication.getName()`, fija estado `Pendiente` y fija la fecha actual. Esto es importante porque el nombre no se confia al formulario del navegador.

El cliente consulta solamente sus solicitudes en `/mantenimientos/cliente`, usando:

```java
findBySolicitanteOrderByFechaDesc(authentication.getName())
```

### Edicion operativa

Antes de editar, `exigirOperativo` comprueba que el rol sea ADMIN, SUPERVISOR, TECNICO o ENCARGADO. Si no, lanza `AccessDeniedException`.

### Archivos

Los archivos se guardan en `uploads/mantenimientos`, no dentro de la tabla como binarios. En MySQL se guardan los nombres.

Al construir el nombre se usa un UUID para evitar colisiones. Al descargar:

- se normaliza la ruta;
- se comprueba que no salga de la carpeta permitida;
- se revisa si el usuario esta autorizado;
- se comprueba si el archivo existe;
- se detecta el tipo MIME;
- PDF, DOC y DOCX se descargan como documentos;
- imagenes y otros tipos permitidos pueden mostrarse en linea.

La comprobacion `startsWith(base)` es una defensa contra traversal de rutas como `../archivo`.

---

## 16. Thymeleaf y vistas dinamicas

Una vista Thymeleaf tiene el namespace:

```html
<html xmlns:th="http://www.thymeleaf.org">
```

Ejemplos:

- `th:each="maquina : ${maquinas}"`: repite una opcion por cada maquina.
- `th:text="${m.estado}"`: imprime el estado.
- `th:if="${esOperativo}"`: muestra un elemento solo cuando la condicion es verdadera.
- `th:href`: construye enlaces con datos del servidor.
- `th:action`: decide la ruta del formulario.

La diferencia principal es:

- HTML en `static`: se sirve directamente.
- HTML en `templates`: lo procesa un controlador con datos.

---

## 17. Flujo completo para explicar en la exposicion

Puedes contar este caso de principio a fin:

1. El cliente abre `index.html` desde `http://localhost:8080`.
2. Spring Boot sirve HTML, CSS, JavaScript, imagenes y videos.
3. El cliente va a contacto y llena el formulario.
4. JavaScript valida los campos en el navegador.
5. `fetch` envia JSON a `POST /api/contacto`.
6. `ContactoController` recibe el JSON y valida con Jakarta Validation.
7. `JavaMailSender` envia el correo.
8. El backend devuelve una respuesta JSON.
9. JavaScript muestra el mensaje de exito o error.

Segundo caso:

1. Un usuario se registra en `/registro`.
2. Java valida datos y revisa duplicados en MySQL.
3. BCrypt cifra la contraseña.
4. Se guarda el usuario con rol `CLIENTE`.
5. El usuario inicia sesion en `/login`.
6. Spring Security consulta la base de datos.
7. Compara la contraseña con BCrypt.
8. Crea la sesion.
9. El handler lo redirige a `/cliente/dashboard`.
10. El cliente crea una solicitud de mantenimiento.
11. Java guarda la orden en MySQL y los archivos en disco.
12. El personal autorizado consulta y actualiza el estado.

---

## 18. Como ejecutar el proyecto para demostrarlo

Requisitos:

- JDK compatible con el proyecto. El `pom.xml` declara Java 21.
- Maven.
- MySQL o XAMPP activo.
- Base de datos `solumeca_db` creada.

Pasos:

1. Ejecutar `database-schema.sql` en MySQL o phpMyAdmin.
2. Revisar usuario, contraseña y puerto en `application.properties`.
3. Abrir una terminal en la carpeta del proyecto.
4. Ejecutar:

```bash
mvn spring-boot:run
```

5. Abrir `http://localhost:8080/index.html`.
6. Probar el contacto.
7. Registrar un cliente.
8. Iniciar sesion.
9. Probar la redireccion por rol.
10. Entrar al modulo de maquinaria o mantenimientos segun el rol.

Las credenciales iniciales estan documentadas en el esquema SQL y tambien se pueden crear mediante `DefaultUsersInitializer`. En una presentacion no conviene mostrar contraseñas reales ni dejar credenciales de desarrollo en produccion.

---

## 19. Preguntas que pueden hacer y respuestas

### ¿Por que usaste Java?

Porque Java es robusto, orientado a objetos, multiplataforma y tiene un ecosistema maduro para crear APIs, seguridad, acceso a datos y aplicaciones empresariales. Spring Boot permite organizar esas capacidades sin configurar todo desde cero.

### ¿Que hace Spring Boot?

Inicializa la aplicacion, configura el servidor embebido, detecta componentes y conecta las dependencias del proyecto.

### ¿Que diferencia hay entre Controller y Repository?

El controller atiende la peticion HTTP y decide la respuesta. El repository se comunica con la base de datos mediante JPA.

### ¿Por que no escribiste SQL para cada consulta?

Spring Data JPA permite definir interfaces y metodos derivados. Hibernate genera el SQL comun automaticamente. Esto reduce codigo repetido.

### ¿Como proteges las contraseñas?

Con BCrypt. Se guarda un hash, no la contraseña original.

### ¿Como sabes que un cliente no ve datos de otro?

La ruta `/mantenimientos/cliente` filtra usando el nombre del usuario autenticado y el repositorio busca por `solicitante`.

### ¿Como se controla el acceso por rol?

Spring Security restringe rutas con `hasRole` y el backend valida el usuario autenticado. Los botones ocultos en HTML son solo una ayuda visual; la seguridad real esta en el servidor.

### ¿Donde se guardan los archivos?

Los archivos se guardan en `uploads/mantenimientos`; la base de datos conserva sus nombres para poder localizarlos.

### ¿Que hace Thymeleaf?

Inserta datos que Java coloca en el `Model` dentro de una plantilla HTML y genera la respuesta final.

### ¿El formulario de contacto guarda los datos?

No. En este proyecto el contacto se valida y se envia por correo usando `JavaMailSender`.

### ¿Que pasa si falla el correo?

El controlador captura la excepcion y responde con HTTP 503 y un mensaje para revisar la configuracion.

### ¿Que patron de arquitectura se observa?

Una separacion por capas inspirada en MVC: modelos, controladores, repositorios y servicios/configuracion. El controlador recibe la peticion, el modelo representa datos y la vista muestra la respuesta.

---

## 20. Nuevas Funcionalidades y Despliegue en Produccion (Actualizacion 2026)

### A. Despliegue Cloud en Railway
- **URL Oficial en Produccion:** `https://solumeca.up.railway.app/`
- **Integracion Continua (CI/CD):** Cada `git push origin main` compila automaticamente en Railway mediante Maven con Java 21, levanta el contenedor de Spring Boot y lo conecta a la base de datos MySQL en la nube.

### B. Ciclo Completo: Presolicitud -> Analisis -> Orden de Trabajo -> Factura
1. **Presolicitud (Cliente):**
   - El cliente accede a su panel y crea una presolicitud en `/mantenimientos/cliente/nueva`.
   - Interfaz simplificada: no se le exige clasificar tipo de falla ni informe tecnico; solo selecciona su maquinaria, describe la necesidad y puede adjuntar evidencias (fotos/videos).
2. **Evaluacion Tecnica y Cotizacion (Supervisor / Gerente):**
   - Desde `/mantenimientos/{id}/analizar` registran diagnostico tecnico, solucion propuesta, cotizacion en pesos colombianos ($ COP), tiempo estimado en dias y tecnico asignado.
   - Pasa al estado `Presolicitud analizada`.
3. **Aprobacion Oficial:**
   - Al pulsar "Aprobar Orden", el sistema genera un correlativo oficial (ej. `ORD-2026-001`), fecha de aprobacion y pasa a `Orden de trabajo`.
4. **Factura Corporativa Imprimible:**
   - En `/mantenimientos/{id}/factura`: membrete formal de SOLUMECA S.A.S. (NIT, Barranquilla), desglose financiero, datos del equipo y boton de impresion directa / PDF.

### C. Codigo Unico de Maquinaria (`codigo`)
- Campo unico inventariado (`MQ-001` a `MQ-010`) en la entidad `Maquinaria`, facilitando la identificacion en tablas operativas y desplegables.

### D. Inventario y Control de Repuestos (`/repuestos`)
- Codigos correlativos (`REP-001` a `REP-010`), nombre, marca, proveedor, stock, stock minimo y precio unitario.
- **Alertas Visuales de Stock Critico:** Etiqueta pulsante cuando `cantidadStock <= stockMinimo`.
- **Ajuste Rapido en 1 Clic:** Botones interactivos `+` y `-` para entradas y salidas instantaneas de bodega.

### E. Directorio de Proveedores y Catalogo de Marcas
- Proveedores (`/admin/proveedores`): NIT, empresa, persona de contacto, telefono, correo y ciudad.
- Marcas (`/admin/marcas`): relacion `@ManyToOne` con el proveedor distribuidor de la marca.

### F. Dinamismo y UX en el Sitio Web
- **Buscador en Vivo en Tablas:** Filtrado instantaneo en JavaScript sin recargar la pagina en Mantenimientos, Maquinaria, Repuestos, Marcas y Proveedores.
- **Cotizador Instantaneo Interactivo:** En `servicios.html`, calcula rangos de precio en COP y duracion en dias segun equipo y servicio, enlazando directamente a WhatsApp.
- **Boton WhatsApp Flotante:** Compacto (44x44px), accesible en todas las vistas con tooltip y enlace directo al numero oficial (+57 300 246 4311).
- **Ver/Ocultar Contrasena con Ojito:** En `login.html` y `registro.html`, con iconos SVG adaptables y detector universal en `script.js`.
- **Formularios Legibles:** Etiquetas de entrada en color blanco puro (`#ffffff`, `font-weight: 700`) sobre el fondo oscuro industrial.

### G. Formulario de Contacto Conectado
- Ruta `/api/contacto` desbloqueada en CSRF.
- Persistencia automatica de prospectos en la tabla `mensajes_contacto` de MySQL.
- Despacho automatico de correo via SMTP a `solumeca.2@gmail.com`.

---

## 21. Credenciales de Prueba para la Exposicion

| Rol | Usuario | Contrasena | Enlace directo |
|---|---|---|---|
| **Gerente (ADMIN)** | `gerente` | `gerente123` | `/login.html` -> `/admin/dashboard` |
| **Supervisor** | `supervisor` | `supervisor123` | `/login.html` -> `/supervisor/dashboard` |
| **Tecnico** | `tecnico` | `tecnico123` | `/login.html` -> `/tecnico/dashboard` |
| **Cliente / Usuario** | `usuario` | `usuario123` | `/login.html` -> `/cliente/dashboard` |

---

## 22. Guion Paso a Paso para Demostracion en Vivo (10 Minutos)

1. **Introduccion (1 minuto):**
   - Presentar el sitio oficial en produccion: `https://solumeca.up.railway.app/`.
   - Destacar que es una solucion Full Stack (Java 21, Spring Boot 3, MySQL, Spring Security, Thymeleaf, JavaScript y CSS moderno).
2. **Sitio Publico y Contacto (2 minutos):**
   - Mostrar la landing, el cotizador interactivo en `servicios.html` y el boton flotante de WhatsApp.
   - Enviar un mensaje desde `contacto.html` y demostrar que se guarda de inmediato en la base de datos y despacha el correo.
3. **Inicio de Sesion con Seguridad (1 minuto):**
   - Mostrar el boton del ojito para revelar u ocultar la contrasena.
   - Explicar el cifrado seguro con BCrypt (las contrasenas nunca se guardan en texto plano).
4. **Flujo de Presolicitud como Cliente (2 minutos):**
   - Iniciar sesion con `usuario` / `usuario123`.
   - Crear una presolicitud: formulario limpio con etiquetas legibles en blanco y seleccion de maquinaria por codigo `[MQ-001]`.
   - Mostrar como queda registrada en estado `Presolicitud`.
5. **Evaluacion y Aprobacion como Supervisor / Gerente (2 minutos):**
   - Iniciar sesion con `gerente` / `gerente123` o `supervisor` / `supervisor123`.
   - Analizar la presolicitud: registrar diagnostico, presupuesto en COP, dias y tecnico.
   - Aprobar la orden: observar la conversion oficial a `ORD-2026-XXX`.
   - Abrir la **Factura Corporativa**, con membrete formal y boton de impresion / PDF.
6. **Modulos Administrativos (1 minuto):**
   - Mostrar el inventario de repuestos en `/repuestos`, el ajuste rapido con botones `+`/`-`, y la alerta de stock bajo.
   - Mostrar el catalogo de marcas y proveedores asociados.
   - Demostrar el buscador interactivo en vivo filtrando cualquier tabla.
7. **Cierre Tecnico (1 minuto):**
   - Explicar la arquitectura por capas y responder preguntas de los evaluadores.

---

## 23. Preguntas Tipicas de Evaluadores y Como Responderlas

### 1. ¿Por que eligieron Java y Spring Boot para este proyecto?
> *"Elegimos Java 21 y Spring Boot 3 porque es el estandar de la industria para aplicaciones empresariales robustas. Ofrece una arquitectura limpia por capas (Controller, Service, Repository, Model), gestion automatica de dependencias con Maven, seguridad robusta mediante Spring Security y persistencia de datos simplificada con JPA e Hibernate."*

### 2. ¿Como protegen las credenciales de los usuarios?
> *"Nunca guardamos contrasenas en texto plano. Usamos `BCryptPasswordEncoder`, que es un algoritmo criptografico de dispersion con sal (salt). Al iniciar sesion, Spring Security aplica la misma funcion hash y compara de forma segura. Ademas, las rutas estan protegidas con filtros de autorizacion basados en roles (`ROLE_ADMIN`, `ROLE_SUPERVISOR`, `ROLE_TECNICO`, `ROLE_CLIENTE`)."*

### 3. ¿Como funciona la conexion entre el formulario HTML y la base de datos?
> *"En el flujo Thymeleaf, el formulario envia un `POST` con token CSRF al `@Controller`. Spring mapea los datos a un objeto Java mediante anotaciones (`@ModelAttribute` o `@RequestBody`). El controlador aplica las validaciones de negocio y delega en una interfaz `@Repository` (Spring Data JPA), la cual Hibernate traduce automaticamente a sentencias SQL (`INSERT`, `UPDATE`) para persistir la informacion en MySQL."*

### 4. ¿Donde se guardan las fotos, videos e informes que suben los usuarios?
> *"Los metadatos y nombres de archivo unicos (generados con UUID para evitar colisiones) se guardan en la tabla `mantenimientos` de MySQL, mientras que los archivos fisicos binarios se almacenan de forma segura en el sistema de almacenamiento del servidor (`uploads/mantenimientos/`), servidos mediante endpoints controlados con validacion de autorizacion."*

### 5. ¿Como esta desplegado en produccion?
> *"La aplicacion esta desplegada en Railway mediante un contenedor en la nube. Esta vinculada directamente al repositorio Git en GitHub, de modo que cada actualizacion probada localmente se compila, empaqueta en `.jar` y se publica en vivo en `https://solumeca.up.railway.app/` de manera automatizada."*
