package com.solumeca.config;

import com.solumeca.service.UsuarioDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UsuarioDetailsService usuarioDetailsService;

    @Autowired
    private RoleBasedAuthenticationSuccessHandler successHandler;

    @Autowired
    private CustomAuthenticationFailureHandler failureHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(usuarioDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        provider.setHideUserNotFoundExceptions(false); // Distingue usuario no registrado vs credenciales invalidas
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.ignoringRequestMatchers("/login", "/registro", "/api/contacto"))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/", "/index.html", "/nosotros.html", "/servicios.html",
                    "/proyectos.html", "/contacto.html", "/login.html", "/registro.html", "/registro", "/api/contacto",
                    "/css/**", "/js/**", "/assets/**", "/api/session", "/error"
                ).permitAll()
                .requestMatchers("/repuestos/**").hasAnyRole("ADMIN", "SUPERVISOR", "TECNICO", "ENCARGADO")
                .requestMatchers("/admin/proveedores/**", "/admin/marcas/**").hasAnyRole("ADMIN", "SUPERVISOR", "ENCARGADO")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/cliente/**").hasRole("CLIENTE")
                .requestMatchers("/tecnico/**").hasRole("TECNICO")
                .requestMatchers("/supervisor/**").hasRole("SUPERVISOR")
                .requestMatchers("/encargado/**").hasRole("ENCARGADO")
                .requestMatchers("/mantenimientos/archivo/**").hasAnyRole("CLIENTE", "ADMIN", "SUPERVISOR", "TECNICO", "ENCARGADO")
                .requestMatchers("/mantenimientos/*/aprobar", "/mantenimientos/*/rechazar", "/mantenimientos/*/factura", "/mantenimientos/*/diagnosticar", "/mantenimientos/*/cotizar", "/mantenimientos/*/completar").hasAnyRole("CLIENTE", "ADMIN", "SUPERVISOR", "TECNICO", "ENCARGADO")
                .requestMatchers("/mantenimientos", "/mantenimientos/").hasAnyRole("CLIENTE", "ADMIN", "SUPERVISOR", "TECNICO", "ENCARGADO")
                .requestMatchers("/mantenimientos/cliente/**").hasAnyRole("CLIENTE", "ADMIN", "SUPERVISOR", "TECNICO", "ENCARGADO")
                .requestMatchers("/mantenimientos/tecnico/**").hasAnyRole("CLIENTE", "ADMIN", "SUPERVISOR", "TECNICO", "ENCARGADO")
                .requestMatchers("/mantenimientos/**").hasAnyRole("CLIENTE", "ADMIN", "SUPERVISOR", "TECNICO", "ENCARGADO")
                .requestMatchers("/maquinaria/**").hasAnyRole("CLIENTE", "ADMIN", "SUPERVISOR", "TECNICO", "ENCARGADO")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login.html")
                .loginProcessingUrl("/login")
                .successHandler(successHandler)
                .failureHandler(failureHandler)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/index.html")
                .permitAll()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .sessionFixation(sessionFixation -> sessionFixation.migrateSession())
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            )
            .headers(headers -> headers
                .contentTypeOptions(contentType -> {})
                .frameOptions(frame -> frame.deny())
                .httpStrictTransportSecurity(hsts -> hsts.disable())
            )
            .authenticationProvider(authenticationProvider());

        return http.build();
    }
}
