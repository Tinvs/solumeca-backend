package com.solumeca;

import com.solumeca.model.Mantenimiento;
import com.solumeca.model.Maquinaria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;
import org.thymeleaf.context.IContext;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TemplateRenderingTest {

    private SpringTemplateEngine templateEngine;
    private JakartaServletWebApplication application;

    @BeforeEach
    void setUp() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(false);

        templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(resolver);
        application = JakartaServletWebApplication.buildApplication(new MockServletContext());
    }

    private IContext createContext(Map<String, Object> variables) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        var webExchange = application.buildExchange(request, response);
        var ctx = new org.thymeleaf.context.WebContext(webExchange, Locale.getDefault());
        variables.forEach(ctx::setVariable);
        return ctx;
    }

    @Test
    void testRenderMantenimientosListaConDatosVacios() {
        Map<String, Object> vars = new HashMap<>();
        vars.put("mantenimientos", Collections.emptyList());
        vars.put("maquinasMap", Collections.emptyMap());
        vars.put("maquinasList", Collections.emptyList());
        vars.put("esOperativo", true);
        vars.put("esTecnico", true);
        vars.put("esAdmin", true);
        vars.put("esCliente", false);

        String html = templateEngine.process("mantenimientos-lista", createContext(vars));
        assertNotNull(html);
        assertTrue(html.contains("Gestión de Mantenimientos") || html.contains("Mantenimientos"));
    }

    @Test
    void testRenderMantenimientosListaConMantenimientoNullMaquinaria() {
        Mantenimiento m = new Mantenimiento();
        m.setId(1L);
        m.setMaquinariaId(null);
        m.setSolicitante("martin");
        m.setTipo("Correctivo");
        m.setDescripcion("Falla general en motor");
        m.setEstado("Completado");
        m.setFecha(LocalDate.now());
        m.setSolucion("Reparación estándar");

        Map<String, Object> vars = new HashMap<>();
        vars.put("mantenimientos", List.of(m));
        vars.put("maquinasMap", Collections.emptyMap());
        vars.put("maquinasList", Collections.emptyList());
        vars.put("esOperativo", true);
        vars.put("esTecnico", true);
        vars.put("esAdmin", true);
        vars.put("esCliente", true);

        String html = templateEngine.process("mantenimientos-lista", createContext(vars));
        assertNotNull(html);
        assertTrue(html.contains("ORD-2026-006"));
    }
}
