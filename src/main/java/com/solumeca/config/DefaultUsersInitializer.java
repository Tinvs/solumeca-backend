package com.solumeca.config;

import com.solumeca.model.*;
import com.solumeca.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.*;

@Configuration
public class DefaultUsersInitializer {

    @Bean
    CommandLineRunner initializeDefaultData(UsuarioRepository usuarioRepository,
                                           PasswordEncoder passwordEncoder,
                                           ProveedorRepository proveedorRepository,
                                           MarcaRepository marcaRepository,
                                           MaquinariaRepository maquinariaRepository,
                                           RepuestoRepository repuestoRepository,
                                           MantenimientoRepository mantenimientoRepository) {
        return args -> {
            // 1. Usuarios
            createIfMissing(usuarioRepository, passwordEncoder, "gerente", "gerente123", "ADMIN");
            createIfMissing(usuarioRepository, passwordEncoder, "usuario", "usuario123", "CLIENTE");
            createIfMissing(usuarioRepository, passwordEncoder, "tecnico", "tecnico123", "TECNICO");
            createIfMissing(usuarioRepository, passwordEncoder, "supervisor", "supervisor123", "SUPERVISOR");
            createIfMissing(usuarioRepository, passwordEncoder, "encargado", "encargado123", "ENCARGADO");

            // 2. Proveedores con NIT y líneas telefónicas verificadas
            Proveedor provDiesel = seedProveedorIfMissing(proveedorRepository, "Distribuidora Diésel del Caribe S.A.S.",
                    "901.284.716-1", "Carlos Méndez", "+57 310 354 4394", "ventas@dieseldelcaribe.com",
                    "Calle 30 # 40-15", "Barranquilla", "Motores y Repuestos Diésel");

            Proveedor provPartes = seedProveedorIfMissing(proveedorRepository, "Partes y Equipos de la Costa",
                    "802.014.892-3", "Mariana Gómez", "+57 317 670 7071", "contacto@partesdelacosta.com",
                    "Carrera 53 # 74-20", "Barranquilla", "Maquinaria Pesada e Hidráulica");

            Proveedor provMontacargas = seedProveedorIfMissing(proveedorRepository, "Maquinaria & Montacargas S.A.S.",
                    "900.541.638-4", "Jorge E. Suárez", "+57 311 415 6001", "jorge.suarez@montacargassas.co",
                    "Vía 40 # 85-220", "Barranquilla", "Montacargas y Elevadores");

            Proveedor provSellos = seedProveedorIfMissing(proveedorRepository, "Hidráulicos & Sellos Industriales",
                    "806.012.749-3", "Andrés Valencia", "+57 314 295 4816", "info@selloshidraulicos.com",
                    "Zona Industrial Mamonal Km 3", "Cartagena", "Sellos, Válvulas y Mangueras");

            // 3. Marcas asociadas a proveedores
            Marca marcaCat = seedMarcaIfMissing(marcaRepository, "CATERPILLAR", "Estados Unidos", "Líder mundial en maquinaria pesada y motores industriales", provDiesel);
            Marca marcaToyota = seedMarcaIfMissing(marcaRepository, "TOYOTA", "Japón", "Especialista global en montacargas de combustión y eléctricos", provMontacargas);
            Marca marcaKomatsu = seedMarcaIfMissing(marcaRepository, "KOMATSU", "Japón", "Equipos pesados de excavación, cargadores y minería", provPartes);
            Marca marcaBobcat = seedMarcaIfMissing(marcaRepository, "BOBCAT", "Estados Unidos", "Líder en minicargadores compactos y aditamentos", provDiesel);
            Marca marcaYale = seedMarcaIfMissing(marcaRepository, "YALE", "Estados Unidos", "Equipos para manejo de materiales y montacargas", provMontacargas);
            Marca marcaGenie = seedMarcaIfMissing(marcaRepository, "GENIE", "Estados Unidos", "Plataformas de elevación y brazos articulados", provPartes);
            Marca marcaClark = seedMarcaIfMissing(marcaRepository, "CLARK", "Estados Unidos", "Montacargas industriales diésel y GLP", provMontacargas);
            Marca marcaRaymond = seedMarcaIfMissing(marcaRepository, "RAYMOND", "Estados Unidos", "Montacargas eléctricos para bodegas y pasillo angosto", provMontacargas);
            seedMarcaIfMissing(marcaRepository, "NISSAN", "Japón", "Montacargas y equipos de manejo logístico", provMontacargas);
            seedMarcaIfMissing(marcaRepository, "LINDE", "Alemania", "Tecnología europea en equipos de manutención", provMontacargas);

            // 4. 10 Maquinarias con códigos únicos (MQ-001 a MQ-010)
            seedMaquinariaIfMissing(maquinariaRepository, "MQ-001", "Retroexcavadora 420F2", "CATERPILLAR", "420F2", "CAT420F2-0091", "Operativa");
            seedMaquinariaIfMissing(maquinariaRepository, "MQ-002", "Montacargas 8FGU25", "TOYOTA", "8FGU25", "TOY8FGU25-4421", "Operativa");
            seedMaquinariaIfMissing(maquinariaRepository, "MQ-003", "Excavadora Hidráulica PC200", "KOMATSU", "PC200-8", "KOMPC200-1120", "En mantenimiento");
            seedMaquinariaIfMissing(maquinariaRepository, "MQ-004", "Minicargador S570", "BOBCAT", "S570", "BOBS570-8832", "Operativa");
            seedMaquinariaIfMissing(maquinariaRepository, "MQ-005", "Montacargas Combustión GLP", "YALE", "GLP050VX", "YALGLP050-9923", "Operativa");
            seedMaquinariaIfMissing(maquinariaRepository, "MQ-006", "Cargador Frontal WA320", "KOMATSU", "WA320-6", "KOMWA320-5501", "Fuera de servicio");
            seedMaquinariaIfMissing(maquinariaRepository, "MQ-007", "Montacargas Eléctrico 7500", "RAYMOND", "7500", "RAY7500-6612", "Operativa");
            seedMaquinariaIfMissing(maquinariaRepository, "MQ-008", "Minicargador de Orugas T770", "BOBCAT", "T770", "BOBT770-3344", "En mantenimiento");
            seedMaquinariaIfMissing(maquinariaRepository, "MQ-009", "Plataforma Articulada Z-45", "GENIE", "Z-45/25", "GENZ45-7789", "Operativa");
            seedMaquinariaIfMissing(maquinariaRepository, "MQ-010", "Montacargas Diésel C30D", "CLARK", "C30D", "CLKC30D-2219", "Operativa");

            // 5. 10 Repuestos con código, stock, stock mínimo y precio
            seedRepuestoIfMissing(repuestoRepository, "REP-001", "Filtro de Aceite Hidráulico", "Filtro para línea de retorno de alta presión", "CATERPILLAR", provDiesel, 15, 5, 145000.0, "Bodega A - Estante 1");
            seedRepuestoIfMissing(repuestoRepository, "REP-002", "Filtro de Combustible Primario", "Separador de agua y sedimentos 30 micras", "KOMATSU", provPartes, 18, 6, 95000.0, "Bodega A - Estante 2");
            seedRepuestoIfMissing(repuestoRepository, "REP-003", "Kit Sellos Pistón Elevación", "Sellos de uretano y vitón para vástago 50mm", "TOYOTA", provSellos, 3, 5, 280000.0, "Bodega B - Cajón 4"); // BAJO STOCK
            seedRepuestoIfMissing(repuestoRepository, "REP-004", "Bomba de Inyección Diésel", "Bomba de alta presión sistema Common Rail", "CATERPILLAR", provDiesel, 2, 2, 3450000.0, "Bodega A - Zona Segura"); // LIMITE STOCK
            seedRepuestoIfMissing(repuestoRepository, "REP-005", "Correa de Transmisión y Alternador", "Banda estriada Poly-V resistente a temperatura", "BOBCAT", provDiesel, 12, 4, 85000.0, "Bodega A - Estante 3");
            seedRepuestoIfMissing(repuestoRepository, "REP-006", "Manguera Hidráulica 3/4\" R2", "Manguera 2 mallas de acero 4000 PSI", "GENÉRICA", provSellos, 25, 8, 120000.0, "Bodega B - Rollo 1");
            seedRepuestoIfMissing(repuestoRepository, "REP-007", "Zapatas / Pastillas de Freno", "Juego completo de frenos para eje delantero", "YALE", provMontacargas, 4, 6, 220000.0, "Bodega B - Estante 1"); // BAJO STOCK
            seedRepuestoIfMissing(repuestoRepository, "REP-008", "Rodamiento de Mástil Principal", "Rodamiento sellado reforzado para mástil tríplex", "CLARK", provMontacargas, 8, 4, 310000.0, "Bodega B - Estante 2");
            seedRepuestoIfMissing(repuestoRepository, "REP-009", "Celda Batería Traccionaria 2V", "Elemento de ácido-plomo 48V 620Ah", "RAYMOND", provMontacargas, 1, 2, 950000.0, "Bodega C - Eléctrico"); // BAJO STOCK
            seedRepuestoIfMissing(repuestoRepository, "REP-010", "Válvula Solenoide de Control", "Válvula proporcional direccional 24V", "GENIE", provPartes, 6, 3, 650000.0, "Bodega B - Cajón 2");

            // 6. Mantenimientos de prueba en diversos estados
            seedMantenimientosIfEmpty(mantenimientoRepository, maquinariaRepository);
        };
    }

    private void createIfMissing(UsuarioRepository usuarioRepository,
                                 PasswordEncoder passwordEncoder,
                                 String username,
                                 String password,
                                 String role) {
        if (usuarioRepository.findByUsername(username).isPresent()) {
            return;
        }
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setRol(role);
        usuarioRepository.save(usuario);
    }

    private Proveedor seedProveedorIfMissing(ProveedorRepository repo, String nombre, String nit, String contacto,
                                             String telefono, String email, String direccion, String ciudad, String categoria) {
        Proveedor p = repo.findByNombreIgnoreCase(nombre)
                .or(() -> repo.findByNit(nit))
                .orElseGet(() -> new Proveedor(nombre, nit, contacto, telefono, email, direccion, ciudad, categoria));
        p.setNombre(nombre);
        p.setNit(nit);
        p.setContacto(contacto);
        p.setTelefono(telefono);
        p.setEmail(email);
        p.setDireccion(direccion);
        p.setCiudad(ciudad);
        p.setCategoria(categoria);
        return repo.save(p);
    }

    private Marca seedMarcaIfMissing(MarcaRepository repo, String nombre, String pais, String desc, Proveedor prov) {
        return repo.findByNombreIgnoreCase(nombre).orElseGet(() -> {
            Marca m = new Marca(nombre, pais, desc, prov);
            return repo.save(m);
        });
    }

    private void seedMaquinariaIfMissing(MaquinariaRepository repo, String codigo, String nombre, String marca,
                                         String modelo, String numeroSerie, String estado) {
        if (!repo.existsByCodigo(codigo)) {
            Maquinaria m = new Maquinaria(codigo, nombre, marca, modelo, numeroSerie, estado);
            repo.save(m);
        }
    }

    private void seedRepuestoIfMissing(RepuestoRepository repo, String codigo, String nombre, String desc,
                                       String marca, Proveedor prov, int stock, int stockMin, double precio, String ubicacion) {
        if (!repo.existsByCodigo(codigo)) {
            Repuesto r = new Repuesto(codigo, nombre, desc, marca, prov, stock, stockMin, precio, ubicacion);
            repo.save(r);
        }
    }

    private void seedMantenimientosIfEmpty(MantenimientoRepository repo, MaquinariaRepository maquinaRepo) {
        if (repo.count() > 0) return;

        List<Maquinaria> maquinas = maquinaRepo.findAll();
        if (maquinas.isEmpty()) return;

        Long id1 = maquinas.get(0).getId();
        Long id2 = maquinas.size() > 1 ? maquinas.get(1).getId() : id1;
        Long id3 = maquinas.size() > 2 ? maquinas.get(2).getId() : id1;

        // 1. Presolicitud pendiente
        Mantenimiento m1 = new Mantenimiento();
        m1.setMaquinariaId(id1);
        m1.setSolicitante("usuario");
        m1.setTipo("Preventivo");
        m1.setDescripcion("Mantenimiento programado de 250 horas: cambio de filtros, aceite de motor y revisión hidráulica general.");
        m1.setEstado("Presolicitud");
        m1.setFecha(LocalDate.now().minusDays(3));
        repo.save(m1);

        // 2. Presolicitud analizada y cotizada
        Mantenimiento m2 = new Mantenimiento();
        m2.setMaquinariaId(id2);
        m2.setSolicitante("usuario");
        m2.setTipo("Correctivo");
        m2.setDescripcion("Fuga en mangueras de alta presión del mástil y ruido en el sistema de frenos.");
        m2.setEstado("Presolicitud analizada");
        m2.setFecha(LocalDate.now().minusDays(5));
        m2.setAnalisis("Se inspeccionó el circuito hidráulico; se detectó fisura en manguera principal y desgaste del 80% en las zapatas de frenos delanteras.");
        m2.setSolucion("Reemplazo de juego de mangueras de 3/4\", cambio de zapatas de freno, purga de circuito y calibración de presión.");
        m2.setCostoEstimado(1450000.0);
        m2.setDiasEstimados(2);
        m2.setTecnicoAsignado("tecnico");
        repo.save(m2);

        // 3. Orden de trabajo aprobada en ejecución
        Mantenimiento m3 = new Mantenimiento();
        m3.setMaquinariaId(id3);
        m3.setSolicitante("usuario");
        m3.setTipo("Correctivo");
        m3.setDescripcion("Pérdida de fuerza en el brazo excavador y humo negro en aceleración.");
        m3.setEstado("Orden de trabajo");
        m3.setFecha(LocalDate.now().minusDays(8));
        m3.setAnalisis("Filtro de combustible obstruido y pérdida de presión en bomba de inyección.");
        m3.setSolucion("Mantenimiento y calibración a bomba de inyección diésel, sustitución de filtros y limpieza de inyectores.");
        m3.setCostoEstimado(3800000.0);
        m3.setValorTotal(3800000.0);
        m3.setDiasEstimados(4);
        m3.setTecnicoAsignado("tecnico");
        m3.setNumeroOrden("ORD-2026-001");
        m3.setFechaAprobacion(LocalDate.now().minusDays(6));
        repo.save(m3);
    }
}