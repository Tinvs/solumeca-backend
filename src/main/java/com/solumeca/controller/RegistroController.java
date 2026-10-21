package com.solumeca.controller;

import com.solumeca.model.Usuario;
import com.solumeca.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Locale;
import java.util.regex.Pattern;

@Controller
public class RegistroController {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/registro")
    public String formulario() {
        return "forward:/registro.html";
    }

    @PostMapping("/registro")
    public String registrar(@RequestParam String nombre,
                            @RequestParam String correo,
                            @RequestParam String password,
                            @RequestParam(name = "tipoCliente", required = false, defaultValue = "PERSONA") String tipoCliente,
                            @RequestParam(name = "nombreEmpresa", required = false) String nombreEmpresa,
                            @RequestParam(name = "nit", required = false) String nit,
                            @RequestParam(name = "telefono", required = false) String telefono) {
        String username = nombre.trim();
        String email = correo.trim().toLowerCase(Locale.ROOT);

        if (username.length() < 3 || username.length() > 50) {
            return "redirect:/registro.html?error=nombre";
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return "redirect:/registro.html?error=correo";
        }
        if (password.length() < 8) {
            return "redirect:/registro.html?error=password";
        }
        if (usuarioRepository.findByUsername(username).isPresent()) {
            return "redirect:/registro.html?error=usuario";
        }
        if (usuarioRepository.existsByEmail(email)) {
            return "redirect:/registro.html?error=correo-existente";
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setRol("CLIENTE");

        boolean esEmpresa = "EMPRESA".equalsIgnoreCase(tipoCliente);
        usuario.setTipoCliente(esEmpresa ? "EMPRESA" : "PERSONA");
        if (esEmpresa) {
            usuario.setNombreEmpresa(nombreEmpresa != null && !nombreEmpresa.isBlank() ? nombreEmpresa.trim() : username);
            usuario.setNit(nit != null && !nit.isBlank() ? nit.trim() : null);
        } else {
            usuario.setNombreEmpresa(null);
            usuario.setNit(null);
        }
        usuario.setTelefono(telefono != null && !telefono.isBlank() ? telefono.trim() : null);

        usuarioRepository.save(usuario);

        return "redirect:/login.html?registered=true";
    }
}
