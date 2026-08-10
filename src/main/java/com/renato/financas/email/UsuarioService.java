package com.renato.financas.email;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final SenhaService senhaService;

    public UsuarioService(UsuarioRepository usuarioRepository, SenhaService senhaService) {
        this.usuarioRepository = usuarioRepository;
        this.senhaService = senhaService;
    }

    /**
     * Cadastra um novo usuário. A senha em texto puro só existe em memória,
     * durante esta chamada — é convertida em hash Argon2id antes de qualquer
     * gravação no banco.
     */
    @Transactional
    public Usuario cadastrar(CadastroDTO dto) {
        String emailNormalizado = dto.getEmail().trim().toLowerCase();

        if (usuarioRepository.existsByEmail(emailNormalizado)) {
            throw new EmailJaCadastradoException(emailNormalizado);
        }

        String hashSenha = senhaService.gerarHash(dto.getSenha());
        Usuario usuario = new Usuario(dto.getNome().trim(), emailNormalizado, hashSenha);
        return usuarioRepository.save(usuario);
    }

    /**
     * Autentica um usuário comparando a senha informada com o hash salvo.
     * Nunca decodifica/reverte o hash — o Argon2id só permite comparação.
     */
    @Transactional(readOnly = true)
    public Usuario autenticar(LoginDTO dto) {
        String emailNormalizado = dto.getEmail().trim().toLowerCase();

        Usuario usuario = usuarioRepository.findByEmail(emailNormalizado)
                .orElseThrow(CredenciaisInvalidasException::new);

        if (!senhaService.verificar(usuario.getSenha(), dto.getSenha())) {
            throw new CredenciaisInvalidasException();
        }

        return usuario;
    }

    /**
     * Atualiza nome e/ou e-mail de uma conta já existente.
     * Se o novo e-mail já pertencer a OUTRA conta, rejeita (evita colisão).
     */
    @Transactional
    public Usuario atualizarPerfil(Long id, AtualizarPerfilDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(UsuarioNaoEncontradoException::new);

        String novoEmailNormalizado = dto.getEmail().trim().toLowerCase();

        if (!novoEmailNormalizado.equals(usuario.getEmail())
                && usuarioRepository.existsByEmail(novoEmailNormalizado)) {
            throw new EmailJaCadastradoException(novoEmailNormalizado);
        }

        usuario.setNome(dto.getNome().trim());
        usuario.setEmail(novoEmailNormalizado);
        return usuarioRepository.save(usuario);
    }

    /**
     * Exclui definitivamente a conta do usuário do banco de dados.
     * Retorna os dados do usuário (nome/e-mail) ANTES de apagar, para que o
     * Controller consiga enviar o e-mail de confirmação de exclusão depois.
     * Não há soft-delete: o registro é removido de fato da tabela "usuarios".
     */
    @Transactional
    public Usuario excluirConta(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(UsuarioNaoEncontradoException::new);

        usuarioRepository.delete(usuario);
        return usuario;
    }
    
    
    /**
     * Busca um usuário pelo e-mail sem lançar exceção — usado pelo fluxo de
     * "esqueci minha senha" pra decidir, silenciosamente, se envia o e-mail
     * (evita confirmar pra quem pergunta se um e-mail está cadastrado ou não).
     */
    @Transactional(readOnly = true)
    public Usuario buscarPorEmailOuNulo(String email) {
        return usuarioRepository.findByEmail(email.trim().toLowerCase()).orElse(null);
    }

    /**
     * Troca a senha de uma conta já existente. Só deve ser chamado depois
     * que o código de recuperação já foi validado pelo RecuperacaoSenhaService.
     */
    @Transactional
    public void redefinirSenha(String email, String novaSenhaTextoPuro) {
        Usuario usuario = usuarioRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(UsuarioNaoEncontradoException::new);

        String novoHash = senhaService.gerarHash(novaSenhaTextoPuro);
        usuario.setSenha(novoHash);
        usuarioRepository.save(usuario);
    }
    
    
}

































