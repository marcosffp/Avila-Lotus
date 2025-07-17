package avila.lotus.back.util.relatorio.auxiliar;

import avila.lotus.back.modelos.entidades.Agendamento;
import avila.lotus.back.modelos.enumeradores.StatusAgendamento;
import avila.lotus.back.modelos.enumeradores.SubStatus;

import java.util.List;
import java.util.function.Predicate;

public class AuxiliarCalculadoraDeIndicadores {


  private static final Predicate<Agendamento> EH_REALIZADO = a -> a.getStatus() == StatusAgendamento.APROVADO &&
      a.getSubStatus() == SubStatus.FINALIZADO;

  private static final Predicate<Agendamento> EH_NAO_REALIZADO = a -> a.getStatus() == StatusAgendamento.RECUSADO ||
      a.getStatus() == StatusAgendamento.CANCELADO ||
      a.getStatus() == StatusAgendamento.CANCELADO_PELO_CLIENTE ||
      a.getSubStatus() == SubStatus.AUSENTE;

  private static final Predicate<Agendamento> EH_EM_ABERTO = a -> a.getStatus() == StatusAgendamento.PENDENTE ||
      (a.getStatus() == StatusAgendamento.APROVADO && a.getSubStatus() == null);


  private static long contarPorCondicao(List<Agendamento> lista, Predicate<Agendamento> condicao) {
    return lista.stream().filter(condicao).count();
  }

  private static double calcularPorcentagem(List<Agendamento> lista, Predicate<Agendamento> condicao) {
    long total = lista.size();
    long quantidade = contarPorCondicao(lista, condicao);
    return total == 0 ? 0 : (quantidade * 100.0) / total;
  }


  public static long contarRealizados(List<Agendamento> lista) {
    return contarPorCondicao(lista, EH_REALIZADO);
  }

  public static long contarNaoRealizados(List<Agendamento> lista) {
    return contarPorCondicao(lista, EH_NAO_REALIZADO);
  }

  public static long contarEmAberto(List<Agendamento> lista) {
    return contarPorCondicao(lista, EH_EM_ABERTO);
  }

  public static double calcularPorcentagemRealizados(List<Agendamento> lista) {
    return calcularPorcentagem(lista, EH_REALIZADO);
  }

  public static double calcularPorcentagemNaoRealizados(List<Agendamento> lista) {
    return calcularPorcentagem(lista, EH_NAO_REALIZADO);
  }

  public static double calcularPorcentagemEmAberto(List<Agendamento> lista) {
    return calcularPorcentagem(lista, EH_EM_ABERTO);
  }
}
