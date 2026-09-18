package com.solumeca.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String redirectDashboard(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }

        String rol = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("");

        return switch (rol) {
            case "ROLE_ADMIN" -> "redirect:/admin/dashboard";
            case "ROLE_CLIENTE" -> "redirect:/cliente/dashboard";
            case "ROLE_TECNICO" -> "redirect:/tecnico/dashboard";
            case "ROLE_SUPERVISOR" -> "redirect:/supervisor/dashboard";
            case "ROLE_ENCARGADO" -> "redirect:/encargado/dashboard";
            default -> "redirect:/index.html";
        };
    }
}

