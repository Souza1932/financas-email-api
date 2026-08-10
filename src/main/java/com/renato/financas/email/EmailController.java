package com.renato.financas.email;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/email")
// Libera chamadas vindas do app Electron. Em produção, restrinja a origin se possível.
@CrossOrigin(origins = "*")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    // Chamado pelo login.js na função cadastrarUsuario(), ao final do cadastro
    @PostMapping("/confirmacao")
    public ResponseEntity<?> confirmacaoCadastro(@Valid @RequestBody ConfirmacaoCadastroDTO dto) {
        try {
            emailService.enviarConfirmacaoCadastro(dto.getNome(), dto.getEmail());
            return ResponseEntity.ok(Map.of("ok", true));
        } catch (MailException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("ok", false, "erro", "Falha ao enviar e-mail de confirmação."));
        }
    }

    // Chamado pelo login.js na função notificarLogin(), após login bem-sucedido (com ou sem 2FA)
    @PostMapping("/notificacao-login")
    public ResponseEntity<?> notificacaoLogin(@Valid @RequestBody NotificacaoLoginDTO dto) {
        try {
            emailService.enviarNotificacaoLogin(dto.getNome(), dto.getEmail());
            return ResponseEntity.ok(Map.of("ok", true));
        } catch (MailException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("ok", false, "erro", "Falha ao enviar e-mail de notificação."));
        }
    }
}
