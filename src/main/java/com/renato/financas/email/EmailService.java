
package com.renato.financas.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    // E-mail remetente, configurado em application.properties (mail.remetente)
    @Value("${mail.remetente}")
    private String remetente;
    // Nome de exibição do remetente (o que aparece na caixa de entrada do destinatário)
    @Value("${mail.remetente.nome:Finance}")
    private String nomeRemetente;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Envia o e-mail de confirmação de cadastro.
     * Usado pelo login.js em: POST /api/email/confirmacao
     */
    public void enviarConfirmacaoCadastro(String nomeDestinatario, String emailDestinatario) throws MailException {
        String assunto = "Bem-vindo(a) ao Finanças!";
        String corpo = "<p>Olá, " + nomeDestinatario + "!</p>"
                     + "<p>Seu cadastro no <strong>Finanças</strong> foi realizado com sucesso.</p>"
                     + "<p>Agora você já pode organizar suas contas e acompanhar seus gastos.</p>";

        enviarHtml(emailDestinatario, assunto, corpo);
    }

    /**
     * Envia o e-mail de notificação de novo login.
     * Usado pelo login.js em: POST /api/email/notificacao-login
     */
    public void enviarNotificacaoLogin(String nomeDestinatario, String emailDestinatario) throws MailException {
        String dataHoraFormatada = formatarDataHoraAtual();

        String assunto = "Novo login detectado na sua conta";
        String corpo = "<p>Olá, " + nomeDestinatario + "!</p>"
                     + "<p>Detectamos um novo acesso à sua conta em <strong>" + dataHoraFormatada + "</strong>.</p>"
                     + "<p>Se não foi você, recomendamos trocar sua senha imediatamente.</p>";

        enviarHtml(emailDestinatario, assunto, corpo);
    }

    /**
     * Envia o e-mail com o código de verificação da Autenticação de Dois Fatores.
     * O código é gerado e validado no servidor (TwoFactorService/TwoFactorController);
     * esta função apenas o envia por e-mail.
     * Usado pelo TwoFactorController em: POST /api/2fa/enviar
     */
    public void enviarCodigo2FA(String nomeDestinatario, String emailDestinatario, String codigo) throws MailException {
        String assunto = "Seu código de verificação - Finanças";
        String corpo = "<p>Olá, " + nomeDestinatario + "!</p>"
                     + "<p>Use o código abaixo para concluir seu login com Autenticação de Dois Fatores:</p>"
                     + "<p style=\"font-size:24px; font-weight:bold; letter-spacing:4px; margin:16px 0;\">" + codigo + "</p>"
                     + "<p>Este código expira em <strong>5 minutos</strong>.</p>"
                     + "<p>Se você não tentou fazer login, ignore este e-mail e considere trocar sua senha.</p>";

        enviarHtml(emailDestinatario, assunto, corpo);
    }

    /**
     * Envia o e-mail avisando que a conta foi excluída permanentemente.
     * Usado pelo UsuarioController em: DELETE /api/usuarios/{id}
     */
    public void enviarConfirmacaoExclusao(String nomeDestinatario, String emailDestinatario) throws MailException {
        String dataHoraFormatada = formatarDataHoraAtual();

        String assunto = "Sua conta foi excluída - Finanças";
        String corpo = "<p>Olá, " + nomeDestinatario + "!</p>"
                     + "<p>Confirmamos que sua conta no <strong>Finanças</strong> foi excluída permanentemente em <strong>" + dataHoraFormatada + "</strong>.</p>"
                     + "<p>Todos os seus dados de cadastro e informações financeiras foram removidos e não podem ser recuperados.</p>"
                     + "<p>Se você não solicitou essa exclusão, entre em contato com o suporte imediatamente.</p>";

        enviarHtml(emailDestinatario, assunto, corpo);
    }

    private String formatarDataHoraAtual() {
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy, HH:mm", new Locale("pt", "BR"));
        return LocalDateTime.now().format(formatador);
    }

    private void enviarHtml(String destinatario, String assunto, String corpoHtml) throws MailException {
        try {
            MimeMessage mensagem = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensagem, "UTF-8");

            helper.setFrom(remetente, nomeRemetente);
            helper.setTo(destinatario);
            helper.setSubject(assunto);
            helper.setText(corpoHtml, true); // true = corpo em HTML

            mailSender.send(mensagem);
        } catch (Exception e) {
            // Encapsula qualquer erro de montagem/envio como MailException,
            // pra o Controller conseguir tratar de forma uniforme.
            throw new org.springframework.mail.MailSendException("Falha ao enviar e-mail para " + destinatario, e);
        }
    }
    
    /**
     * Envia o e-mail com o código de redefinição de senha.
     * Usado pelo RecuperacaoSenhaController em: POST /api/senha/esqueci
     */
    public void enviarCodigoRecuperacaoSenha(String nomeDestinatario, String emailDestinatario, String codigo) throws MailException {
        String assunto = "Redefinição de senha - Finanças";
        String corpo = "<p>Olá, " + nomeDestinatario + "!</p>"
                     + "<p>Recebemos uma solicitação para redefinir a senha da sua conta. Use o código abaixo:</p>"
                     + "<p style=\"font-size:24px; font-weight:bold; letter-spacing:4px; margin:16px 0;\">" + codigo + "</p>"
                     + "<p>Este código expira em <strong>15 minutos</strong>.</p>"
                     + "<p>Se você não solicitou essa redefinição, ignore este e-mail — sua senha atual continua válida.</p>";

        enviarHtml(emailDestinatario, assunto, corpo);
    }
    
}


























