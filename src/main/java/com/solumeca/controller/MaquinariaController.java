package com.solumeca.controller;

import com.solumeca.model.Maquinaria;
import com.solumeca.repository.MaquinariaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/maquinaria")
public class MaquinariaController {

    @Autowired
    private MaquinariaRepository maquinariaRepository;

    // Consultar: lista todas las maquinas registradas
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("maquinas", maquinariaRepository.findAll());
        return "admin/maquinaria-lista";
    }

    // Registrar: muestra el formulario vacio
    @GetMapping("/nueva")
    public String formularioNueva(Model model) {
        model.addAttribute("maquina", new Maquinaria());
        model.addAttribute("esNueva", true);
        return "admin/maquinaria-form";
    }

    // Registrar: guarda la maquina nueva
    @PostMapping
    public String guardar(@ModelAttribute Maquinaria maquina,
                          @RequestParam(required = false) String marcaPersonalizada) {
        aplicarMarcaPersonalizada(maquina, marcaPersonalizada);
        maquinariaRepository.save(maquina);
        return "redirect:/admin/maquinaria";
    }

    // Modificar: muestra el formulario con los datos actuales
    @GetMapping("/{id}/editar")
    public String formularioEditar(@PathVariable Long id, Model model) {
        Maquinaria maquina = maquinariaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maquina no encontrada"));
        model.addAttribute("maquina", maquina);
        model.addAttribute("esNueva", false);
        return "admin/maquinaria-form";
    }

    // Modificar: guarda los cambios
    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id, @ModelAttribute Maquinaria maquina,
                             @RequestParam(required = false) String marcaPersonalizada) {
        aplicarMarcaPersonalizada(maquina, marcaPersonalizada);
        maquina.setId(id);
        maquinariaRepository.save(maquina);
        return "redirect:/admin/maquinaria";
    }

    private void aplicarMarcaPersonalizada(Maquinaria maquina, String marcaPersonalizada) {
        if ("Otra".equals(maquina.getMarca()) && marcaPersonalizada != null
                && !marcaPersonalizada.trim().isEmpty()) {
            maquina.setMarca(marcaPersonalizada.trim());
        }
    }

    // Eliminar (complementa el CRUD)
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id) {
        maquinariaRepository.deleteById(id);
        return "redirect:/admin/maquinaria";
    }
}
