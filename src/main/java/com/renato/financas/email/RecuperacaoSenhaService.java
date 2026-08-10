package com.renato.financas.email;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gera, armazena e valida os códigos de redefinição de senha enviados por e-mail.
 * Mesmo esquema do TwoFactorService, porém com mapa próprio (não deve ser
 * confundido com códigos de 2FA) e validade maior (15 minutos, já que o
 * usuário precisa checar o e-mail e escolher uma nova senha).
 */
@Service
public class RecuperacaoSenhaService {

    private static final int TAMANHO_CODIGO = 6;
    private static final long VALIDADE_MINUTOS = 15;

    private static final String LETRAS   = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz";
    private static final String NUMEROS  = "23456789";
    private static final String ESPECIAIS = "!@#$%*+-=?";
    private static final String TODOS = LETRAS + NUMEROS + ESPECIAIS;

    private final SecureRandom random = new SecureRandom();

    // email -> código pendente
    private final Map<String, CodigoArmazenado> codigosPendentes = new ConcurrentHashMap<>();

    private record CodigoArmazenado(String codigo, Instant expiraEm) {
        boolean expirado() {
            return Instant.now().isAfter(expiraEm);
        }
    }

    public String gerarNovoCodigo(String email) {
        String codigo = gerarCodigoAleatorio();
        codigosPendentes.put(email, new CodigoArmazenado(codigo, Instant.now().plusSeconds(VALIDADE_MINUTOS * 60)));
        return codigo;
    }

    public ResultadoVerificacao verificar(String email, String codigoDigitado) {
        CodigoArmazenado armazenado = codigosPendentes.get(email);

        if (armazenado == null) {
            return ResultadoVerificacao.NAO_ENCONTRADO;
        }
        if (armazenado.expirado()) {
            codigosPendentes.remove(email);
            return ResultadoVerificacao.EXPIRADO;
        }
        if (!armazenado.codigo().equals(codigoDigitado)) {
            return ResultadoVerificacao.INCORRETO;
        }

        codigosPendentes.remove(email);
        return ResultadoVerificacao.VALIDO;
    }

    public enum ResultadoVerificacao {
        VALIDO, INCORRETO, EXPIRADO, NAO_ENCONTRADO
    }

    private String gerarCodigoAleatorio() {
        List<Character> caracteres = new java.util.ArrayList<>(TAMANHO_CODIGO);
        caracteres.add(sortear(LETRAS));
        caracteres.add(sortear(NUMEROS));
        caracteres.add(sortear(ESPECIAIS));
        while (caracteres.size() < TAMANHO_CODIGO) {
            caracteres.add(sortear(TODOS));
        }
        java.util.Collections.shuffle(caracteres, random);
        StringBuilder sb = new StringBuilder(TAMANHO_CODIGO);
        caracteres.forEach(sb::append);
        return sb.toString();
    }

    private char sortear(String conjunto) {
        return conjunto.charAt(random.nextInt(conjunto.length()));
    }
}
