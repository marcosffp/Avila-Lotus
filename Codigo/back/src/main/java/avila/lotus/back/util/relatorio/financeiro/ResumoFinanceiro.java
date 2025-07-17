package avila.lotus.back.util.relatorio.financeiro;

import java.util.*;
import java.util.stream.Collectors;

import avila.lotus.back.modelos.entidades.Agendamento;
import avila.lotus.back.modelos.enumeradores.SubStatus;

public class ResumoFinanceiro {

  public static double calcularRecebiveisPendentes(List<Agendamento> agendamentos) {
    return somarLancamentosFiltrados(agendamentos,
        l -> l.isCredito() && l.getTipoOperacao().equals("C"));
  }

  public static double calcularReceitaFinalizados(List<Agendamento> agendamentos) {
    return somarLancamentosFiltrados(agendamentos,
        l -> l.isCredito() && l.getSubStatusTexto().equals(SubStatus.FINALIZADO.name()));
  }

  public static double calcularFaturamento(List<Agendamento> agendamentos) {
    return somarLancamentosCredito(agendamentos);
  }

  private static double somarLancamentosCredito(List<Agendamento> agendamentos) {
    return somarLancamentosFiltrados(agendamentos, LancamentoFinanceiro::isCredito);
  }

  private static double somarLancamentosFiltrados(
      List<Agendamento> agendamentos,
      java.util.function.Predicate<LancamentoFinanceiro> filtro) {

    return agendamentos.stream()
        .map(LancamentoFinanceiro::new)
        .filter(filtro)
        .mapToDouble(LancamentoFinanceiro::getValor)
        .sum();
  }

  public static Map<String, Double> faturamentoPorCliente(Map<String, List<Agendamento>> porCliente) {
  return porCliente.entrySet().stream()
      .collect(Collectors.toMap(
          Map.Entry::getKey,
          e -> calcularFaturamento(e.getValue())
      ));
}

}
