package com.solumeca;

import com.solumeca.controller.MantenimientoController;
import com.solumeca.model.Mantenimiento;
import com.solumeca.repository.ArchivoAdjuntoRepository;
import com.solumeca.repository.MantenimientoRepository;
import com.solumeca.repository.MaquinariaRepository;
import com.solumeca.repository.MarcaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class MantenimientoControllerMvcTest {

    @Mock
    private MantenimientoRepository mantenimientoRepository;
    @Mock
    private MaquinariaRepository maquinariaRepository;
    @Mock
    private MarcaRepository marcaRepository;
    @Mock
    private ArchivoAdjuntoRepository archivoAdjuntoRepository;

    @InjectMocks
    private MantenimientoController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testDiagnosticarConRolAdminExitoso() throws Exception {
        // Gerente con ROLE_ADMIN diagnosticando o testeando el diagnóstico
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "gerente", "gerente123", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        Mantenimiento m = new Mantenimiento();
        m.setId(8L);
        m.setEstado("Presolicitud");
        when(mantenimientoRepository.findById(8L)).thenReturn(Optional.of(m));
        when(mantenimientoRepository.save(any(Mantenimiento.class))).thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(multipart("/mantenimientos/8/diagnosticar")
                        .param("analisis", "Revisión técnica por Gerencia/Taller")
                        .param("solucion", "Ajuste general y reemplazo de retenes")
                        .param("diasEstimados", "3")
                        .principal(auth))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mantenimientos"));

        assertEquals("Diagnosticado", m.getEstado());
        assertEquals("Revisión técnica por Gerencia/Taller", m.getAnalisis());
    }

    @Test
    void testDiagnosticarConRolTecnicoExitoso() throws Exception {
        // Tecnico con ROLE_TECNICO
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "tecnico", "tecnico123", List.of(new SimpleGrantedAuthority("ROLE_TECNICO")));

        Mantenimiento m = new Mantenimiento();
        m.setId(8L);
        m.setEstado("Presolicitud");
        when(mantenimientoRepository.findById(8L)).thenReturn(Optional.of(m));
        when(mantenimientoRepository.save(any(Mantenimiento.class))).thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(multipart("/mantenimientos/8/diagnosticar")
                        .param("analisis", "Causa detectada por técnico")
                        .param("solucion", "Procedimiento de calibración")
                        .param("diasEstimados", "2")
                        .principal(auth))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mantenimientos"));

        assertEquals("Diagnosticado", m.getEstado());
        assertEquals("Causa detectada por técnico", m.getAnalisis());
    }

    @Test
    void testDiagnosticarIdInexistenteRedirigeSinError500() throws Exception {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "tecnico", "tecnico123", List.of(new SimpleGrantedAuthority("ROLE_TECNICO")));

        when(mantenimientoRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(multipart("/mantenimientos/999/diagnosticar")
                        .param("analisis", "Prueba")
                        .param("solucion", "Prueba")
                        .principal(auth))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mantenimientos"));
    }

    @Test
    void testCotizarExclusivoGerente() throws Exception {
        // Gerente cotiza con éxito
        UsernamePasswordAuthenticationToken authGerente = new UsernamePasswordAuthenticationToken(
                "gerente", "gerente123", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        Mantenimiento m = new Mantenimiento();
        m.setId(8L);
        m.setEstado("Diagnosticado");
        when(mantenimientoRepository.findById(8L)).thenReturn(Optional.of(m));
        when(mantenimientoRepository.save(any(Mantenimiento.class))).thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(post("/mantenimientos/8/cotizar")
                        .param("costoEstimado", "1500000.0")
                        .param("diasEstimados", "3")
                        .principal(authGerente))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mantenimientos"));

        assertEquals("Cotizada", m.getEstado());
        assertEquals(1500000.0, m.getCostoEstimado());

        // Tecnico NO puede cotizar, redirige a /mantenimientos
        UsernamePasswordAuthenticationToken authTecnico = new UsernamePasswordAuthenticationToken(
                "tecnico", "tecnico123", List.of(new SimpleGrantedAuthority("ROLE_TECNICO")));

        mockMvc.perform(post("/mantenimientos/8/cotizar")
                        .param("costoEstimado", "2000000.0")
                        .principal(authTecnico))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mantenimientos"));
    }
}

