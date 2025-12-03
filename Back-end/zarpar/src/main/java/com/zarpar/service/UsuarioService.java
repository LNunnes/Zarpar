package com.zarpar.service;

import com.zarpar.domain.Usuario;
import com.zarpar.domain.Role;
import com.zarpar.repository.UsuarioRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public boolean emailExists(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public boolean temPermissaoDeAlteracao(Usuario usuarioLogado, Usuario donoDoRecurso) {
        if (usuarioLogado.getRole() == Role.ADMIN) {
            return true;
        }
        return usuarioLogado.getId().equals(donoDoRecurso.getId());
    }

    @Transactional
    public Usuario register(String nome, String email, String senha) {
        String hashed = BCrypt.hashpw(senha, BCrypt.gensalt(12));
        Usuario u = new Usuario(nome, email, hashed, Role.USER);
        return usuarioRepository.save(u);
    }

    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    public boolean checkPassword(Usuario usuario, String rawSenha) {
        return BCrypt.checkpw(rawSenha, usuario.getSenhaHash());
    }

    @Transactional
    public Usuario promoteToAdminIfNeeded(Usuario usuario) {
        usuario.setRole(Role.ADMIN);
        return usuarioRepository.save(usuario);
    }
}
