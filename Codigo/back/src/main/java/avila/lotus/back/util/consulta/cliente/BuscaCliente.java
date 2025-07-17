package avila.lotus.back.util.consulta.cliente;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import avila.lotus.back.excecao.ClienteException;
import avila.lotus.back.modelos.entidades.Cliente;
import avila.lotus.back.repositorio.RepositorioCliente;
import avila.lotus.back.servico.autenticacao.ServicoToken;


@Component
public class BuscaCliente {

  @Autowired
  private RepositorioCliente repositorioCliente;

  @Autowired
  private ServicoToken servicoToken;

  public Cliente buscarClientePorToken(String token) {
    String email = servicoToken.validarToken(token);
    return repositorioCliente.buscarPorEmail(email)
        .orElseThrow(() -> new ClienteException("Cliente não encontrado"));
  }

}
