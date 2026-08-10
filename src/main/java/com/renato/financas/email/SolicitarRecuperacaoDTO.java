package com.renato.financas.email;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// Corresponde ao body enviado pelo login.js para /api/senha/esqueci:
// JSON.stringify({ email })
public class SolicitarRecuperacaoDTO {

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "E-mail inválido.")
    private String email;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
