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

    @GetMapping
    public String listar(Authentication authentication, Model model) {
        if (authentication != null && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"))) {
            return "redirect:/mantenimientos/cliente";
        }
        model.addAttribute("mantenimientos", mantenimientoRepository.findAllByOrderByFechaDesc());
        model.addAttribute("maquinasMap", maquinariaRepository.findAll().stream()
                .collect(Collectors.toMap(com.solumeca.model.Maquinaria::getId, m -> m, (a, b) -> a)));
        model.addAttribute("esOperativo", esOperativo(authentication));
        model.addAttribute("esTecnico", esTecnico(authentication));
        model.addAttribute("esCliente", false);
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
        if (mantenimiento.getEstado() == null || mantenimiento.getEstado().isBlank()) {
            mantenimiento.setEstado("Pendiente");
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
                                   @RequestParam(name = "informe", required = false) MultipartFile informe)
            throws IOException {
        solicitud.setSolicitante(authentication.getName());
        solicitud.setEstado("Presolicitud");
        if (solicitud.getTipo() == null || solicitud.getTipo().isBlank()) {
            solicitud.setTipo("Solicitud cliente");
        }
        solicitud.setFecha(LocalDate.now());

        // Si el cliente ingresa datos de su maquinaria, registrarla automáticamente en el inventario
        if (nombreEquipo != null && !nombreEquipo.trim().isEmpty()) {
            String marcaFinal = ("Otra".equalsIgnoreCase(marcaEquipo) && marcaPersonalizada != null && !marcaPersonalizada.trim().isEmpty())
                    ? marcaPersonalizada.trim()
                    : (marcaEquipo != null && !marcaEquipo.trim().isEmpty() ? marcaEquipo.trim() : "CATERPILLAR");

            long count = maquinariaRepository.count() + 1;
            String codigo = String.format("MQ-%03d", count);
            while (maquinariaRepository.existsByCodigo(codigo)) {
                count++;
                codigo = String.format("MQ-%03d", count);
            }

            String serieFinal = (numeroSerie != null && !numeroSerie.trim().isEmpty())
                    ? numeroSerie.trim()
                    : ("SN-" + codigo + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase());

            Maquinaria nuevaMaquina = new Maquinaria(
                    codigo,
                    nombreEquipo.trim(),
                    marcaFinal,
                    modeloEquipo != null ? modeloEquipo.trim() : "",
                    serieFinal,
                    "En mantenimiento"
            );
            nuevaMaquina = maquinariaRepository.save(nuevaMaquina);
            solicitud.setMaquinariaId(nuevaMaquina.getId());
        }

        guardarArchivos(solicitud, evidencias, informe);
        mantenimientoRepository.save(solicitud);
        return "redirect:/mantenimientos/cliente";
    }

    @GetMapping("/cliente")
    public String misSolicitudes(Authentication authentication, Model model) {
        model.addAttribute("mantenimientos", mantenimientoRepository
                .findBySolicitanteOrderByFechaDesc(authentication.getName()));
        model.addAttribute("maquinasMap", maquinariaRepository.findAll().stream()
                .collect(Collectors.toMap(com.solumeca.model.Maquinaria::getId, m -> m, (a, b) -> a)));
        model.addAttribute("esCliente", true);
        model.addAttribute("esOperativo", false);
        return "mantenimientos-lista";
    }

    @GetMapping("/{id}/analizar")
    public String formularioAnalisis(@PathVariable Long id, Authentication authentication, Model model) {
        exigirTecnico(authentication);
        Mantenimiento mantenimiento = mantenimientoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mantenimiento no encontrado: " + id));
        com.solumeca.model.Maquinaria maquina = maquinariaRepository.findById(mantenimiento.getMaquinariaId()).orElse(null);
        model.addAttribute("mantenimiento", mantenimiento);
        model.addAttribute("maquina", maquina);
        model.addAttribute("username", authentication != null ? authentication.getName() : "tecnico");
        return "mantenimiento-analizar";
    }

    @PostMapping("/{id}/analizar")
    public String guardarAnalisis(@PathVariable Long id,
                                  @RequestParam String analisis,
                                  @RequestParam String solucion,
                                  @RequestParam Double costoEstimado,
                                  @RequestParam Integer diasEstimados,
                                  @RequestParam String tecnicoAsignado,
                                  @RequestParam(name = "evidencias", required = false) MultipartFile[] evidencias,
                                  @RequestParam(name = "informe", required = false) MultipartFile informe,
                                  Authentication authentication) throws IOException {
        exigirTecnico(authentication);
        Mantenimiento mantenimiento = mantenimientoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mantenimiento no encontrado: " + id));
        mantenimiento.setAnalisis(analisis);
        mantenimiento.setSolucion(solucion);
        mantenimiento.setCostoEstimado(costoEstimado);
        mantenimiento.setDiasEstimados(diasEstimados);
        mantenimiento.setTecnicoAsignado(tecnicoAsignado);
        mantenimiento.setEstado("Presolicitud analizada");
        guardarArchivos(mantenimiento, evidencias, informe);
        mantenimientoRepository.save(mantenimiento);
        return "redirect:/mantenimientos";
    }

    @PostMapping("/{id}/aprobar")
    public String aprobar(@PathVariable Long id, Authentication authentication) {
        Mantenimiento mantenimiento = mantenimientoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mantenimiento no encontrado: " + id));

        boolean esPropio = authentication != null && authentication.getName().equals(mantenimiento.getSolicitante());
        boolean esCliente = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"));

        // El gerente no aprueba, solo el usuario cliente que creo la solicitud
        if (!esPropio || !esCliente) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Solo el usuario cliente que solicitó el mantenimiento puede aprobar la cotización.");
        }

        mantenimiento.setEstado("Orden de trabajo");
        mantenimiento.setFechaAprobacion(LocalDate.now());
        if (mantenimiento.getNumeroOrden() == null || mantenimiento.getNumeroOrden().isBlank()) {
            mantenimiento.setNumeroOrden(String.format("ORD-%d-%03d", LocalDate.now().getYear(), mantenimiento.getId()));
        }
        if (mantenimiento.getValorTotal() == null) {
            mantenimiento.setValorTotal(mantenimiento.getCostoEstimado() != null ? mantenimiento.getCostoEstimado() : 0.0);
        }
        mantenimientoRepository.save(mantenimiento);

        return "redirect:/mantenimientos/cliente";
    }

    @PostMapping("/{id}/completar")
    public String completar(@PathVariable Long id, Authentication authentication) {
        exigirOperativo(authentication);
        Mantenimiento mantenimiento = mantenimientoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mantenimiento no encontrado: " + id));
        mantenimiento.setEstado("Completado");
        if (mantenimiento.getValorTotal() == null) {
            mantenimiento.setValorTotal(mantenimiento.getCostoEstimado() != null ? mantenimiento.getCostoEstimado() : 0.0);
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
        Mantenimiento mantenimiento = mantenimientoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mantenimiento no encontrado: " + id));

        boolean esPropio = authentication != null && authentication.getName().equals(mantenimiento.getSolicitante());
        if (!esPropio && !esOperativo(authentication)) {
            throw new org.springframework.security.access.AccessDeniedException("No autorizado");
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
        actual.setEstado(datos.getEstado());
        actual.setTecnicoAsignado(datos.getTecnicoAsignado());
        if (datos.getAnalisis() != null) actual.setAnalisis(datos.getAnalisis());
        if (datos.getSolucion() != null) actual.setSolucion(datos.getSolucion());
        if (datos.getCostoEstimado() != null) actual.setCostoEstimado(datos.getCostoEstimado());
        if (datos.getDiasEstimados() != null) actual.setDiasEstimados(datos.getDiasEstimados());
        if (datos.getValorTotal() != null) actual.setValorTotal(datos.getValorTotal());
        guardarArchivos(actual, evidencias, informe);
        mantenimientoRepository.save(actual);
        return "redirect:/mantenimientos";
    }

    private void guardarArchivos(Mantenimiento mantenimiento, MultipartFile[] evidencias,
                                 MultipartFile informe) throws IOException {
        Files.createDirectories(uploadDirectory);
        if (evidencias != null) {
            String nombres = Arrays.stream(evidencias)
                    .filter(file -> file != null && !file.isEmpty())
                    .map(this::guardarArchivo)
                    .collect(Collectors.joining(","));
            if (!nombres.isBlank()) mantenimiento.setArchivosEvidencia(nombres);
        }
        if (informe != null && !informe.isEmpty()) mantenimiento.setInformeArchivo(guardarArchivo(informe));
    }

    private String guardarArchivo(MultipartFile archivo) {
        try {
            String nombre = UUID.randomUUID() + "-" + Paths.get(archivo.getOriginalFilename()).getFileName();
            byte[] bytes = archivo.getBytes();
            Files.createDirectories(uploadDirectory);
            Files.write(uploadDirectory.resolve(nombre), bytes);
            try {
                String tipo = archivo.getContentType();
                archivoAdjuntoRepository.save(new ArchivoAdjunto(nombre, bytes, tipo));
            } catch (Exception dbEx) {
                // Si la BD no está disponible temporalmente, el archivo se conserva en disco
            }
            return nombre;
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo guardar el archivo", exception);
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
        if (!esTecnico(authentication)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Acceso restringido: El análisis y cotización técnica debe ser realizado por el técnico.");
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
        model.addAttribute("esSolicitud", esSolicitud);
    }
}
