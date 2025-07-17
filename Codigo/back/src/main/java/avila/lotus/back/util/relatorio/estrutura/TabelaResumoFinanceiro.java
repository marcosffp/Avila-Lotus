package avila.lotus.back.util.relatorio.estrutura;

import java.util.List;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import avila.lotus.back.modelos.entidades.Agendamento;
import avila.lotus.back.util.relatorio.estatistica.EstatisticaComparecimento;
import avila.lotus.back.util.relatorio.financeiro.ResumoFinanceiro;

public class TabelaResumoFinanceiro {

  public static void adicionarResumoFinanceiro(Document document, List<Agendamento> agendamentos, double saldoInicial) {
    adicionarSubTitulo(document, "Resumo Financeiro");

    // 🔢 Calcula os dados
    double faturamento = ResumoFinanceiro.calcularFaturamento(agendamentos);
    double receitaFinalizados = ResumoFinanceiro.calcularReceitaFinalizados(agendamentos);
    double recebiveisPendentes = ResumoFinanceiro.calcularRecebiveisPendentes(agendamentos);
    double percentualComparecimento = EstatisticaComparecimento.calcularPercentualComparecimento(agendamentos);

// 🧾 Cria a tabela
Table tabela = criarTabelaResumoFinanceiro(
  receitaFinalizados,
  recebiveisPendentes,
  percentualComparecimento,
  faturamento
);
document.add(tabela);

// 🔸 Observação
Paragraph observacao = new Paragraph(
    "🔸 Observação: O saldo final inclui valores já pagos e também agendamentos pendentes de confirmação ou pagamento.")
    .setFontSize(9)
    .setItalic()
    .setTextAlignment(TextAlignment.LEFT);
document.add(observacao);

document.add(new Paragraph("\n"));
  }

  private static void adicionarSubTitulo(Document document, String tituloTexto) {
    Paragraph titulo = new Paragraph(tituloTexto)
        .setFontSize(14)
        .setBold()
        .setTextAlignment(TextAlignment.LEFT);
    document.add(titulo);
  }

  private static Table criarTabelaResumoFinanceiro(
      double receitaFinalizados,
      double recebiveisPendentes,
      double percentualComparecimento,
      double faturamento) {
    Table table = new Table(UnitValue.createPercentArray(new float[] { 3, 1 }))
        .setWidth(UnitValue.createPercentValue(100))
        .setMarginTop(10);

    // 🔸 Adiciona as linhas de resumo
    adicionarLinhaResumo(table, "Receita de Finalizados:", formatarValor(receitaFinalizados));
    adicionarLinhaResumo(table, "Recebíveis Pendentes:", formatarValor(recebiveisPendentes));
    adicionarLinhaResumo(table, "Percentual de Comparecimento:", String.format("%.2f%%", percentualComparecimento));
    adicionarLinhaResumo(table, "Faturamento:", formatarValor(faturamento));
    return table;
  }

  private static void adicionarLinhaResumo(Table table, String label, String valorFormatado) {
    table.addCell(Celula.header(label));
    table.addCell(Celula.normal(valorFormatado));
  }

  private static String formatarValor(double valor) {
    return String.format("R$ %.2f", valor);
  }
}
