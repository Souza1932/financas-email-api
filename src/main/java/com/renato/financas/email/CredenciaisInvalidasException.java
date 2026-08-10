package com.renato.financas.email;

/**
 * Lançada quando e-mail ou senha estão incorretos.
 * Propositalmente usa a MESMA mensagem tanto para "e-mail não encontrado"
 * quanto para "senha incorreta" — nunca revela qual dos dois está errado,
 * pra não dar pistas a quem está tentando adivinhar contas existentes.
 */
public class CredenciaisInvalidasException extends RuntimeException {
    public CredenciaisInvalidasException() {
        super("E-mail ou senha incorretos.");
    }
}
