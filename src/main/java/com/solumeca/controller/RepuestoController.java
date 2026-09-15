package com.solumeca.controller;

import com.solumeca.model.Repuesto;
import com.solumeca.model.Proveedor;
import com.solumeca.repository.RepuestoRepository;
import com.solumeca.repository.ProveedorRepository;
import com.solumeca.repository.MarcaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/repuestos")
public class RepuestoController {

    @Autowired
    private RepuestoRepository repuestoRepository;

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private MarcaRepository marcaRepository;

    @GetMapping
    public String listar(Model model, Authentication authentication) {
        model.addAttribute("repuestos", repuestoRepository.findAllByOrderByNombreAsc());
        model.addAttribute("esAdminOEncargado", esAdminOEncargado(authentication));
        return "repuestos/repuestos-lista";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model, Authentication authentication) {
        exigirAdminOEncargado(authentication);
        model.addAttribute("repuesto", new Repuesto());
        model.addAttribute("marcas", marcaRepository.findAllByOrderByNombreAsc());
        model.addAttribute("proveedores", proveedorRepository.findAll());
        model.addAttribute("esNuevo", true);
        return "repuestos/repuesto-form";
    }

    @PostMapping
    public String guardar(@ModelAttribute Repuesto repuesto,
                          @RequestParam(required = false) Long proveedorId,
                          Authentication authentication) {
        exigirAdminOEncargado(authentication);
        if (proveedorId != null) {
            Proveedor proveedor = proveedorRepository.findById(proveedorId).orElse(null);
            repuesto.setProveedor(proveedor);
        }
        repuestoRepository.save(repuesto);
        return "redirect:/repuestos";
    }

    @GetMapping("/{id}/editar")
    public String formularioEditar(@PathVariable Long id, Model model, Authentication authentication) {
        exigirAdminOEncargado(authentication);
        Repuesto repuesto = repuestoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Repuesto no encontrado: " + id));
        model.addAttribute("repuesto", repuesto);
        model.addAttribute("marcas", marcaRepository.findAllByOrderByNombreAsc());
        model.addAttribute("proveedores", proveedorRepository.findAll());
        model.addAttribute("esNuevo", false);
        return "repuestos/repuesto-form";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id,
                             @ModelAttribute Repuesto repuesto,
                             @RequestParam(required = false) Long proveedorId,
                             Authentication authentication) {
        exigirAdminOEncargado(authentication);
        repuesto.setId(id);
        if (proveedorId != null) {
            Proveedor proveedor = proveedorRepository.findById(proveedorId).orElse(null);
            repuesto.setProveedor(proveedor);
        } else {
            repuesto.setProveedor(null);
        }
        repuestoRepository.save(repuesto);
        return "redirect:/repuestos";
    }

    @PostMapping("/{id}/ajustar-stock")
    public String ajustarStock(@PathVariable Long id,
                               @RequestParam Integer cambio,
                               Authentication authentication) {
        exigirAdminOEncargado(authentication);
        Repuesto repuesto = repuestoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Repuesto no encontrado: " + id));
        int nuevoStock = Math.max(0, repuesto.getCantidadStock() + cambio);
        repuesto.setCantidadStock(nuevoStock);
        repuestoRepository.save(repuesto);
        return "redirect:/repuestos";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, Authentication authentication) {
        exigirAdminOEncargado(authentication);
        repuestoRepository.deleteById(id);
        return "redirect:/repuestos";
    }

    private boolean esAdminOEncargado(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().matches("ROLE_(ADMIN|ENCARGADO|SUPERVISOR)"));
    }

    private void exigirAdminOEncargado(Authentication authentication) {
        if (!esAdminOEncargado(authentication)) {
            throw new org.springframework.security.access.AccessDeniedException("No autorizado");
        }
    }
}

