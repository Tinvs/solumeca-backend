# GUÍA OFICIAL DE PRESENTACIÓN Y DEFENSA — SOLUMECA S.A.S.

**Proyecto:** Plataforma Web Full Stack de Gestión Operativa y Mantenimiento Industrial  
**Dominio Oficial en Vivo:** https://solumeca.up.railway.app/  
**Tecnologías:** Java 21 · Spring Boot 3.3.4 · MySQL · Spring Security · Thymeleaf · JavaScript · CSS3 · Railway Cloud  

---

## 1. RESUMEN EJECUTIVO DEL PROYECTO

SOLUMECA S.A.S. es una plataforma web desarrollada para digitalizar, optimizar y controlar los servicios de mantenimiento de maquinaria pesada, montacargas y obras civiles en la Costa Caribe.

### Problema que resuelve
Anteriormente, las cotizaciones, solicitudes de clientes, diagnósticos técnicos y control de existencias de repuestos se gestionaban de forma manual, verbal o en planillas desconectadas.

### Solución implementada
Un ecosistema web integral en la nube que articula a cuatro actores clave:
1. **Clientes:** Solicitan servicios (presolicitudes), hacen seguimiento en vivo, aprueban cotizaciones y descargan facturas.
2. **Técnicos:** Disponen de un panel operativo puro para documentar intervenciones técnicas sin cargas administrativas.
3. **Supervisores:** Evalúan fallas, estructuran cotizaciones en $ COP con tiempos estimados y asignan técnicos.
4. **Gerente (Administrador):** Acceso integral con control de maquinaria inventariada, catálogo de marcas, directorio de proveedores, inventario de repuestos y reportes operativos.

---

## 2. CREDENCIALES DE PRUEBA PARA LA DEMO EN VIVO

| Rol del Sistema | Usuario | Contraseña | ¿Qué funciones desempeña? |
|---|---|---|---|
| **Cliente / Usuario** | `usuario` | `usuario123` | Registra presolicitudes, aprueba órdenes de trabajo, descarga facturas, soporte WhatsApp. |
| **Supervisor** | `supervisor` | `supervisor123` | Diagnóstico técnico, cotización en COP y días, asignación técnica, control de repuestos. |
| **Gerente (ADMIN)** | `gerente` | `gerente123` | Control total del sistema, proveedores, marcas, maquinaria, repuestos y reportes. |
| **Técnico** | `tecnico` | `tecnico123` | Vista puramente operativa para documentar órdenes de trabajo. |

---

## 3. GUIÓN DE EXPOSICIÓN PASO A PASO (8 A 10 MINUTOS)

### Paso 1: Introducción (1 minuto)
* **Qué decir:**
  > "Buenos días. Presentamos la plataforma web oficial de SOLUMECA S.A.S., desarrollada en Java 21 y Spring Boot 3 con base de datos MySQL y desplegada en la nube en Railway. Esta plataforma resuelve la necesidad de conectar en tiempo real a los clientes con el personal operativo y la gerencia para la gestión integral de maquinaria pesada y servicios técnicos."
* **Qué mostrar:** Abrir en el navegador https://solumeca.up.railway.app/.

### Paso 2: Sitio Web Público, Cotizador y WhatsApp (2 minutos)
* **Qué mostrar:**
  1. Navegar a **Servicios** (`/servicios.html`) y bajar al **Cotizador Rápido Interactivo**:
     - Seleccionar un equipo (ej. *Retroexcavadora*) y un mantenimiento (ej. *Mantenimiento Preventivo*).
     - Mostrar cómo JavaScript calcula en tiempo real el presupuesto estimado en pesos colombianos ($ COP) y días requeridos.
     - Mostrar cómo el botón genera dinámicamente el mensaje predeterminado hacia WhatsApp.
  2. Mostrar el **Botón Flotante de WhatsApp**: compacto (44x44px), discreto, con tooltip informativo y enlace directo al soporte oficial (+57 300 246 4311).
  3. Ir a **Contáctenos** (`/contacto.html`):
     - Enviar un mensaje de prueba.
     - **Argumento técnico:** *"El formulario valida campos en frontend y backend, almacena el prospecto en la tabla `mensajes_contacto` de MySQL y despacha el correo electrónico vía SMTP con Spring Mail."*

### Paso 3: Login Seguro con Botón de Ojito (1 minuto)
* **Qué mostrar:** Ir a **Iniciar Sesión** (`/login.html`).
  1. Escribir caracteres en la contraseña y pulsar el **botón del ojito**: mostrar la alternancia fluida entre ocultar y ver los caracteres mediante SVG e interactividad limpia.
  2. **Argumento técnico:** *"La autenticación usa Spring Security. Las contraseñas se almacenan con el algoritmo criptográfico `BCryptPasswordEncoder`, que genera un hash irreversible con sal (salt). Además, contamos con protección contra sesiones simultáneas y control estricto de roles."*

### Paso 4: Experiencia del Cliente — Crear Presolicitud (2 minutos)
* **Qué hacer:** Iniciar sesión como `usuario` / `usuario123`.
* **Qué mostrar:**
  1. Panel del cliente adaptado con sus opciones operativas y soporte.
  2. Clic en **03. Crear nueva presolicitud técnica** (`/mantenimientos/cliente/nueva`):
     - Destacar las **etiquetas en color blanco puro (`#ffffff`)** con alto contraste sobre el fondo industrial oscuro.
     - Mostrar la simplificación del formulario: el cliente solo escoge su equipo con código inventariado (ej. `[MQ-001] Retroexcavadora 420F2`), describe la falla y sube fotos/videos de evidencia.
     - Guardar la presolicitud: se registra en estado `Presolicitud`.
  3. Cerrar sesión.

### Paso 5: Experiencia del Supervisor / Gerente — Análisis y Facturación (2.5 minutos)
* **Qué hacer:** Iniciar sesión como `gerente` / `gerente123` (o `supervisor`).
* **Qué mostrar:**
  1. Panel con las 9 opciones completas.
  2. Ir a **Revisar operaciones / Mantenimientos**:
     - Demostrar el **buscador en vivo en la tabla**: filtrar al instante por código de máquina o estado sin recargar la página.
     - Ubicar la presolicitud y pulsar **🔍 Analizar y Cotizar**:
       - Registrar: Diagnóstico (ej. *Fuga en circuito hidráulico principal*), Solución (ej. *Sustitución de manguera y empaques*), Costo (ej. `$1,800,000 COP`), Días (ej. `2`) y Técnico (ej. `tecnico`).
       - Guardar: el estado cambia a `Cotizada / Analizada`.
  3. Demostrar la **Aprobación Oficial**:
     - Pulsar **✓ Aprobar Orden**: el sistema asigna el correlativo oficial (ej. `ORD-2026-001`), fija fecha de aprobación y pasa a `Orden de trabajo`.
  4. Pulsar **📄 Factura / Orden**:
     - Mostrar la **Factura Corporativa** formal con membrete corporativo de SOLUMECA S.A.S. (NIT, Barranquilla, desglose financiero, datos del equipo y valor total).
     - Probar el botón **Imprimir / Guardar en PDF** (`window.print()`).

### Paso 6: Módulos de Repuestos, Proveedores y Marcas (1.5 minutos)
* **Qué mostrar:**
  1. **Stock de Repuestos** (`/repuestos`):
     - Alertas visuales dinámicas de **⚠️ Bajo stock** cuando `cantidadStock <= stockMinimo`.
     - Botones de ajuste rápido en 1 clic (`+` y `-`) para registrar entradas y salidas de bodega.
  2. **Catálogo de Marcas y Proveedores** (`/admin/marcas` y `/admin/proveedores`):
     - Explicar la relación relacional `@ManyToOne`: cada marca está asociada a su proveedor distribuidor regional con NIT y datos de contacto.

### Paso 7: Cierre Técnico (30 segundos)
* **Frase final:**
  > "SOLUMECA S.A.S. demuestra cómo una arquitectura empresarial en Java y Spring Boot proporciona trazabilidad completa, seguridad con estándares de la industria y disponibilidad 24/7 en la nube para el sector del mantenimiento industrial."

---

## 4. PREGUNTAS CLAVE DE EVALUADORES Y CÓMO DEFENDERLAS

### P1: ¿Cuál es el patrón de arquitectura del sistema?
**Respuesta:**
> "El backend implementa el patrón por capas MVC (Modelo-Vista-Controlador):
> - **Capa de Modelo (`com.solumeca.model`):** Clases entidad anotadas con JPA/Hibernate que representan las tablas en MySQL (`Maquinaria`, `Mantenimiento`, `Repuesto`, `Proveedor`, `Marca`, `MensajeContacto`).
> - **Capa de Repositorio (`com.solumeca.repository`):** Interfaces que extienden `JpaRepository`, permitiendo consultas optimizadas sin escribir SQL manual.
> - **Capa de Controlador (`com.solumeca.controller`):** Controladores Spring MVC y REST que gestionan las peticiones HTTP, validan los datos y devuelven respuestas JSON o vistas Thymeleaf.
> - **Capa de Configuración y Seguridad (`com.solumeca.config`):** Gestiona la cadena de filtros de Spring Security, codificación de contraseñas y datos iniciales."

### P2: ¿Cómo garantizan la seguridad de las contraseñas y rutas?
**Respuesta:**
> "Las contraseñas se almacenan mediante `BCryptPasswordEncoder`, que aplica una función hash criptográfica irreversible con salt. Las rutas están segmentadas mediante `SecurityFilterChain` con directivas `hasRole()` y `hasAnyRole()`, garantizando que un cliente no pueda ingresar a paneles administrativos ni un técnico a módulos de inventario gerencial. Además, las transacciones Thymeleaf están blindadas con tokens CSRF."

### P3: ¿Cómo funciona la relación entre Marcas y Proveedores en la base de datos?
**Respuesta:**
> "Se implementó integridad referencial relacional mediante JPA. La entidad `Marca` tiene una anotación `@ManyToOne` hacia `Proveedor` con una clave foránea `proveedor_id` y restricción `ON DELETE SET NULL`. Esto modela fielmente la realidad empresarial: una empresa proveedora puede distribuir múltiples marcas de maquinaria."

### P4: ¿Cómo manejan los archivos adjuntos (fotos/videos/informes)?
**Respuesta:**
> "Los binarios de archivos no se guardan como BLOB dentro de MySQL para no degradar el rendimiento de la base de datos. Se almacenan físicamente en el servidor bajo el directorio `uploads/mantenimientos/`, asignándoles un identificador único mediante `UUID.randomUUID()` para evitar colisiones de nombres. En MySQL solo se guarda la referencia del nombre, y la descarga se sirve mediante un endpoint protegido que verifica que el usuario solicitante o un rol operativo tenga autorización para consultarlo."

### P5: ¿Cómo está estructurado el despliegue en la nube?
**Respuesta:**
> "El proyecto opera en Railway vinculado al repositorio Git de GitHub. Cada cambio enviado a la rama `main` activa un pipeline automatizado de Integración Continua (CI/CD) que compila con Maven en Java 21, ejecuta las pruebas unitarias y levanta el contenedor productivo en el dominio público `https://solumeca.up.railway.app/`, conectado al servicio administrado de MySQL."

---

## 5. RESUMEN DE CAMBIOS Y MEJORAS REALIZADAS EN LA SESIÓN

1. **Botón flotante de WhatsApp:** Redimensionado a formato discreto (44x44px), sin animaciones invasivas, con tooltip interactivo y enlace directo.
2. **Ciclo Completo de Presolicitud a Facturación:** Flujo formal con análisis técnico, cotización en COP, asignación de técnico, aprobación a orden de trabajo (`ORD-2026-XXX`) y factura corporativa imprimible con membrete.
3. **Códigos de Maquinaria:** Identificador único (`MQ-001` a `MQ-010`) en base de datos y desplegables.
4. **Inventario de Repuestos:** Códigos (`REP-001` a `REP-010`), alertas de stock bajo y botones de ajuste rápido `+`/`-`.
5. **Directorio de Proveedores y Marcas:** Relación relacional completa con NIT, contactos y ciudades.
6. **Buscador en Vivo en Tablas:** Filtrado instantáneo en tiempo real sin recargar pantalla.
7. **Cotizador Interactivo Dinámico:** Cálculo automático de tarifas y duraciones en `servicios.html`.
8. **Dashboards Segmentados:** Panel de Técnico enfocado exclusivamente en acciones de taller; panel de Gerente y Supervisor con herramientas administrativas completas.
9. **Botón del Ojito para Contraseñas:** Alternancia dinámica de visibilidad en login y registro con SVG e inicializador universal en `script.js`.
10. **Diseño y Legibilidad:** Título formal *"Presolicitud"* y etiquetas de formulario en color blanco puro (`#ffffff`) sobre el panel industrial.
11. **Módulo de Contáctenos Conectado:** Exención de CSRF en Spring Security, almacenamiento automático de prospectos en MySQL (`mensajes_contacto`) y despacho por correo electrónico SMTP.

