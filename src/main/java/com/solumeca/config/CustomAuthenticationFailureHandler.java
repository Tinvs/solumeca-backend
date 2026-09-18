package com.solumeca.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Distingue cuando un usuario no esta registrado de cuando escribe una contrasena incorrecta.
 */
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        if (exception instanceof UsernameNotFoundException) {
            response.sendRedirect("/login.html?error=not_registered");
        } else if (exception instanceof BadCredentialsException) {
            response.sendRedirect("/login.html?error=bad_credentials");
        } else {
            response.sendRedirect("/login.html?error=true");
        }
    }
}
