package avila.lotus.back.util.relatorio.financeiro;

import com.itextpdf.kernel.colors.DeviceRgb;

public class LancamentoFinanceiroVisual {

  private final LancamentoFinanceiro lancamento;

  public LancamentoFinanceiroVisual(LancamentoFinanceiro lancamento) {
    this.lancamento = lancamento;
  }

  public DeviceRgb getCorValor() {
    return lancamento.isCredito() ? azul() : vermelho();
  }

  public DeviceRgb getCorSaldo(double saldoAtual) {
    double novoSaldo = lancamento.aplicarAoSaldo(saldoAtual);
    return novoSaldo >= 0 ? azul() : vermelho();
  }

  public String getValorFormatado() {
    return String.format("%.2f", lancamento.getValor());
  }

  private DeviceRgb azul() {
    return new DeviceRgb(0, 102, 204);
  }

  private DeviceRgb vermelho() {
    return new DeviceRgb(204, 0, 0);
  }
}
