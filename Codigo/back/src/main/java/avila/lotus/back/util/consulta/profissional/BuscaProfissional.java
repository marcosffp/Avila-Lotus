package avila.lotus.back.util.consulta.profissional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import avila.lotus.back.excecao.ProfissionalException;
import avila.lotus.back.modelos.entidades.Profissional;
import avila.lotus.back.repositorio.RepositorioProfisisonal;
import avila.lotus.back.servico.autenticacao.ServicoToken;
import org.springframework.transaction.annotation.Transactional;


@Component
public class BuscaProfissional {
  @Value("${contato.username}")
  private String emailPermitido;

  @Autowired
  private RepositorioProfisisonal repositorioProfisisonal;

  @Autowired
  private ServicoToken servicoToken;

  public Profissional buscarProfissionalPorToken(String token) {
    String email = servicoToken.validarToken(token);
    return repositorioProfisisonal.buscarPorEmail(email)
        .orElseThrow(() -> new ProfissionalException("Profissional não encontrado"));
  }

  @Transactional(readOnly = true)
  public Profissional getProfissional() {
    return repositorioProfisisonal.buscarPorEmail(emailPermitido)
        .orElseThrow(() -> new ProfissionalException("Usuário não encontrado"));
  }
}
