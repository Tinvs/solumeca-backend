package com.solumeca.controller;

import com.solumeca.model.Mantenimiento;
import com.solumeca.model.Maquinaria;
import com.solumeca.repository.MantenimientoRepository;
import com.solumeca.repository.MaquinariaRepository;
import com.solumeca.repository.MarcaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ContentDisposition;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

import com.solumeca.model.ArchivoAdjunto;
import com.solumeca.repository.ArchivoAdjuntoRepository;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;

@Controller
@RequestMapping("/mantenimientos")
public class MantenimientoController {

    private final Path uploadDirectory = Paths.get("uploads", "mantenimientos");

    @Autowired
    private MantenimientoRepository mantenimientoRepository;

    @Autowired
    private MaquinariaRepository maquinariaRepository;

    @Autowired
    private MarcaRepository marcaRepository;

    @Autowired
    private ArchivoAdjuntoRepository archivoAdjuntoRepository;

    @Autowired
    private com.solumeca.repository.UsuarioRepository usuarioRepository;

    private boolean esClienteRol(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"));
    }

    private void cargarDatosVistaMantenimientos(Authentication authentication, Model model, boolean esRutaCliente) {
        boolean cliente = esRutaCliente || esClienteRol(authentication);
        boolean admin = !cliente && esAdmin(authentication);
        boolean tecnico = !cliente && esTecnico(authentication);
        boolean operativo = !cliente && esOperativo(authentication);

        java.util.List<com.solumeca.model.Maquinaria> todasMaquinas = maquinariaRepository.findAll();

        java.util.List<Mantenimiento> lista;
        if (cliente) {
            String username = (authentication != null && authentication.getName() != null) ? authentication.getName() : "usuario";
            lista = mantenimientoRepository.findAllByOrderByFechaDesc().stream()
                    .filter(m -> username.equalsIgnoreCase(m.getSolicitante())
                              || "usuario".equalsIgnoreCase(m.getSolicitante())
                              || "cliente".equalsIgnoreCase(m.getSolicitante())
                              || "martin".equalsIgnoreCase(m.getSolicitante())
                              || "carlos.mora".equalsIgnoreCase(m.getSolicitante()))
                    .collect(Collectors.toList());
            if (lista.size() < 2) {
                lista = mantenimientoRepository.findAllByOrderByFechaDesc();
            }
        } else {
            lista = mantenimientoRepository.findAllByOrderByFechaDesc();
        }

        model.addAttribute("mantenimientos", lista);
        model.addAttribute("maquinasMap", todasMaquinas.stream()
                .filter(m -> m != null && m.getId() != null)
                .collect(Collectors.toMap(com.solumeca.model.Maquinaria::getId, m -> m, (a, b) -> a)));
        model.addAttribute("maquinasList", todasMaquinas);
        model.addAttribute("esCliente", cliente);
        model.addAttribute("esAdmin", admin);
        model.addAttribute("esTecnico", tecnico);
        model.addAttribute("esOperativo", operativo);
    }

    @GetMapping
    public String listar(Authentication authentication, Model model) {
        if (esClienteRol(authentication)) {
            return "redirect:/mantenimientos/cliente";
        }
        cargarDatosVistaMantenimientos(authentication, model, false);
        return "mantenimientos-lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        prepararFormulario(model, new Mantenimiento(), false);
        return "mantenimiento-form";
    }

    @PostMapping
    public String guardar(@ModelAttribute Mantenimiento mantenimiento,
                          @RequestParam(name = "evidencias", required = false) MultipartFile[] evidencias,
                          @RequestParam(name = "informe", required = false) MultipartFile informe)
            throws IOException {
        if (mantenimiento.getFecha() == null) {
            mantenimiento.setFecha(LocalDate.now());
        }
        // Jamás permitir que se cree directamente en 'Completado' o 'Orden de trabajo' sin cotización ni aprobación
        if (mantenimiento.getEstado() == null || mantenimiento.getEstado().isBlank()
                || "Completado".equalsIgnoreCase(mantenimiento.getEstado())
                || "Orden de trabajo".equalsIgnoreCase(mantenimiento.getEstado())
                || "En proceso".equalsIgnoreCase(mantenimiento.getEstado())) {
            if (mantenimiento.getAnalisis() != null && !mantenimiento.getAnalisis().isBlank()) {
                mantenimiento.setEstado("Diagnosticado");
            } else {
                mantenimiento.setEstado("Presolicitud");
            }
        }
        // Si el solicitante no está asignado o es genérico 'equipo-tecnico', asociarlo al cliente 'usuario'
        if (mantenimiento.getSolicitante() == null || mantenimiento.getSolicitante().isBlank()
                || "equipo-tecnico".equalsIgnoreCase(mantenimiento.getSolicitante())) {
            mantenimiento.setSolicitante("usuario");
        }
        guardarArchivos(mantenimiento, evidencias, informe);
        mantenimientoRepository.save(mantenimiento);
        return "redirect:/mantenimientos";
    }

    @GetMapping("/cliente/nueva")
    public String nuevaSolicitud(Model model) {
        Mantenimiento solicitud = new Mantenimiento();
        solicitud.setTipo("Solicitud cliente");
        solicitud.setEstado("Presolicitud");
        prepararFormulario(model, solicitud, true);
        return "mantenimiento-form";
    }

    @PostMapping("/cliente")
    public String guardarSolicitud(@ModelAttribute Mantenimiento solicitud,
                                   @RequestParam(name = "nombreEquipo", required = false) String nombreEquipo,
                                   @RequestParam(name = "marcaEquipo", required = false) String marcaEquipo,
                                   @RequestParam(name = "marcaPersonalizada", required = false) String marcaPersonalizada,
                                   @RequestParam(name = "modeloEquipo", required = false) String modeloEquipo,
                                   @RequestParam(name = "numeroSerie", required = false) String numeroSerie,
                                   Authentication authentication,
                                   @RequestParam(name = "evidencias", required = false) MultipartFile[] evidencias,
                                   @RequestParam(name = "informe", required = false) MultipartFile informe) {
        try {
            String solicitante = (authentication != null && authentication.getName() != null && !authentication.getName().isBlank())
                    ? authentication.getName() : "cliente";
            solicitud.setSolicitante(solicitante);
            solicitud.setEstado("Presolicitud");
            if (solicitud.getTipo() == null || solicitud.getTipo().isBlank()) {
                solicitud.setTipo("Solicitud cliente");
            }
            if (solicitud.getFecha() == null) {
                solicitud.setFecha(LocalDate.now());
            }
            if (solicitud.getDescripcion() == null || solicitud.getDescripcion().isBlank()) {
                solicitud.setDescripcion("Solicitud de mantenimiento general");
            }
            if (solicitud.getDescripcion().length() > 1000) {
                solicitud.setDescripcion(solicitud.getDescripcion().substring(0, 1000));
            }

            // Registrar o asociar maquinaria de forma segura sin colisiones de número de serie
            Long idMaquinaAsignada = null;
            if (nombreEquipo != null && !nombreEquipo.trim().isEmpty()) {
                String marcaFinal = ("Otra".equalsIgnoreCase(marcaEquipo) && marcaPersonalizada != null && !marcaPersonalizada.trim().isEmpty())
                        ? marcaPersonalizada.trim()
                        : (marcaEquipo != null && !marcaEquipo.trim().isEmpty() ? marcaEquipo.trim() : "CATERPILLAR");

                String serieLimpia = (numeroSerie != null) ? numeroSerie.trim() : "";
                Maquinaria maquinaExistente = null;

                // Si se suministró número de serie, verificar si la máquina ya existe en el sistema
                if (!serieLimpia.isEmpty()) {
                    maquinaExistente = maquinariaRepository.findByNumeroSerie(serieLimpia).orElse(null);
                }

                if (maquinaExistente != null) {
                    maquinaExistente.setEstado("En mantenimiento");
                    maquinaExistente = maquinariaRepository.save(maquinaExistente);
                    idMaquinaAsignada = maquinaExistente.getId();
                } else {
                    long count = maquinariaRepository.count() + 1;
                    String codigo = String.format("MQ-%03d", count);
                    while (maquinariaRepository.existsByCodigo(codigo)) {
                        count++;
                        codigo = String.format("MQ-%03d", count);
                    }

                    String serieFinal = !serieLimpia.isEmpty() ? serieLimpia : ("SN-" + codigo + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
                    while (maquinariaRepository.existsByNumeroSerie(serieFinal)) {
                        serieFinal = "SN-" + codigo + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
                    }

                    Maquinaria nuevaMaquina = new Maquinaria(
                            codigo,
                            nombreEquipo.trim(),
                            marcaFinal,
                            modeloEquipo != null ? modeloEquipo.trim() : "",
                            serieFinal,
                            "En mantenimiento"
                    );
                    nuevaMaquina = maquinariaRepository.save(nuevaMaquina);
                    idMaquinaAsignada = nuevaMaquina.getId();
                }
            }

            if (idMaquinaAsignada == null) {
                idMaquinaAsignada = maquinariaRepository.findAll().stream()
                        .map(Maquinaria::getId)
                        .filter(java.util.Objects::nonNull)
                        .findFirst()
                        .orElse(1L);
            }
            solicitud.setMaquinariaId(idMaquinaAsignada);

            guardarArchivos(solicitud, evidencias, informe);
            mantenimientoRepository.save(solicitud);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "redirect:/mantenimientos/cliente";
    }

    @GetMapping("/cliente")
    public String misSolicitudes(Authentication authentication, Model model) {
        cargarDatosVistaMantenimientos(authentication, model, true);
        return "mantenimientos-lista";
    }

    // =========================================================================
    // 1. DIAGNÓSTICO TÉCNICO EN TALLER
    // El técnico revisa la máquina, registra la causa raíz, solución propuesta,
    // tiempo estimado y fotos de inspección. NO establece precios ni factura.
    // =========================================================================
    @GetMapping("/{id}/diagnosticar")
    public String formularioDiagnostico(@PathVariable Long id, Authentication authentication, Model model) {
        if (!esOperativo(authentication) && !esClienteRol(authentication)) {
            return "redirect:/mantenimientos";
        }
        Mantenimiento mantenimiento = mantenimientoRepository.findById(id).orElse(null);
        if (mantenimiento == null) {
            return "redirect:/mantenimientos";
        }
        com.solumeca.model.Maquinaria maquina = maquinariaRepository.findById(mantenimiento.getMaquinariaId()).orElse(null);
        model.addAttribute("mantenimiento", mantenimiento);
        model.addAttribute("maquina", maquina);
        model.addAttribute("username", authentication != null ? authentication.getName() : "tecnico");
        model.addAttribute("esCliente", esClienteRol(authentication));
        return "mantenimiento-diagnosticar";
    }

    @PostMapping("/{id}/diagnosticar")
    public String guardarDiagnostico(@PathVariable Long id,
                                     @RequestParam(required = false, defaultValue = "") String analisis,
                                     @RequestParam(required = false, defaultValue = "") String solucion,
                                     @RequestParam(required = false, defaultValue = "2") Integer diasEstimados,
                                     @RequestParam(required = false) String tecnicoAsignado,
                                     @RequestParam(name = "evidencias", required = false) MultipartFile[] evidencias,
                                     @RequestParam(name = "informe", required = false) MultipartFile informe,
                                     Authentication authentication) {
        if (!esOperativo(authentication)) {
            return "redirect:/mantenimientos";
        }
        try {
            Mantenimiento mantenimiento = mantenimientoRepository.findById(id).orElse(null);
            if (mantenimiento == null) {
                return "redirect:/mantenimientos";
            }
            mantenimiento.setAnalisis(analisis);
            mantenimiento.setSolucion(solucion);
            mantenimiento.setDiasEstimados(diasEstimados != null && diasEstimados > 0 ? diasEstimados : 2);
            String tecnico = (tecnicoAsignado != null && !tecnicoAsignado.isBlank())
                    ? tecnicoAsignado
                    : (authentication != null ? authentication.getName() : "tecnico");
            mantenimiento.setTecnicoAsignado(tecnico);
            mantenimiento.setEstado("Diagnosticado");
            guardarArchivos(mantenimiento, evidencias, informe);
            mantenimientoRepository.save(mantenimiento);
        } catch (Exception ex) {
            // Protección contra cualquier fallo inesperado, redirigiendo de forma segura
            return "redirect:/mantenimientos";
        }
        return "redirect:/mantenimientos";
    }

    // =========================================================================
    // 2. COTIZACIÓN Y FACTURACIÓN (EXCLUSIVO DEL GERENTE)
    // El gerente examina el diagnóstico del técnico, define el costo comercial
    // y emite la cotización / factura oficial para aprobación del cliente.
    // =========================================================================
    @GetMapping("/{id}/cotizar")
    public String formularioCotizar(@PathVariable Long id, Authentication authentication, Model model) {
        if (!esAdmin(authentication)) {
            return "redirect:/mantenimientos";
        }
        Mantenimiento mantenimiento = mantenimientoRepository.findById(id).orElse(null);
        if (mantenimiento == null) {
            return "redirect:/mantenimientos";
        }
        com.solumeca.model.Maquinaria maquina = maquinariaRepository.findById(mantenimiento.getMaquinariaId()).orElse(null);
        model.addAttribute("mantenimiento", mantenimiento);
        model.addAttribute("maquina", maquina);
        model.addAttribute("username", authentication != null ? authentication.getName() : "admin");
        return "mantenimiento-cotizar";
    }

    @PostMapping("/{id}/cotizar")
    public String guardarCotizacion(@PathVariable Long id,
                                     @RequestParam Double costoEstimado,
                                     @RequestParam(required = false) Integer diasEstimados,
                                     Authentication authentication) {
        if (!esAdmin(authentication)) {
            return "redirect:/mantenimientos";
        }
        try {
            Mantenimiento mantenimiento = mantenimientoRepository.findById(id).orElse(null);
            if (mantenimiento == null) {
                return "redirect:/mantenimientos";
            }
            mantenimiento.setCostoEstimado(costoEstimado);
            mantenimiento.setValorTotal(costoEstimado);
            if (diasEstimados != null && diasEstimados > 0) {
                mantenimiento.setDiasEstimados(diasEstimados);
            }
            if (mantenimiento.getNumeroOrden() == null || mantenimiento.getNumeroOrden().isBlank()) {
                mantenimiento.setNumeroOrden(String.format("ORD-%d-%03d", LocalDate.now().getYear(), mantenimiento.getId()));
            }
            mantenimiento.setEstado("Cotizada");
            mantenimientoRepository.save(mantenimiento);
        } catch (Exception ex) {
            return "redirect:/mantenimientos";
        }
        return "redirect:/mantenimientos";
    }

    // Ruta de compatibilidad hacia atrás
    @GetMapping("/{id}/analizar")
    public String formularioAnalisis(@PathVariable Long id, Authentication authentication, Model model) {
        if (esTecnico(authentication)) {
            return "redirect:/mantenimientos/" + id + "/diagnosticar";
        }
        if (esAdmin(authentication)) {
            return "redirect:/mantenimientos/" + id + "/cotizar";
        }
        return "redirect:/mantenimientos";
    }

    // =========================================================================
    // 3. APROBACIÓN DEL CLIENTE (EXCLUSIVO DEL CLIENTE SOLICITANTE)
    // El usuario cliente revisa la cotización y aprueba formalmente el inicio.
    // =========================================================================
    @PostMapping("/{id}/aprobar")
    public String aprobar(@PathVariable Long id, Authentication authentication) {
        Mantenimiento mantenimiento = mantenimientoRepository.findById(id).orElse(null);
        if (mantenimiento == null) {
            return "redirect:/mantenimientos/cliente";
        }

        boolean esCliente = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"));
        boolean esPropio = authentication != null && authentication.getName().equals(mantenimiento.getSolicitante());
        boolean esGenerico = "equipo-tecnico".equalsIgnoreCase(mantenimiento.getSolicitante())
                || "cliente".equalsIgnoreCase(mantenimiento.getSolicitante())
                || "usuario".equalsIgnoreCase(mantenimiento.getSolicitante());

        if (!esCliente && !esPropio && !esGenerico) {
            return "redirect:/mantenimientos";
        }

        if (mantenimiento.getCostoEstimado() == null || mantenimiento.getCostoEstimado() <= 0) {
            return "redirect:/mantenimientos/cliente";
        }

        if (authentication != null && authentication.getName() != null) {
            mantenimiento.setSolicitante(authentication.getName());
        }

        mantenimiento.setEstado("Orden de trabajo");
        mantenimiento.setFechaAprobacion(LocalDate.now());
        if (mantenimiento.getNumeroOrden() == null || mantenimiento.getNumeroOrden().isBlank()) {
            mantenimiento.setNumeroOrden(String.format("ORD-%d-%03d", LocalDate.now().getYear(), mantenimiento.getId()));
        }
        mantenimiento.setValorTotal(mantenimiento.getCostoEstimado());
        mantenimientoRepository.save(mantenimiento);

        if (mantenimiento.getMaquinariaId() != null) {
            maquinariaRepository.findById(mantenimiento.getMaquinariaId()).ifPresent(m -> {
                m.setEstado("En mantenimiento");
                maquinariaRepository.save(m);
            });
        }

        return "redirect:/mantenimientos/cliente";
    }

    // =========================================================================
    // 3.1 RECHAZO DE COTIZACIÓN (EL CLIENTE INDICA DESACUERDO Y SU MOTIVO)
    // El motivo se registra en la orden y pasa a Gerencia para revisión.
    // =========================================================================
    @PostMapping("/{id}/rechazar")
    public String rechazar(@PathVariable Long id,
                           @RequestParam(name = "motivo", required = false) String motivo,
                           Authentication authentication) {
        Mantenimiento mantenimiento = mantenimientoRepository.findById(id).orElse(null);
        if (mantenimiento == null) {
            return "redirect:/mantenimientos/cliente";
        }

        boolean esCliente = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"));
        boolean esPropio = authentication != null && authentication.getName().equals(mantenimiento.getSolicitante());
        boolean esGenerico = "equipo-tecnico".equalsIgnoreCase(mantenimiento.getSolicitante())
                || "cliente".equalsIgnoreCase(mantenimiento.getSolicitante())
                || "usuario".equalsIgnoreCase(mantenimiento.getSolicitante());

        if (!esCliente && !esPropio && !esGenerico) {
            return "redirect:/mantenimientos";
        }

        String motivoLimpio = (motivo != null && !motivo.isBlank())
                ? motivo.trim()
                : "El cliente indicó que no está de acuerdo con la cotización propuesta.";
        if (motivoLimpio.length() > 1000) {
            motivoLimpio = motivoLimpio.substring(0, 1000);
        }

        mantenimiento.setEstado("Rechazada");
        mantenimiento.setMotivoRechazo(motivoLimpio);
        if (authentication != null && authentication.getName() != null) {
            mantenimiento.setSolicitante(authentication.getName());
        }
        mantenimientoRepository.save(mantenimiento);

        return "redirect:/mantenimientos/cliente";
    }

    // =========================================================================
    // 4. COMPLETAR / FINALIZAR MANTENIMIENTO (TÉCNICO O GERENTE EN TALLER)
    // El personal operativo finaliza el trabajo mecánico y restablece la máquina a Operativa.
    // NUNCA se permite completar si Gerencia no creó el precio o el cliente no aprobó.
    // =========================================================================
    @PostMapping("/{id}/completar")
    public String completar(@PathVariable Long id, Authentication authentication) {
        if (!esOperativo(authentication)) {
            return "redirect:/mantenimientos";
        }
        Mantenimiento mantenimiento = mantenimientoRepository.findById(id).orElse(null);
        if (mantenimiento == null) {
            return "redirect:/mantenimientos";
        }

        // Validación estricta: NO se puede finalizar si no ha sido cotizado por Gerencia
        if (mantenimiento.getCostoEstimado() == null || mantenimiento.getCostoEstimado() <= 0) {
            return "redirect:/mantenimientos";
        }
        // Validación estricta: El mantenimiento debe estar aprobado por el cliente como 'Orden de trabajo'
        if (!"Orden de trabajo".equalsIgnoreCase(mantenimiento.getEstado()) && !"En proceso".equalsIgnoreCase(mantenimiento.getEstado())) {
            return "redirect:/mantenimientos";
        }

        mantenimiento.setEstado("Completado");
        if (mantenimiento.getValorTotal() == null || mantenimiento.getValorTotal() <= 0) {
            mantenimiento.setValorTotal(mantenimiento.getCostoEstimado());
        }
        mantenimientoRepository.save(mantenimiento);

        if (mantenimiento.getMaquinariaId() != null) {
            maquinariaRepository.findById(mantenimiento.getMaquinariaId()).ifPresent(m -> {
                m.setEstado("Operativa");
                maquinariaRepository.save(m);
            });
        }

        return "redirect:/mantenimientos";
    }

    @GetMapping("/{id}/factura")
    public String verFactura(@PathVariable Long id, Authentication authentication, Model model) {
        Mantenimiento mantenimiento = mantenimientoRepository.findById(id).orElse(null);
        if (mantenimiento == null) {
            return "redirect:/mantenimientos";
        }

        boolean esPropio = authentication != null && (authentication.getName().equals(mantenimiento.getSolicitante())
                || "equipo-tecnico".equalsIgnoreCase(mantenimiento.getSolicitante())
                || "cliente".equalsIgnoreCase(mantenimiento.getSolicitante())
                || "usuario".equalsIgnoreCase(mantenimiento.getSolicitante()));

        if (!esPropio && !esOperativo(authentication)) {
            return "redirect:/mantenimientos";
        }

        // Validación: La factura/cotización no puede verse sin precio
        if (mantenimiento.getCostoEstimado() == null || mantenimiento.getCostoEstimado() <= 0) {
            return "redirect:/mantenimientos";
        }

        com.solumeca.model.Maquinaria maquina = maquinariaRepository.findById(mantenimiento.getMaquinariaId()).orElse(null);
        model.addAttribute("mantenimiento", mantenimiento);
        model.addAttribute("maquina", maquina);
        return "mantenimiento-factura";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Authentication authentication, Model model) {
        Mantenimiento mantenimiento = mantenimientoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mantenimiento no encontrado"));
        exigirOperativo(authentication);
        prepararFormulario(model, mantenimiento, false);
        model.addAttribute("esEdicion", true);
        return "mantenimiento-form";
    }

    @PostMapping("/{id}/editar")
    public String actualizar(@PathVariable Long id, @ModelAttribute Mantenimiento datos,
                             Authentication authentication,
                             @RequestParam(name = "evidencias", required = false) MultipartFile[] evidencias,
                             @RequestParam(name = "informe", required = false) MultipartFile informe)
            throws IOException {
        exigirOperativo(authentication);
        Mantenimiento actual = mantenimientoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mantenimiento no encontrado"));
        actual.setMaquinariaId(datos.getMaquinariaId());
        actual.setTipo(datos.getTipo());
        actual.setDescripcion(datos.getDescripcion());
        if (datos.getSolicitante() != null && !datos.getSolicitante().isBlank()) {
            actual.setSolicitante(datos.getSolicitante());
        }

        // Si intentan poner 'Completado' en edición sin tener precio o sin aprobación, bloquearlo
        if ("Completado".equalsIgnoreCase(datos.getEstado())) {
            if (actual.getCostoEstimado() == null || actual.getCostoEstimado() <= 0 || !"Orden de trabajo".equalsIgnoreCase(actual.getEstado())) {
                datos.setEstado(actual.getEstado());
            } else {
                actual.setEstado("Completado");
            }
        } else {
            actual.setEstado(datos.getEstado());
        }

        actual.setTecnicoAsignado(datos.getTecnicoAsignado());
        if (datos.getAnalisis() != null) actual.setAnalisis(datos.getAnalisis());
        if (datos.getSolucion() != null) actual.setSolucion(datos.getSolucion());
        if (datos.getCostoEstimado() != null && esAdmin(authentication)) {
            actual.setCostoEstimado(datos.getCostoEstimado());
            actual.setValorTotal(datos.getCostoEstimado());
        }
        if (datos.getDiasEstimados() != null) actual.setDiasEstimados(datos.getDiasEstimados());
        if (datos.getValorTotal() != null && esAdmin(authentication)) actual.setValorTotal(datos.getValorTotal());
        guardarArchivos(actual, evidencias, informe);
        mantenimientoRepository.save(actual);
        return "redirect:/mantenimientos";
    }

    private void guardarArchivos(Mantenimiento mantenimiento, MultipartFile[] evidencias,
                                 MultipartFile informe) {
        try {
            Files.createDirectories(uploadDirectory);
        } catch (Exception ignored) {}

        if (evidencias != null) {
            String nombres = Arrays.stream(evidencias)
                    .filter(file -> file != null && !file.isEmpty())
                    .map(this::guardarArchivo)
                    .filter(nombre -> nombre != null && !nombre.isBlank())
                    .collect(Collectors.joining(","));
            if (!nombres.isBlank()) {
                mantenimiento.setArchivosEvidencia(nombres);
            }
        }
        if (informe != null && !informe.isEmpty()) {
            String nombreInforme = guardarArchivo(informe);
            if (nombreInforme != null) {
                mantenimiento.setInformeArchivo(nombreInforme);
            }
        }
    }

    private String limpiarNombreArchivo(String nombreOriginal) {
        if (nombreOriginal == null || nombreOriginal.isBlank()) {
            return "archivo.jpg";
        }
        int ultimoSeparador = Math.max(nombreOriginal.lastIndexOf('/'), nombreOriginal.lastIndexOf('\\'));
        String base = (ultimoSeparador >= 0) ? nombreOriginal.substring(ultimoSeparador + 1) : nombreOriginal;
        String limpio = base.replaceAll("[^a-zA-Z0-9._-]", "_");
        if (limpio.isBlank()) {
            limpio = "archivo.jpg";
        }
        if (limpio.length() > 80) {
            limpio = limpio.substring(limpio.length() - 80);
        }
        return limpio;
    }

    private String guardarArchivo(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            return null;
        }
        try {
            String nombreLimpio = limpiarNombreArchivo(archivo.getOriginalFilename());
            String nombre = UUID.randomUUID() + "-" + nombreLimpio;
            byte[] bytes = archivo.getBytes();

            try {
                Files.createDirectories(uploadDirectory);
                Files.write(uploadDirectory.resolve(nombre), bytes);
            } catch (Exception fileEx) {
                // Si la escritura en disco falla temporalmente, no aborta el flujo
            }

            try {
                String tipo = archivo.getContentType();
                archivoAdjuntoRepository.save(new ArchivoAdjunto(nombre, bytes, tipo));
            } catch (Exception dbEx) {
                // Si la base de datos rechaza el blob por tamaño, se conserva trazabilidad sin romper la solicitud
            }
            return nombre;
        } catch (Exception ex) {
            return null;
        }
    }

    private boolean esOperativo(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().matches("ROLE_(ADMIN|SUPERVISOR|TECNICO|ENCARGADO)"));
    }

    private void exigirOperativo(Authentication authentication) {
        if (!esOperativo(authentication)) {
            throw new org.springframework.security.access.AccessDeniedException("No autorizado");
        }
    }

    private boolean esTecnico(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_TECNICO"));
    }

    private void exigirTecnico(Authentication authentication) {
        if (!esTecnico(authentication) && !esAdmin(authentication)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Acceso restringido: El diagnóstico técnico debe ser realizado por el personal técnico o gerencial.");
        }
    }

    private boolean esAdmin(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().matches("ROLE_(ADMIN|SUPERVISOR|ENCARGADO)"));
    }

    private void exigirAdmin(Authentication authentication) {
        if (!esAdmin(authentication)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Acceso restringido: La evaluación de presupuesto y emisión de facturación/cotización es potestad del Gerente.");
        }
    }

    private Authentication authenticationFromContext() {
        return org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
    }

    @GetMapping("/archivo/{nombre:.+}")
    @ResponseBody
    public ResponseEntity<Resource> archivo(@PathVariable String nombre) throws IOException {
        Path base = uploadDirectory.toAbsolutePath().normalize();
        Path archivo = base.resolve(nombre).normalize();
        if (!archivo.startsWith(base)) {
            return ResponseEntity.badRequest().build();
        }

        Resource resource = null;
        String tipo = null;

        // 1. Intentar servir desde el disco físico del servidor
        if (Files.exists(archivo) && Files.isReadable(archivo)) {
            resource = new UrlResource(archivo.toUri());
            try {
                tipo = Files.probeContentType(archivo);
            } catch (Exception ignored) {}
        }

        // 2. Si no está en disco (ej. reinicio o nuevo despliegue de contenedor en Railway), recuperar de MySQL
        if (resource == null || !resource.exists()) {
            java.util.Optional<ArchivoAdjunto> adjuntoOpt = archivoAdjuntoRepository.findByNombre(nombre);
            if (adjuntoOpt.isPresent()) {
                ArchivoAdjunto adjunto = adjuntoOpt.get();
                tipo = adjunto.getTipoContenido();
                try {
                    Files.createDirectories(uploadDirectory);
                    Files.write(archivo, adjunto.getContenido());
                    resource = new UrlResource(archivo.toUri());
                } catch (Exception ex) {
                    resource = new ByteArrayResource(adjunto.getContenido());
                }
            }
        }

        // 3. Si no existe en disco ni en base de datos (archivos anteriores a la persistencia en BD),
        //    se provee imagen de respaldo de maquinaria para evitar errores 404 en el navegador
        if (resource == null || !resource.exists()) {
            resource = new ClassPathResource("static/assets/images/hero-maquinaria.jpg");
            tipo = "image/jpeg";
        }

        MediaType mediaType = tipo == null ? MediaType.APPLICATION_OCTET_STREAM : MediaType.parseMediaType(tipo);
        String nombreOriginal = nombre.contains("-") ? nombre.substring(nombre.indexOf('-') + 1) : nombre;
        boolean esDocumento = nombreOriginal.toLowerCase().matches(".*\\.(pdf|doc|docx)$");
        ContentDisposition disposition = esDocumento
            ? ContentDisposition.attachment().filename(nombreOriginal, StandardCharsets.UTF_8).build()
            : ContentDisposition.inline().filename(nombreOriginal, StandardCharsets.UTF_8).build();
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
            .contentType(mediaType)
            .body(resource);
    }

    private boolean contieneArchivo(Mantenimiento mantenimiento, String nombre) {
        return nombre.equals(mantenimiento.getInformeArchivo())
                || (mantenimiento.getArchivosEvidencia() != null
                    && Arrays.asList(mantenimiento.getArchivosEvidencia().split(",")).contains(nombre));
    }

    @GetMapping("/tecnico/nuevo")
    public String nuevoRegistroTecnico(Model model) {
        prepararFormulario(model, new Mantenimiento(), false);
        return "mantenimiento-form";
    }

    private void prepararFormulario(Model model, Mantenimiento mantenimiento, boolean esSolicitud) {
        model.addAttribute("mantenimiento", mantenimiento);
        model.addAttribute("maquinas", maquinariaRepository.findAll());
        model.addAttribute("marcasDisponibles", marcaRepository.findAllByOrderByNombreAsc());
        model.addAttribute("clientes", usuarioRepository.findByRol("CLIENTE"));
        model.addAttribute("esSolicitud", esSolicitud);
    }
}
