package com.solumeca.controller;

import com.solumeca.model.Usuario;
import com.solumeca.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
public class ClienteController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/cliente/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String username = (authentication != null && authentication.getName() != null) ? authentication.getName() : "cliente";
        model.addAttribute("username", username);
        if (usuarioRepository != null) {
            Optional<Usuario> opt = usuarioRepository.findByUsername(username);
            opt.ifPresent(u -> {
                if (u.getNombreEmpresa() != null && !u.getNombreEmpresa().isBlank()) {
                    model.addAttribute("nombreEmpresa", u.getNombreEmpresa());
                }
                model.addAttribute("tipoCliente", u.getTipoCliente());
            });
        }
        return "cliente-dashboard";
    }
}
