CREATE DATABASE IF NOT EXISTS solumeca_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE solumeca_db;

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(150) UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS proveedores (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    nit VARCHAR(50) NOT NULL UNIQUE,
    contacto VARCHAR(100),
    telefono VARCHAR(50),
    email VARCHAR(150),
    direccion VARCHAR(200),
    ciudad VARCHAR(100),
    categoria VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS marcas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    pais_origen VARCHAR(100),
    descripcion VARCHAR(500),
    proveedor_id BIGINT,
    CONSTRAINT fk_marcas_proveedor FOREIGN KEY (proveedor_id) REFERENCES proveedores(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS maquinaria (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    marca VARCHAR(100) NOT NULL,
    modelo VARCHAR(100),
    numero_serie VARCHAR(100) NOT NULL UNIQUE,
    estado VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS repuestos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(150) NOT NULL,
    descripcion VARCHAR(500),
    marca VARCHAR(100),
    proveedor_id BIGINT,
    cantidad_stock INT NOT NULL DEFAULT 0,
    stock_minimo INT NOT NULL DEFAULT 5,
    precio_unitario DOUBLE DEFAULT 0.0,
    ubicacion VARCHAR(100),
    CONSTRAINT fk_repuestos_proveedor FOREIGN KEY (proveedor_id) REFERENCES proveedores(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS mantenimientos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    maquinaria_id BIGINT NOT NULL,
    solicitante VARCHAR(50) NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    descripcion VARCHAR(1000) NOT NULL,
    estado VARCHAR(50) NOT NULL,
    tecnico_asignado VARCHAR(50),
    fecha DATE NOT NULL,
    archivos_evidencia VARCHAR(4000),
    informe_archivo VARCHAR(500),
    analisis VARCHAR(1000),
    solucion VARCHAR(1000),
    costo_estimado DOUBLE,
    dias_estimados INT,
    valor_total DOUBLE,
    numero_orden VARCHAR(50),
    fecha_aprobacion DATE,
    motivo_rechazo VARCHAR(1000),
    CONSTRAINT fk_mantenimiento_maquinaria
        FOREIGN KEY (maquinaria_id) REFERENCES maquinaria(id)
);

CREATE INDEX idx_mantenimientos_solicitante ON mantenimientos(solicitante);
CREATE INDEX idx_mantenimientos_estado ON mantenimientos(estado);
CREATE INDEX idx_maquinaria_codigo ON maquinaria(codigo);
CREATE INDEX idx_repuestos_codigo ON repuestos(codigo);

-- Usuarios iniciales: gerente123, usuario123, tecnico123, supervisor123, encargado123.
INSERT INTO usuarios (username, password, rol) VALUES
('gerente', '$2a$10$7sW2EiFQqsZTZr3tuORCTuZ4rlLXAclDf/CGP6yn6wlpcvjnbm6li', 'ADMIN'),
('usuario', '$2a$10$4/ClbfY/bsXqQwiHJ0vyz.vIDYr1KkxsJiTSrV4sn2mopRwMVpAw2', 'CLIENTE'),
('tecnico', '$2a$10$wDlRHwXXym8NF00Qf1rOc.d92Fnr6hWtBesNPmzpDT..hK3JQJQQK', 'TECNICO'),
('supervisor', '$2a$10$k4uT/G67P467MYnE5TGjMOGn2nT29fZKiShzGPiXoLG8rP6BmPUMi', 'SUPERVISOR'),
('encargado', '$2a$10$p0b380N0x10b7b12Xl65cOO3k1q5zPZ4oQh4eJtUuN3Yv4z8K7s1K', 'ENCARGADO')
ON DUPLICATE KEY UPDATE password = VALUES(password), rol = VALUES(rol);
