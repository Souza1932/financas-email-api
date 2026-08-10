package com.renato.financas.email;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.stereotype.Service;

/**
 * Hash e verificação de senha com Argon2id (variante híbrida do Argon2,
 * recomendada pela própria especificação do algoritmo para uso geral —
 * resistente tanto a ataques de canal lateral quanto a ataques com GPU).
 *
 * Parâmetros usados (mesmos em todo o sistema):
 *  - iterações:   10
 *  - memória:     65536 KB (64 MB)
 *  - paralelismo: 4 threads
 */
@Service
public class SenhaService {

    private static final int ITERACOES   = 10;
    private static final int MEMORIA_KB  = 65536;
    private static final int PARALELISMO = 4;

    private final Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);

    /** Gera o hash Argon2id da senha em texto puro. */
    public String gerarHash(String senhaTextoPuro) {
        char[] senhaChars = senhaTextoPuro.toCharArray();
        try {
            return argon2.hash(ITERACOES, MEMORIA_KB, PARALELISMO, senhaChars);
        } finally {
            // Limpa a senha da memória assim que possível (boa prática do Argon2).
            argon2.wipeArray(senhaChars);
        }
    }

    /** Verifica se a senha em texto puro corresponde ao hash armazenado. */
    public boolean verificar(String hashArmazenado, String senhaTextoPuro) {
        char[] senhaChars = senhaTextoPuro.toCharArray();
        try {
            return argon2.verify(hashArmazenado, senhaChars);
        } finally {
            argon2.wipeArray(senhaChars);
        }
    }
}
