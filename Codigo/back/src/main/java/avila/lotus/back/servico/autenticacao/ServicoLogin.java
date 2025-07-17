package avila.lotus.back.servico.autenticacao;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import avila.lotus.back.excecao.auth.LoginException;
import avila.lotus.back.modelos.dto.autenticacao.SolicitacaoAutenticacao;
import avila.lotus.back.modelos.entidades.Cliente;
import avila.lotus.back.modelos.entidades.Profissional;
import avila.lotus.back.repositorio.RepositorioCliente;
import avila.lotus.back.repositorio.RepositorioProfisisonal;

@Service
public class ServicoLogin {

  @Autowired
  private RepositorioCliente repositorioCliente;

  @Autowired
  private RepositorioProfisisonal repositorioProfissional;

  @Autowired
  private PasswordEncoder codificadorSenha;

  @Autowired
  private ServicoToken servicoToken;

  public String autenticar(SolicitacaoAutenticacao SolicitacaoAutenticacao) {
    String email = SolicitacaoAutenticacao.email();
    String senha = SolicitacaoAutenticacao.password();

    Optional<Cliente> clienteOpt = repositorioCliente.buscarPorEmail(email);
    if (clienteOpt.isPresent()) {
      Cliente cliente = clienteOpt.get();
      if (!codificadorSenha.matches(senha, cliente.getPassword())) {
        throw new LoginException("Senha incorreta para cliente.");
      }
      return servicoToken.gerarToken(cliente.getId(), cliente.getEmail(), cliente.getTipo().toString());
    }

    Optional<Profissional> profissionalOpt = repositorioProfissional.buscarPorEmail(email);
    if (profissionalOpt.isPresent()) {
      Profissional profissional = profissionalOpt.get();
      if (!codificadorSenha.matches(senha, profissional.getPassword())) {
        throw new LoginException("Senha incorreta para profissional.");
      }
      return servicoToken.gerarToken(profissional.getId(), profissional.getEmail(),
          profissional.getTipo().toString());
    }

    throw new LoginException("Usuário não encontrado.");
  }
}
