package com.zarpar.service;

import com.zarpar.domain.Role;
import com.zarpar.domain.Usuario;
import com.zarpar.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João Silva");
        usuario.setEmail("joao@test.com");
        usuario.setSenhaHash(BCrypt.hashpw("senha123", BCrypt.gensalt()));
        usuario.setRole(Role.USER);
    }

    @Test
    void deveCriarUsuarioComSucesso() {
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario result = usuarioService.register("João Silva", "joao@test.com", "senha123");

        assertNotNull(result);
        assertEquals("João Silva", result.getNome());
        assertEquals("joao@test.com", result.getEmail());

        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void deveVerificarSeEmailExiste() {
        when(usuarioRepository.existsByEmail("joao@test.com")).thenReturn(true);

        boolean exists = usuarioService.emailExists("joao@test.com");

        assertTrue(exists);
        verify(usuarioRepository).existsByEmail("joao@test.com");
    }

    @Test
    void deveBuscarUsuarioPorEmail() {
        when(usuarioRepository.findByEmail("joao@test.com")).thenReturn(Optional.of(usuario));

        Optional<Usuario> result = usuarioService.findByEmail("joao@test.com");

        assertTrue(result.isPresent());
        assertEquals("João Silva", result.get().getNome());
        verify(usuarioRepository).findByEmail("joao@test.com");
    }

    @Test
    void deveBuscarUsuarioPorId() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Optional<Usuario> result = usuarioService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(usuarioRepository).findById(1L);
    }

    @Test
    void deveVerificarSenhaCorreta() {
        boolean isValid = usuarioService.checkPassword(usuario, "senha123");

        assertTrue(isValid);
    }

    @Test
    void deveRetornarFalsoParaSenhaIncorreta() {
        boolean isValid = usuarioService.checkPassword(usuario, "senhaErrada");

        assertFalse(isValid);
    }

    @Test
    void devePermitirAlteracaoParaAdmin() {
        Usuario admin = new Usuario();
        admin.setId(2L);
        admin.setRole(Role.ADMIN);

        Usuario outroUsuario = new Usuario();
        outroUsuario.setId(3L);
        outroUsuario.setRole(Role.USER);

        boolean temPermissao = usuarioService.temPermissaoDeAlteracao(admin, outroUsuario);

        assertTrue(temPermissao);
    }

    @Test
    void devePermitirAlteracaoParaProprioUsuario() {
        boolean temPermissao = usuarioService.temPermissaoDeAlteracao(usuario, usuario);

        assertTrue(temPermissao);
    }

    @Test
    void naoDevePermitirAlteracaoParaOutroUsuarioComum() {
        Usuario outroUsuario = new Usuario();
        outroUsuario.setId(2L);
        outroUsuario.setRole(Role.USER);

        boolean temPermissao = usuarioService.temPermissaoDeAlteracao(usuario, outroUsuario);

        assertFalse(temPermissao);
    }

    @Test
    void devePromoverUsuarioParaAdmin() {
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario promoted = usuarioService.promoteToAdminIfNeeded(usuario);

        assertEquals(Role.ADMIN, promoted.getRole());
        verify(usuarioRepository).save(usuario);
    }
}
