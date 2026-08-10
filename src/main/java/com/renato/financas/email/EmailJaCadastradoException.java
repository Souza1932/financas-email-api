package com.renato.financas.email;

/** Lançada ao tentar cadastrar um e-mail que já existe. */
public class EmailJaCadastradoException extends RuntimeException {
    public EmailJaCadastradoException(String email) {
        super("Este e-mail já está cadastrado: " + email);
    }
}
