package com.renato.financas.email;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/senha")
@CrossOrigin(origins = "*")
public class RecuperacaoSenhaController {

    private final RecuperacaoSenhaService recuperacaoSenhaService;
    private final UsuarioService usuarioService;
    private final EmailService emailService;

    public RecuperacaoSenhaController(RecuperacaoSenhaService recuperacaoSenhaService,
                                       UsuarioService usuarioService,
                                       EmailService emailService) {
        this.recuperacaoSenhaService = recuperacaoSenhaService;
        this.usuarioService = usuarioService;
        this.emailService = emailService;
    }

    // Chamado pelo login.js quando o usuário clica em "Esqueceu a senha?" e digita o e-mail.
    // Sempre responde ok:true, exista ou não a conta — assim não revelamos
    // pra quem pergunta se um e-mail está cadastrado no sistema.
    @PostMapping("/esqueci")
    public ResponseEntity<?> esqueciSenha(@Valid @RequestBody SolicitarRecuperacaoDTO dto) {
        Usuario usuario = usuarioService.buscarPorEmailOuNulo(dto.getEmail());

        if (usuario != null) {
            String codigo = recuperacaoSenhaService.gerarNovoCodigo(usuario.getEmail());
            try {
                emailService.enviarCodigoRecuperacaoSenha(usuario.getNome(), usuario.getEmail(), codigo);
            } catch (Exception e) {
                // Falha silenciosa: não expõe se o e-mail existe ou se o SMTP falhou.
            }
        }

        return ResponseEntity.ok(Map.of("ok", true));
    }

    // Chamado pelo login.js quando o usuário digita o código + a nova senha.
    @PostMapping("/redefinir")
    public ResponseEntity<?> redefinirSenha(@Valid @RequestBody RedefinirSenhaDTO dto) {
        String emailNormalizado = dto.getEmail().trim().toLowerCase();
        RecuperacaoSenhaService.ResultadoVerificacao resultado =
                recuperacaoSenhaService.verificar(emailNormalizado, dto.getCodigo());

        return switch (resultado) {
            case VALIDO -> {
                usuarioService.redefinirSenha(emailNormalizado, dto.getNovaSenha());
                yield ResponseEntity.ok(Map.of("ok", true));
            }
            case INCORRETO -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("ok", false, "erro", "Código incorreto."));
            case EXPIRADO -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("ok", false, "erro", "Código expirado. Solicite um novo código."));
            case NAO_ENCONTRADO -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("ok", false, "erro", "Nenhum código pendente. Solicite um novo código."));
        };
    }
}












































