package com.solumeca.controller;

import com.solumeca.model.Marca;
import com.solumeca.model.Proveedor;
import com.solumeca.repository.MarcaRepository;
import com.solumeca.repository.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/marcas")
public class MarcaController {

    @Autowired
    private MarcaRepository marcaRepository;

    @Autowired
    private ProveedorRepository proveedorRepository;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("marcas", marcaRepository.findAllByOrderByNombreAsc());
        return "marcas/marcas-lista";
    }

    @GetMapping("/nueva")
    public String formularioNueva(Model model) {
        model.addAttribute("marca", new Marca());
        model.addAttribute("proveedores", proveedorRepository.findAll());
        model.addAttribute("esNueva", true);
        return "marcas/marca-form";
    }

    @PostMapping
    public String guardar(@ModelAttribute Marca marca, @RequestParam(required = false) Long proveedorId) {
        if (proveedorId != null) {
            Proveedor proveedor = proveedorRepository.findById(proveedorId).orElse(null);
            marca.setProveedor(proveedor);
        }
        marcaRepository.save(marca);
        return "redirect:/admin/marcas";
    }

    @GetMapping("/{id}/editar")
    public String formularioEditar(@PathVariable Long id, Model model) {
        Marca marca = marcaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Marca no encontrada: " + id));
        model.addAttribute("marca", marca);
        model.addAttribute("proveedores", proveedorRepository.findAll());
        model.addAttribute("esNueva", false);
        return "marcas/marca-form";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id, @ModelAttribute Marca marca, @RequestParam(required = false) Long proveedorId) {
        marca.setId(id);
        if (proveedorId != null) {
            Proveedor proveedor = proveedorRepository.findById(proveedorId).orElse(null);
            marca.setProveedor(proveedor);
        } else {
            marca.setProveedor(null);
        }
        marcaRepository.save(marca);
        return "redirect:/admin/marcas";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id) {
        marcaRepository.deleteById(id);
        return "redirect:/admin/marcas";
    }
}

