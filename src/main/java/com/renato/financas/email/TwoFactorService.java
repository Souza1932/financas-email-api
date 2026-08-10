package com.renato.financas.email;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gera, armazena e valida os códigos de verificação (2FA) enviados por e-mail.
 *
 * Cada código:
 *  - tem 6 caracteres (letras, números e caracteres especiais);
 *  - é aleatório a cada geração;
 *  - expira em 5 minutos;
 *  - é de uso único (removido do armazenamento após verificação bem-sucedida).
 *
 * Armazenamento em memória: suficiente pro escopo desta aplicação (uma única
 * instância do servidor). Se a API rodar com múltiplas instâncias, isso deve
 * migrar para um cache compartilhado (ex.: Redis).
 */
@Service
public class TwoFactorService {

    private static final int TAMANHO_CODIGO = 6;
    private static final long VALIDADE_MINUTOS = 5;

    // Conjuntos de caracteres sem símbolos ambíguos (O/0, I/l/1) e sem
    // caracteres que exigiriam escape em HTML (<, >, &, ", ').
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

    /**
     * Gera um novo código de 6 caracteres (garantindo ao menos uma letra,
     * um número e um caractere especial), associa ao e-mail informado e
     * retorna o código gerado para que ele possa ser enviado por e-mail.
     */
    public String gerarNovoCodigo(String email) {
        String codigo = gerarCodigoAleatorio();
        codigosPendentes.put(email, new CodigoArmazenado(codigo, Instant.now().plusSeconds(VALIDADE_MINUTOS * 60)));
        return codigo;
    }

    /**
     * Verifica se o código informado é válido (existe, não expirou e confere)
     * para o e-mail informado. Em caso de sucesso, o código é consumido
     * (removido) para não permitir reuso.
     */
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

        // Garante pelo menos um caractere de cada categoria exigida.
        caracteres.add(sortear(LETRAS));
        caracteres.add(sortear(NUMEROS));
        caracteres.add(sortear(ESPECIAIS));

        // Completa o restante com caracteres de qualquer categoria.
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
