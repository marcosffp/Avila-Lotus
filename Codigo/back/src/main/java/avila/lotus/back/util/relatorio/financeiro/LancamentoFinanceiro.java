package avila.lotus.back.util.relatorio.financeiro;



import avila.lotus.back.excecao.RelatorioException;
import avila.lotus.back.modelos.entidades.Agendamento;
import avila.lotus.back.modelos.enumeradores.StatusAgendamento;
import avila.lotus.back.modelos.enumeradores.SubStatus;

public class LancamentoFinanceiro {

  private final Agendamento agendamento;
  private final double valor;
  private final boolean isCredito;

  public LancamentoFinanceiro(Agendamento agendamento) {
    this.agendamento = agendamento;
    this.valor = calcularValor();
    this.isCredito = determinarCredito();
  }

  public double getValor() {
    return valor;
  }

  public boolean isCredito() {
    return isCredito;
  }

  public double aplicarAoSaldo(double saldoAtual) {
    return isCredito ? saldoAtual + valor : saldoAtual - valor;
  }

  public String getTipoOperacao() {
    if (valor == 0)
      return "-";
    return isCredito ? "C" : "D";
  }

  public String getSubStatusTexto() {
    return agendamento.getSubStatus() != null ? agendamento.getSubStatus().name() : "-";
  }

  private double calcularValor() {
    // mesma lógica atual, mas pode ser externalizada para uma Strategy no futuro
    StatusAgendamento status = agendamento.getStatus();
    SubStatus subStatus = agendamento.getSubStatus();

    return switch (status) {
      case PENDENTE -> agendamento.getValorRecebido();

      case APROVADO -> {
        if (subStatus == null || subStatus == SubStatus.AUSENTE)
          yield agendamento.getValorRecebido();
        if (subStatus == SubStatus.FINALIZADO)
          yield agendamento.getValorRecebido() * 2;
        throw erroInvalido(status, subStatus);
      }

      case CANCELADO_PELO_CLIENTE -> {
        if (subStatus == SubStatus.REEMBOLSADO)
          yield 0.0;
        if (subStatus == SubStatus.REEMBOLSO_PENDENTE)
          yield agendamento.getValorRecebido();
        throw erroInvalido(status, subStatus);
      }

      case RECUSADO, CANCELADO -> 0.0;
    };
  }

  private boolean determinarCredito() {
    StatusAgendamento status = agendamento.getStatus();
    SubStatus subStatus = agendamento.getSubStatus();

    return switch (status) {
      case PENDENTE, APROVADO ->
        subStatus == null || subStatus == SubStatus.AUSENTE || subStatus == SubStatus.FINALIZADO;

      case CANCELADO_PELO_CLIENTE ->
        subStatus == SubStatus.REEMBOLSO_PENDENTE;

      case RECUSADO, CANCELADO ->
        false;
    };
  }

  private RelatorioException erroInvalido(StatusAgendamento status, SubStatus subStatus) {
    return new RelatorioException(
        "Status ou SubStatus inválido: " + status + " - " + (subStatus != null ? subStatus : "N/A"));
  }
}
