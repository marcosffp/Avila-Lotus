package avila.lotus.back.util.relatorio.gerador;

import avila.lotus.back.modelos.entidades.Agendamento;
import avila.lotus.back.modelos.enumeradores.StatusAgendamento;
import avila.lotus.back.modelos.enumeradores.SubStatus;
import avila.lotus.back.util.relatorio.estatistica.Percentil;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class GeradorAnaliseDeHorarios {

  private static final DateTimeFormatter HORA_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

  private static String extrairHorario(Agendamento agendamento) {
    return agendamento.getHora().format(HORA_FORMATTER);
  }

  public static Map<String, Long> agruparPorHorarioAprovados(List<Agendamento> agendamentos) {
    return agendamentos.stream()
        .filter(a -> a.getStatus() == StatusAgendamento.APROVADO)
        .collect(Collectors.groupingBy(
            GeradorAnaliseDeHorarios::extrairHorario,
            Collectors.counting()));
  }

  public static Map<String, Long> agruparPorHorarioProblemas(List<Agendamento> agendamentos) {
    return agendamentos.stream()
        .filter(a -> a.getStatus() == StatusAgendamento.CANCELADO ||
            a.getStatus() == StatusAgendamento.CANCELADO_PELO_CLIENTE ||
            a.getStatus() == StatusAgendamento.RECUSADO ||
            a.getSubStatus() == SubStatus.AUSENTE)
        .collect(Collectors.groupingBy(
          GeradorAnaliseDeHorarios::extrairHorario,
            Collectors.counting()));
  }

  public static long calcularPercentil80(Map<String, Long> mapa) {
    List<Long> valores = mapa.values().stream()
        .sorted()
        .toList();
    int idx = (int) Math.ceil(valores.size() * 0.8) - 1;
    return valores.isEmpty() ? 0 : valores.get(Math.max(0, idx));
  }

  public static List<Map.Entry<String, Long>> melhoresHorarios(List<Agendamento> agendamentos) {
    Map<String, Long> agrupado = agruparPorHorarioAprovados(agendamentos);
    long p80 = (long) Percentil.calcularPercentil80(agrupado.values());

    return agrupado.entrySet().stream()
        .filter(e -> e.getValue() >= p80)
        .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
        .toList();
  }

  public static List<Map.Entry<String, Long>> pioresHorarios(List<Agendamento> agendamentos) {
    Map<String, Long> agrupado = agruparPorHorarioProblemas(agendamentos);
    long p80 = (long) Percentil.calcularPercentil80(agrupado.values());

    return agrupado.entrySet().stream()
        .filter(e -> e.getValue() >= p80)
        .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
        .toList();
  }

}
