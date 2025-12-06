package com.zarpar.service;

import com.zarpar.domain.Foto;
import com.zarpar.domain.PontoTuristico;
import com.zarpar.domain.Role;
import com.zarpar.domain.Usuario;
import com.zarpar.repository.FotoRepository;
import com.zarpar.repository.PontoTuristicoRepository;
import com.zarpar.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class FotoService {

    private final FotoRepository fotoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PontoTuristicoRepository pontoTuristicoRepository;
    private final Path fileStorageLocation;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");

    public FotoService(
            FotoRepository fotoRepository,
            UsuarioRepository usuarioRepository,
            PontoTuristicoRepository pontoTuristicoRepository,
            @Value("${app.upload.dir}")
            String uploadDir
    ) {
        this.fotoRepository = fotoRepository;
        this.usuarioRepository = usuarioRepository;
        this.pontoTuristicoRepository = pontoTuristicoRepository;

        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new RuntimeException("Não foi possível criar o diretório de uploads.", ex);
        }
    }

    public List<Foto> listarPorPonto(Long pontoId) {
        return fotoRepository.findByPontoIdOrderByDataDesc(pontoId);
    }

    public Foto salvar(Long pontoId, Long usuarioId, MultipartFile file, String titulo, String descricao) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        if (fotoRepository.countByPontoId(pontoId) >= 10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Limite de 10 fotos atingido.");
        }

        String originalName = file.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf(".") + 1).toLowerCase();
        }

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Apenas imagens (JPG, PNG, WEBP) são aceitas.");
        }

        String filename = UUID.randomUUID().toString() + "." + extension;
        try {
            Path targetLocation = this.fileStorageLocation.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new RuntimeException("Erro ao salvar arquivo " + filename, ex);
        }

        PontoTuristico ponto = pontoTuristicoRepository.findById(pontoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ponto não encontrado"));

        Foto foto = new Foto(pontoId, usuarioId, usuario.getNome(), filename, titulo, descricao, file.getContentType());
        foto = fotoRepository.save(foto);

        if (ponto.getCapaFilename() == null) {
            ponto.setCapaFilename(filename);
            pontoTuristicoRepository.save(ponto);
        }

        return foto;
    }

    public void excluir(String id, Long usuarioId) {
        Foto foto = fotoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        if (!foto.getUsuarioId().equals(usuarioId) && usuario.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sem permissão");
        }

        PontoTuristico ponto = pontoTuristicoRepository.findById(foto.getPontoId()).orElseThrow();
        if (foto.getFilename().equals(ponto.getCapaFilename())) {
            ponto.setCapaFilename(null);
            pontoTuristicoRepository.save(ponto);
        }

        fotoRepository.delete(foto);

        try {
            Path filePath = this.fileStorageLocation.resolve(foto.getFilename());
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            System.err.println("Erro ao deletar arquivo físico: " + ex.getMessage());
        }
    }

    public Resource carregarArquivo(String filename) {
        try {
            Path filePath = this.fileStorageLocation.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Arquivo não encontrado");
            }
        } catch (MalformedURLException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Arquivo não encontrado");
        }
    }

    public void deletarTodasDoPonto(Long pontoId) {
        List<Foto> fotos = fotoRepository.findByPontoIdOrderByDataDesc(pontoId);

        for (Foto foto : fotos) {
            try {
                Path filePath = this.fileStorageLocation.resolve(foto.getFilename());
                Files.deleteIfExists(filePath);
            } catch (IOException ex) {
                System.err.println("Erro ao apagar arquivo físico: " + foto.getFilename());
            }
        }

        fotoRepository.deleteAll(fotos);
    }
}