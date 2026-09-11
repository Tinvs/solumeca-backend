package com.solumeca;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerarHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println("gerente123 = " + encoder.encode("gerente123"));
        System.out.println("usuario123 = " + encoder.encode("usuario123"));
        System.out.println("tecnico123 = " + encoder.encode("tecnico123"));
        System.out.println("supervisor123 = " + encoder.encode("supervisor123"));
    }
}
