package com.solumeca.config;

import com.solumeca.model.Usuario;
import com.solumeca.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DefaultUsersInitializer {

    @Bean
    CommandLineRunner initializeDefaultUsers(UsuarioRepository usuarioRepository,
                                              PasswordEncoder passwordEncoder) {
        return args -> {
            createIfMissing(usuarioRepository, passwordEncoder, "gerente", "gerente123", "ADMIN");
            createIfMissing(usuarioRepository, passwordEncoder, "usuario", "usuario123", "CLIENTE");
            createIfMissing(usuarioRepository, passwordEncoder, "tecnico", "tecnico123", "TECNICO");
            createIfMissing(usuarioRepository, passwordEncoder, "supervisor", "supervisor123", "SUPERVISOR");
        };
    }

    private void createIfMissing(UsuarioRepository usuarioRepository,
                                 PasswordEncoder passwordEncoder,
                                 String username,
                                 String password,
                                 String role) {
        if (usuarioRepository.findByUsername(username).isPresent()) {
            return;
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setRol(role);
        usuarioRepository.save(usuario);
    }
}