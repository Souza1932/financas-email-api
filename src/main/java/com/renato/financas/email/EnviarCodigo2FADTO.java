package com.renato.financas.email;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// Corresponde ao body enviado pelo login.js para /api/2fa/enviar:
// JSON.stringify({ nome, email })
public class EnviarCodigo2FADTO {

    @NotBlank(message = "O nome é obrigatório.")
    private String nome;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "E-mail inválido.")
    private String email;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
