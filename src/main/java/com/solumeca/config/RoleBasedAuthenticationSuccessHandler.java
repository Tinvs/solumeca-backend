package com.solumeca.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Despues de un login exitoso, revisa el rol del usuario autenticado
 * y lo redirige al panel que le corresponde.
 */
@Component
public class RoleBasedAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Authentication authentication) throws IOException, ServletException {

        String rol = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("");

        String destino = switch (rol) {
            case "ROLE_ADMIN" -> "/admin/dashboard";
            case "ROLE_CLIENTE" -> "/cliente/dashboard";
            case "ROLE_TECNICO" -> "/tecnico/dashboard";
            case "ROLE_SUPERVISOR" -> "/supervisor/dashboard";
            case "ROLE_ENCARGADO" -> "/encargado/dashboard";
            default -> "/index.html";
        };

        response.sendRedirect(destino);
    }
}
