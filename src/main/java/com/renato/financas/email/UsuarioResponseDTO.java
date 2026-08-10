package com.renato.financas.email;

/**
 * O que a API devolve ao front-end depois de cadastro/login.
 * Propositalmente NUNCA inclui a senha (nem o hash) — o login.js usa o
 * campo {@code id} apenas para isolar as chaves do localStorage por conta
 * (ex.: "despesas_" + id), nunca para autenticação futura.
 */
public class UsuarioResponseDTO {

    private final Long id;
    private final String nome;
    private final String email;

    public UsuarioResponseDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
}
