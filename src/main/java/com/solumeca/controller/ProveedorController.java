package com.solumeca.controller;

import com.solumeca.model.Proveedor;
import com.solumeca.repository.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/proveedores")
public class ProveedorController {

    @Autowired
    private ProveedorRepository proveedorRepository;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("proveedores", proveedorRepository.findAll());
        return "proveedores/proveedores-lista";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("proveedor", new Proveedor());
        model.addAttribute("esNuevo", true);
        return "proveedores/proveedor-form";
    }

    @PostMapping
    public String guardar(@ModelAttribute Proveedor proveedor) {
        proveedorRepository.save(proveedor);
        return "redirect:/admin/proveedores";
    }

    @GetMapping("/{id}/editar")
    public String formularioEditar(@PathVariable Long id, Model model) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado: " + id));
        model.addAttribute("proveedor", proveedor);
        model.addAttribute("esNuevo", false);
        return "proveedores/proveedor-form";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id, @ModelAttribute Proveedor proveedor) {
        proveedor.setId(id);
        proveedorRepository.save(proveedor);
        return "redirect:/admin/proveedores";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id) {
        proveedorRepository.deleteById(id);
        return "redirect:/admin/proveedores";
    }
}

