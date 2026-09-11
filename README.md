# SOLUMECA S.A.S. — Backend Java (Spring Boot)

Proyecto Maven en Java que sirve el sitio de SOLUMECA y protege el acceso
del administrador con Spring Security + MySQL.

## Requisitos
- JDK 17
- Maven (o usar el `mvnw` incluido al abrir el proyecto en IntelliJ/Eclipse)
- MySQL corriendo en local

## 1. Crear la base de datos

Ejecuta en MySQL:

```sql
CREATE DATABASE solumeca_db;

USE solumeca_db;

CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL
);
```

## 2. Configurar la conexión

Edita `src/main/resources/application.properties` y cambia:

```
spring.datasource.username=root
spring.datasource.password=tu_password_mysql
```

por tus credenciales reales de MySQL.

## 3. Generar el hash de la contraseña del admin

Antes de insertar tu usuario admin, genera el hash BCrypt (Java no debe
guardar contraseñas en texto plano). El proyecto ya incluye la clase
`GenerarHash.java` en `src/main/java/com/solumeca/` lista para usar:

```java
package com.solumeca;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerarHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode("admin123"); // cambia por tu contraseña
        System.out.println(hash);
    }
}
```

Ejecuta su método `main` una sola vez (botón Run/▶ sobre la clase) y copia
el hash que imprime en la consola. Después de usarla puedes borrar este
archivo — es temporal, no forma parte del sitio ni del login final.

Copia el hash impreso e insértalo:

```sql
INSERT INTO usuarios (username, password, rol)
VALUES ('admin', '<pega_aqui_el_hash>', 'ADMIN');
```

## 4. Ejecutar el proyecto

```bash
./mvnw spring-boot:run
```

El sitio queda disponible en `http://localhost:8080`.

- `http://localhost:8080/index.html` → sitio público
- `http://localhost:8080/login.html` → login de administrador
- `http://localhost:8080/admin/dashboard` → solo accesible tras iniciar
  sesión con un usuario con rol `ADMIN`

## Estructura

```
src/main/java/com/solumeca/
 ├─ SolumecaApplication.java       (clase principal)
 ├─ config/SecurityConfig.java     (rutas públicas, login, roles)
 ├─ controller/AdminController.java
 ├─ model/Usuario.java
 ├─ repository/UsuarioRepository.java
 └─ service/UsuarioDetailsService.java

src/main/resources/
 ├─ application.properties
 ├─ static/                        (index.html, login.html, css/, js/, assets/)
 └─ templates/admin-dashboard.html (vista Thymeleaf tras login)
```

## Pendiente

Copia aquí dentro de `src/main/resources/static/` tus páginas
`nosotros.html`, `servicios.html`, `proyectos.html`, `contacto.html`,
la carpeta `js/` y las imágenes de `assets/images/` — ya quedan servidas
automáticamente por Spring Boot sin configuración extra.

## Roles del sistema

El sistema maneja 5 roles, cada uno con su propio panel al iniciar sesión:

| Rol en columna `rol` | Panel al iniciar sesión |
|---|---|
| `ADMIN` | `/admin/dashboard` |
| `CLIENTE` | `/cliente/dashboard` |
| `TECNICO` | `/tecnico/dashboard` |
| `SUPERVISOR` | `/supervisor/dashboard` |
| `ENCARGADO` | `/encargado/dashboard` |

Al iniciar sesión, `RoleBasedAuthenticationSuccessHandler` revisa el rol del
usuario y lo redirige automáticamente al panel correspondiente.

### Crear un usuario de prueba por cada rol

Usa `GenerarHash.java` para generar un hash por cada contraseña que quieras,
y luego inserta en MySQL, por ejemplo:

```sql
INSERT INTO usuarios (username, password, rol) VALUES
('cliente1',    '<hash>', 'CLIENTE'),
('tecnico1',    '<hash>', 'TECNICO'),
('supervisor1', '<hash>', 'SUPERVISOR'),
('encargado1',  '<hash>', 'ENCARGADO');
```

Cada panel (`cliente-dashboard.html`, `tecnico-dashboard.html`,
`supervisor-dashboard.html`, `encargado-dashboard.html`) ya lista las
funciones de ese rol como enlaces (`#`) — son la base visual; cada función
todavía necesita su lógica real (por ahora no hacen nada al hacer click).

## Tabla de maquinaria

Para que "Registrar / Modificar / Consultar maquinaria" funcione, crea esta
tabla en `solumeca_db` (phpMyAdmin → pestaña SQL):

```sql
CREATE TABLE maquinaria (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    marca VARCHAR(100) NOT NULL,
    modelo VARCHAR(100),
    numero_serie VARCHAR(100) UNIQUE NOT NULL,
    estado VARCHAR(50) NOT NULL
);
```

Rutas activas:
- `GET /admin/maquinaria` — listado (Admin, con editar/eliminar)
- `GET /admin/maquinaria/nueva` — formulario de registro (Admin)
- `GET /admin/maquinaria/{id}/editar` — formulario de edición (Admin)
- `GET /maquinaria` — listado de solo lectura (Técnico, Supervisor, Encargado)
