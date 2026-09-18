package com.solumeca.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDeniedException(AccessDeniedException ex, RedirectAttributes redirectAttributes) {
        logger.warn("Acceso denegado: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("errorMensaje", "No tiene permisos suficientes para realizar esta acción.");
        return "redirect:/mantenimientos";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex, RedirectAttributes redirectAttributes) {
        logger.error("Error no controlado capturado en el servidor: ", ex);
        redirectAttributes.addFlashAttribute("errorMensaje", "Ocurrió un inconveniente al procesar la solicitud. Intente nuevamente.");
        return "redirect:/mantenimientos";
    }
}
