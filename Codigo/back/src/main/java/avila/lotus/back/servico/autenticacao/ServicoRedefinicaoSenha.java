package avila.lotus.back.servico.autenticacao;

import avila.lotus.back.excecao.auth.TokenInvalidoException;
import avila.lotus.back.modelos.entidades.Cliente;
import avila.lotus.back.modelos.entidades.Profissional;
import avila.lotus.back.repositorio.RepositorioCliente;
import avila.lotus.back.repositorio.RepositorioProfisisonal;
import avila.lotus.back.servico.email.ServicoEmailRedefinicaoSenha;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Optional;

@Service
public class ServicoRedefinicaoSenha {

  @Autowired
  private RepositorioCliente repositorioCliente;

  @Autowired
  private RepositorioProfisisonal repositorioProfissional;

  @Autowired
  private ServicoEmailRedefinicaoSenha emailNoReply;

  @Autowired
  private PasswordEncoder codificadorSenha;

  @Autowired
  private CacheManager gerenciadorCache;

  private static final SecureRandom random = new SecureRandom();

  public String gerarCodigoRedefinicaoSenha(String email) {

    Optional<Cliente> clienteOpt = repositorioCliente.buscarPorEmail(email);
    Optional<Profissional> profissionalOpt = repositorioProfissional.buscarPorEmail(email);

    if (clienteOpt.isPresent() || profissionalOpt.isPresent()) {
      String code = String.format("%05d", random.nextInt(100000));
      var cache = gerenciadorCache.getCache("passwordResetCodes");
      if (cache != null) {
        cache.put(email, code);
      }

      emailNoReply.enviarEmailRedefinicao(email, "Redefinição de Senha - Ávila Lótus", code);

      return code;
    }
    throw new TokenInvalidoException("Email não encontrado.");
  }

  public void redefinirSenha(String email, String code, String newPassword) {
    var cache = gerenciadorCache.getCache("passwordResetCodes");
    if (cache == null) {
      throw new TokenInvalidoException("Código inválido ou expirado.");
    }

    String storedCode = cache.get(email, String.class);
    if (storedCode == null || !storedCode.equals(code)) {
      throw new TokenInvalidoException("Código inválido ou expirado.");
    }

    Optional<Cliente> clienteOpt = repositorioCliente.buscarPorEmail(email);
    Optional<Profissional> profissionalOpt = repositorioProfissional.buscarPorEmail(email);

    if (clienteOpt.isPresent()) {
      Cliente cliente = clienteOpt.get();
      cliente.setPassword(codificadorSenha.encode(newPassword));
      repositorioCliente.save(cliente);
    } else if (profissionalOpt.isPresent()) {
      Profissional profissional = profissionalOpt.get();
      profissional.setPassword(codificadorSenha.encode(newPassword));
      repositorioProfissional.save(profissional);
    } else {
      throw new TokenInvalidoException("Usuário não encontrado.");
    }

    cache.evict(email);
  }
}
