package avila.lotus.back.util.relatorio.auxiliar;

import java.util.stream.Collectors;

import avila.lotus.back.modelos.entidades.Agendamento;
import java.util.List;
import java.util.Map;

public class AuxiliarAgrupadorDeAgendamentos {
  public static Map<String, List<Agendamento>> agruparPorCliente(List<Agendamento> agendamentos) {
    return agendamentos.stream()
        .collect(Collectors.groupingBy(a -> a.getCliente().getNome()));
  }
}
