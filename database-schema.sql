CREATE DATABASE IF NOT EXISTS solumeca_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE solumeca_db;

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS maquinaria (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    marca VARCHAR(100) NOT NULL,
    modelo VARCHAR(100),
    numero_serie VARCHAR(100) NOT NULL UNIQUE,
    estado VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS mantenimientos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    maquinaria_id BIGINT NOT NULL,
    solicitante VARCHAR(50) NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    descripcion VARCHAR(1000) NOT NULL,
    estado VARCHAR(30) NOT NULL,
    tecnico_asignado VARCHAR(50),
    fecha DATE NOT NULL,
    CONSTRAINT fk_mantenimiento_maquinaria
        FOREIGN KEY (maquinaria_id) REFERENCES maquinaria(id)
);

CREATE INDEX idx_mantenimientos_solicitante ON mantenimientos(solicitante);
CREATE INDEX idx_mantenimientos_estado ON mantenimientos(estado);

-- Contraseñas: gerente123, usuario123, tecnico123 y supervisor123.
-- CLIENTE es el rol interno del panel de usuario.
INSERT INTO usuarios (username, password, rol) VALUES
('gerente', '$2a$10$7sW2EiFQqsZTZr3tuORCTuZ4rlLXAclDf/CGP6yn6wlpcvjnbm6li', 'ADMIN'),
('usuario', '$2a$10$4/ClbfY/bsXqQwiHJ0vyz.vIDYr1KkxsJiTSrV4sn2mopRwMVpAw2', 'CLIENTE'),
('tecnico', '$2a$10$wDlRHwXXym8NF00Qf1rOc.d92Fnr6hWtBesNPmzpDT..hK3JQJQQK', 'TECNICO'),
('supervisor', '$2a$10$k4uT/G67P467MYnE5TGjMOGn2nT29fZKiShzGPiXoLG8rP6BmPUMi', 'SUPERVISOR')
ON DUPLICATE KEY UPDATE password = VALUES(password), rol = VALUES(rol);
