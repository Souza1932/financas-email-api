package com.renato.financas.email;

import jakarta.persistence.*;

/**
 * Representa um usuário cadastrado no sistema Finanças.
 *
 * IMPORTANTE (privacidade por design): esta tabela guarda APENAS os dados
 * estritamente necessários para autenticação (nome, e-mail e senha).
 * Nenhum dado financeiro (despesas, saldo, gráficos) é persistido aqui —
 * esses dados continuam exclusivamente no localStorage do app, isolados por
 * usuário através do campo {@code id} gerado automaticamente pelo MySQL.
 */
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, unique = true, length = 180)
    private String email;

    // Nunca armazena a senha em texto puro — sempre um hash Argon2id (ver SenhaService).
    // 255 caracteres: hashes Argon2id costumam ficar entre 95-110 caracteres
    // com os parâmetros usados aqui, mas deixamos folga para o futuro.
    @Column(nullable = false, length = 255)
    private String senha;

    protected Usuario() {
        // Construtor exigido pelo JPA/Hibernate.
    }

    public Usuario(String nome, String email, String senhaHash) {
        this.nome = nome;
        this.email = email;
        this.senha = senhaHash;
    }

    public Long getId() { return id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senhaHash) { this.senha = senhaHash; }
}
