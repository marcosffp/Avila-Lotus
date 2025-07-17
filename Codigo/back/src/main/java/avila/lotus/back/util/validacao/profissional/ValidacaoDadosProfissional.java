package avila.lotus.back.util.validacao.profissional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import avila.lotus.back.excecao.ProfissionalException;
import avila.lotus.back.repositorio.RepositorioProfisisonal;

@Component
public class ValidacaoDadosProfissional {

  @Value("${contato.username}")
  private String emailPermitido;

  @Autowired
  private RepositorioProfisisonal repositorioProfisisonal;

  @Value("${cod.autenticacao}")
  private String codigoAutenticacaoValido;

  public void validarAntesCriar(String email, String codigo) {
    if (!email.equalsIgnoreCase(emailPermitido)) {
      throw new ProfissionalException("Cadastro permitido apenas para o e-mail autorizado");
    }

    if (repositorioProfisisonal.buscarPorEmail(email).isPresent()) {
      throw new ProfissionalException("E-mail já cadastrado");
    }

    if (!codigo.equals(codigoAutenticacaoValido)) {
      throw new ProfissionalException("Código de autenticação inválido");
    }
  }
}
