package avila.lotus.back.util.relatorio.estatistica;

import java.util.List;

import avila.lotus.back.modelos.entidades.Agendamento;
import avila.lotus.back.modelos.enumeradores.SubStatus;

public class EstatisticaComparecimento {

  public static double calcularPercentualComparecimento(List<Agendamento> agendamentos) {
    long totalRelevantes = agendamentos.stream()
        .filter(ag -> ag.getSubStatus() == SubStatus.FINALIZADO || ag.getSubStatus() == SubStatus.AUSENTE)
        .count();

    if (totalRelevantes == 0) {
      return 0.0;
    }

    long finalizados = agendamentos.stream()
        .filter(ag -> ag.getSubStatus() == SubStatus.FINALIZADO)
        .count();

    return (finalizados * 100.0) / totalRelevantes;
  }
}
