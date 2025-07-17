package avila.lotus.back.util.consulta.agendamento;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import avila.lotus.back.excecao.AgendamentoException;
import avila.lotus.back.modelos.entidades.Agendamento;
import avila.lotus.back.repositorio.RepositorioAgendamento;

@Component
public class BuscaAgendamento {

  @Autowired
  private RepositorioAgendamento repositorioAgendamento;

  public Agendamento buscarAgendamentoPorId(Long id) {
    return repositorioAgendamento.findById(id)
        .orElseThrow(() -> new AgendamentoException("Agendamento não encontrado"));
  }

}
