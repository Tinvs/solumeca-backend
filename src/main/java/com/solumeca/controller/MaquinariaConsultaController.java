package com.solumeca.controller;

import com.solumeca.repository.MaquinariaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MaquinariaConsultaController {

    @Autowired
    private MaquinariaRepository maquinariaRepository;

    @GetMapping("/maquinaria")
    public String consultar(Model model) {
        model.addAttribute("maquinas", maquinariaRepository.findAll());
        return "maquinaria-consulta";
    }
}
