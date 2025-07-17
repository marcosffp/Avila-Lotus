package avila.lotus.back.util.validacao.disponibilidade;

import org.springframework.stereotype.Component;

import avila.lotus.back.excecao.DisponibilidadeException;
import avila.lotus.back.modelos.entidades.Disponibilidade;

@Component
public class ValidacaoDisponibilidade {
  public void validarDisponibilidade(Disponibilidade disponibilidade) {
    if (disponibilidade == null) {
      throw new DisponibilidadeException("Disponibilidade não encontrada.");
    }
    if (!disponibilidade.isDisponivel()) {
      throw new DisponibilidadeException("Este horário já foi agendado.");
    }
  }
}
