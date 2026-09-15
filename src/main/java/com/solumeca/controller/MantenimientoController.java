package com.solumeca.controller;

import com.solumeca.model.Mantenimiento;
import com.solumeca.repository.MantenimientoRepository;
import com.solumeca.repository.MaquinariaRepository;
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

@Controller
@RequestMapping("/mantenimientos")
public class MantenimientoController {

    private final Path uploadDirectory = Paths.get("uploads", "mantenimientos");

    @Autowired
    private MantenimientoRepository mantenimientoRepository;

    @Autowired
    private MaquinariaRepository maquinariaRepository;

    @GetMapping
    public String listar(Authentication authentication, Model model) {
        model.addAttribute("mantenimientos", mantenimientoRepository.findAllByOrderByFechaDesc());
        model.addAttribute("maquinasMap", maquinariaRepository.findAll().stream()
                .collect(Collectors.toMap(com.solumeca.model.Maquinaria::getId, m -> m, (a, b) -> a)));
        model.addAttribute("esOperativo", esOperativo(authentication));
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
        exigirOperativo(authentication);
        Mantenimiento mantenimiento = mantenimientoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mantenimiento no encontrado: " + id));
        com.solumeca.model.Maquinaria maquina = maquinariaRepository.findById(mantenimiento.getMaquinariaId()).orElse(null);
        model.addAttribute("mantenimiento", mantenimiento);
        model.addAttribute("maquina", maquina);
        return "mantenimiento-analizar";
    }

    @PostMapping("/{id}/analizar")
    public String guardarAnalisis(@PathVariable Long id,
                                  @RequestParam String analisis,
                                  @RequestParam String solucion,
                                  @RequestParam Double costoEstimado,
                                  @RequestParam Integer diasEstimados,
                                  @RequestParam String tecnicoAsignado,
                                  Authentication authentication) {
        exigirOperativo(authentication);
        Mantenimiento mantenimiento = mantenimientoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mantenimiento no encontrado: " + id));
        mantenimiento.setAnalisis(analisis);
        mantenimiento.setSolucion(solucion);
        mantenimiento.setCostoEstimado(costoEstimado);
        mantenimiento.setDiasEstimados(diasEstimados);
        mantenimiento.setTecnicoAsignado(tecnicoAsignado);
        mantenimiento.setEstado("Presolicitud analizada");
        mantenimientoRepository.save(mantenimiento);
        return "redirect:/mantenimientos";
    }

    @PostMapping("/{id}/aprobar")
    public String aprobar(@PathVariable Long id, Authentication authentication) {
        Mantenimiento mantenimiento = mantenimientoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mantenimiento no encontrado: " + id));

        boolean esPropio = authentication != null && authentication.getName().equals(mantenimiento.getSolicitante());
        if (!esPropio && !esOperativo(authentication)) {
            throw new org.springframework.security.access.AccessDeniedException("No autorizado");
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

        return esPropio ? "redirect:/mantenimientos/cliente" : "redirect:/mantenimientos";
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
            Files.copy(archivo.getInputStream(), uploadDirectory.resolve(nombre), StandardCopyOption.REPLACE_EXISTING);
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
        Authentication authentication = authenticationFromContext();
        boolean autorizado = esOperativo(authentication)
                || mantenimientoRepository.findAll().stream()
                    .filter(m -> m.getSolicitante().equals(authentication.getName()))
                    .anyMatch(m -> contieneArchivo(m, nombre));
        if (!autorizado) {
            throw new org.springframework.security.access.AccessDeniedException("Archivo no autorizado");
        }
        Resource resource = new UrlResource(archivo.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }
        String tipo = Files.probeContentType(archivo);
        MediaType mediaType = tipo == null ? MediaType.APPLICATION_OCTET_STREAM : MediaType.parseMediaType(tipo);
        String nombreOriginal = nombre.substring(nombre.indexOf('-') + 1);
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
        model.addAttribute("esSolicitud", esSolicitud);
    }
}
