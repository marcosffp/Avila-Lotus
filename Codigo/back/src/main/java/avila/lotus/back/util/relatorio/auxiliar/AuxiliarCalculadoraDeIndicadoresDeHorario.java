package avila.lotus.back.util.relatorio.auxiliar;

import java.util.List;
import java.time.format.DateTimeFormatter;
import avila.lotus.back.modelos.entidades.Agendamento;

import avila.lotus.back.modelos.enumeradores.StatusAgendamento;
import avila.lotus.back.modelos.enumeradores.SubStatus;
import avila.lotus.back.util.relatorio.financeiro.LancamentoFinanceiro;

public class AuxiliarCalculadoraDeIndicadoresDeHorario {

  public static double calcularTaxaOcupacao(String horario, List<Agendamento> agendamentos) {
    long total = filtrarPorHorario(horario, agendamentos).size();
    long ocupados = filtrarPorHorarioEStatus(horario, agendamentos, StatusAgendamento.APROVADO).size();

    return total == 0 ? 0 : (ocupados * 100.0) / total;
  }

  public static double calcularTicketMedio(String horario, List<Agendamento> agendamentos) {
    var lista = filtrarPorHorario(horario, agendamentos).stream()
        .map(LancamentoFinanceiro::new)
        .filter(LancamentoFinanceiro::isCredito)
        .toList();

    return lista.isEmpty() ? 0.0 : lista.stream().mapToDouble(LancamentoFinanceiro::getValor).average().orElse(0.0);
  }

  public static double calcularTicketMedioGeral(List<Agendamento> agendamentos) {
    var lista = agendamentos.stream()
        .map(LancamentoFinanceiro::new)
        .filter(LancamentoFinanceiro::isCredito)
        .toList();

    return lista.isEmpty() ? 0.0 : lista.stream().mapToDouble(LancamentoFinanceiro::getValor).average().orElse(0.0);
  }

  public static double calcularPercentualClientesNovos(String horario, List<Agendamento> agendamentos) {
    var cancelados = filtrarCancelamentosPorHorario(horario, agendamentos);

    long totalCancelados = cancelados.size();

    long novos = cancelados.stream()
        .filter(cancelado -> agendamentos.stream()
            .filter(a -> !a.equals(cancelado))
            .noneMatch(a -> a.getCliente().equals(cancelado.getCliente()) &&
                a.getHora().isBefore(cancelado.getHora()) 
            ))
        .count();

    return totalCancelados == 0 ? 0 : (novos * 100.0) / totalCancelados;
  }


  public static double calcularTaxaReagendamento(String horario, List<Agendamento> agendamentos) {
    var cancelados = filtrarCancelamentosPorHorario(horario, agendamentos);

    long totalCancelados = cancelados.size();

    long reagendados = cancelados.stream()
        .filter(cancelado -> agendamentos.stream()
            .anyMatch(a -> a.getHora().isAfter(cancelado.getHora()) && 
                a.getStatus() == StatusAgendamento.APROVADO &&
                a.getCliente().equals(cancelado.getCliente())))
        .count();

    return totalCancelados == 0 ? 0 : (reagendados * 100.0) / totalCancelados;
  }

  // 🔎 Utilitários de filtro
  private static List<Agendamento> filtrarPorHorario(String horario, List<Agendamento> agendamentos) {
    return agendamentos.stream()
        .filter(a -> extrairHorario(a).equals(horario))
        .toList();
  }

  private static List<Agendamento> filtrarPorHorarioEStatus(String horario, List<Agendamento> agendamentos,
      StatusAgendamento status) {
    return agendamentos.stream()
        .filter(a -> extrairHorario(a).equals(horario) && a.getStatus() == status)
        .toList();
  }

  private static List<Agendamento> filtrarCancelamentosPorHorario(String horario, List<Agendamento> agendamentos) {
    return agendamentos.stream()
        .filter(a -> extrairHorario(a).equals(horario) &&
            (a.getStatus() == StatusAgendamento.CANCELADO ||
                a.getStatus() == StatusAgendamento.CANCELADO_PELO_CLIENTE ||
                a.getStatus() == StatusAgendamento.RECUSADO ||
                a.getSubStatus() == SubStatus.AUSENTE))
        .toList();
  }

  public static String extrairHorario(Agendamento agendamento) {
    return agendamento.getHora().format(DateTimeFormatter.ofPattern("HH:mm"));
  }
}
