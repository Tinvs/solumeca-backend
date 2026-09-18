package com.solumeca.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ClienteController {

    @GetMapping("/cliente/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String username = (authentication != null && authentication.getName() != null) ? authentication.getName() : "cliente";
        model.addAttribute("username", username);
        return "cliente-dashboard";
    }
}
