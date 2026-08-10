package com.renato.financas.email;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/2fa")
// Libera chamadas vindas do app Electron. Em produção, restrinja a origin se possível.
@CrossOrigin(origins = "*")
public class TwoFactorController {

    private final TwoFactorService twoFactorService;
    private final EmailService emailService;

    public TwoFactorController(TwoFactorService twoFactorService, EmailService emailService) {
        this.twoFactorService = twoFactorService;
        this.emailService = emailService;
    }

    // Chamado pelo login.js sempre que um usuário com 2FA ativo clica em "Entrar".
    // Gera um código de 6 caracteres (letras, números e especiais), válido por 5 minutos,
    // guarda-o no servidor e o envia por e-mail.
    @PostMapping("/enviar")
    public ResponseEntity<?> enviarCodigo(@Valid @RequestBody EnviarCodigo2FADTO dto) {
        String codigo = twoFactorService.gerarNovoCodigo(dto.getEmail());

        try {
            emailService.enviarCodigo2FA(dto.getNome(), dto.getEmail(), codigo);
            return ResponseEntity.ok(Map.of("ok", true));
        } catch (MailException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("ok", false, "erro", "Falha ao enviar o código de verificação."));
        }
    }

    // Chamado pelo login.js quando o usuário digita o código recebido por e-mail.
    // A verificação acontece aqui no servidor (não no front-end).
    @PostMapping("/verificar")
    public ResponseEntity<?> verificarCodigo(@Valid @RequestBody VerificarCodigo2FADTO dto) {
        TwoFactorService.ResultadoVerificacao resultado = twoFactorService.verificar(dto.getEmail(), dto.getCodigo());

        return switch (resultado) {
            case VALIDO -> ResponseEntity.ok(Map.of("ok", true));
            case INCORRETO -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("ok", false, "erro", "Código incorreto."));
            case EXPIRADO -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("ok", false, "erro", "Código expirado. Solicite um novo código."));
            case NAO_ENCONTRADO -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("ok", false, "erro", "Nenhum código pendente. Solicite um novo código."));
        };
    }
}
