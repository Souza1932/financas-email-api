package com.renato.financas.email;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
// Libera chamadas vindas do app Electron. Em produção, restrinja a origin se possível.
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final EmailService emailService;

    public UsuarioController(UsuarioService usuarioService, EmailService emailService) {
        this.usuarioService = usuarioService;
        this.emailService = emailService;
    }

    // Chamado pelo login.js em cadastrarUsuario(), no lugar de gravar direto no localStorage.
    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastrar(@Valid @RequestBody CadastroDTO dto) {
        Usuario usuario = usuarioService.cadastrar(dto);

        // E-mail de boas-vindas: falha "silenciosa" (best-effort) — não deve
        // impedir o cadastro de ter sucesso se o SMTP estiver fora do ar.
        try {
            emailService.enviarConfirmacaoCadastro(usuario.getNome(), usuario.getEmail());
        } catch (Exception e) {
            // Intencional: cadastro já está salvo no banco, e-mail é só um extra.
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new UsuarioResponseDTO(usuario));
    }

    // Chamado pelo login.js em fazerLogin(), no lugar de validar contra o localStorage.
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO dto) {
        Usuario usuario = usuarioService.autenticar(dto);
        return ResponseEntity.ok(new UsuarioResponseDTO(usuario));
    }

    // Chamado pelo script.js em salvarPerfil(), ao editar nome/e-mail na tela de perfil.
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarPerfil(@PathVariable Long id, @Valid @RequestBody AtualizarPerfilDTO dto) {
        Usuario usuario = usuarioService.atualizarPerfil(id, dto);
        return ResponseEntity.ok(new UsuarioResponseDTO(usuario));
    }

    // Chamado pelo script.js em confirmarExclusaoConta(), na tela de perfil,
    // quando o usuário confirma que deseja excluir a conta permanentemente.
    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluirConta(@PathVariable Long id) {
        // Apaga o registro do usuário no MySQL e recupera nome/e-mail
        // (guardados em memória apenas para o e-mail de aviso abaixo).
        Usuario usuarioExcluido = usuarioService.excluirConta(id);

        // E-mail de aviso de exclusão: falha "silenciosa" (best-effort) —
        // a conta já foi apagada do banco, o e-mail é só um aviso extra.
        try {
            emailService.enviarConfirmacaoExclusao(usuarioExcluido.getNome(), usuarioExcluido.getEmail());
        } catch (Exception e) {
            // Intencional: exclusão já foi concluída, e-mail é só um extra.
        }

        return ResponseEntity.ok(Map.of("ok", true));
    }
}





