package com.zarpar.config;

import com.zarpar.domain.Role;
import com.zarpar.domain.Usuario;
import com.zarpar.repository.UsuarioRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class InitialDataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;

    public InitialDataLoader(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        boolean anyAdmin = usuarioRepository.findAll().stream()
                .anyMatch(u -> u.getRole() == Role.ADMIN);

        if (!anyAdmin) {
            String email = "nuninhos@zarpar.local";
            if (usuarioRepository.existsByEmail(email)) {
                usuarioRepository.findByEmail(email).ifPresent(u -> {
                    u.setRole(Role.ADMIN);
                    usuarioRepository.save(u);
                });
            } else {
                String senha = "dados1";
                String hash = BCrypt.hashpw(senha, BCrypt.gensalt(12));
                Usuario admin = new Usuario("The King Nunes", email, hash, Role.ADMIN);
                usuarioRepository.save(admin);
                System.out.println("Usuário ADMIN criado: " + email + " / " + senha);
            }
        }
    }
}
