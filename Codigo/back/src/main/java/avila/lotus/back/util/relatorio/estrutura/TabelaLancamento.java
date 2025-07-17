package avila.lotus.back.util.relatorio.estrutura;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import avila.lotus.back.modelos.entidades.Agendamento;
import avila.lotus.back.util.relatorio.financeiro.LancamentoFinanceiro;
import avila.lotus.back.util.relatorio.financeiro.LancamentoFinanceiroVisual;

public class TabelaLancamento {

  public static double adicionarTabelaLancamentos(Document document, List<Agendamento> lancamentos, double saldo) {
    adicionarSubTitulo(document, "Lançamentos");

    Table table = criarTabelaLancamentos();

    if (!lancamentos.isEmpty()) {
      adicionarLinhaSaldoAnterior(table, lancamentos.get(0).getData());
    }

    lancamentos.sort(Comparator.comparing(Agendamento::getData)
        .thenComparing(Agendamento::getHora));

    int contadorLinha = 0;
    for (Agendamento lanc : lancamentos) {
      saldo = adicionarLinhaLancamento(table, lanc, saldo, contadorLinha++);
    }

    document.add(table);
    document.add(new Paragraph("\n"));
    return saldo;
  }

  private static void adicionarSubTitulo(Document document, String tituloTexto) {
    Paragraph titulo = new Paragraph(tituloTexto)
        .setFontSize(14)
        .setBold()
        .setTextAlignment(TextAlignment.LEFT);
    document.add(titulo);
  }

  private static Table criarTabelaLancamentos() {
    Table table = new Table(new float[] { 2, 2, 3, 2, 2, 2, 1, 2 });
    table.setWidth(UnitValue.createPercentValue(100));

    String[] headers = { "Data", "Hora", "Cliente", "Status", "Sub Status", "Valor (R$)", "Inf", "Saldo" };
    for (String header : headers) {
      table.addHeaderCell(Celula.header(header));
    }

    return table;
  }

  private static void adicionarLinhaSaldoAnterior(Table table, LocalDate dataPrimeiroLanc) {
    LocalDate dataSaldoAnterior = dataPrimeiroLanc.withDayOfMonth(1).minusDays(1);
    DeviceRgb azul = new DeviceRgb(0, 102, 204);

    table.addCell(Celula.normal(dataSaldoAnterior.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
    table.addCell(Celula.normal("-"));
    table.addCell(Celula.normal("0000"));
    table.addCell(Celula.normal("00000"));
    table.addCell(Celula.normal("Saldo Anterior"));
    table.addCell(Celula.colored("0,00", azul));
    table.addCell(Celula.colored("C", azul));
    table.addCell(Celula.normal("0,00"));
  }

  private static double adicionarLinhaLancamento(Table table, Agendamento lanc, double saldoAtual, int index) {
    LancamentoFinanceiro lancamento = new LancamentoFinanceiro(lanc);
    LancamentoFinanceiroVisual visual = new LancamentoFinanceiroVisual(lancamento);

    boolean isPar = index % 2 == 0;
    DeviceRgb fundo = isPar ? new DeviceRgb(220, 220, 220) : null;

    DeviceRgb corValor = visual.getCorValor();
    double novoSaldo = lancamento.aplicarAoSaldo(saldoAtual);
    DeviceRgb corSaldo = visual.getCorSaldo(saldoAtual);

    table.addCell(Celula.normal(lanc.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), fundo));
    table.addCell(Celula.normal(lanc.getHora().format(DateTimeFormatter.ofPattern("HH'h'mm")), fundo));
    table.addCell(Celula.normal(lanc.getCliente().getNome(), fundo));
    table.addCell(Celula.normal(lanc.getStatus().toString(), fundo));
    table.addCell(Celula.normal(
        lanc.getSubStatus() != null ? lanc.getSubStatus().toString() : "-",
        fundo));
    table.addCell(Celula.colored(visual.getValorFormatado(), corValor, fundo));
    table.addCell(Celula.colored(lancamento.getTipoOperacao(), corValor, fundo));
    table.addCell(Celula.colored(String.format("%.2f", novoSaldo), corSaldo, fundo));

    return novoSaldo;
  }

}
