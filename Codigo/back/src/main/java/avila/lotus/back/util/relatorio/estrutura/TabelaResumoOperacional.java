package avila.lotus.back.util.relatorio.estrutura;

import java.util.List;

import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import avila.lotus.back.modelos.entidades.Agendamento;

public class TabelaResumoOperacional {
  public static void adicionarResumo(Document document, List<Agendamento> agendamentos) {
    adicionarSubTitulo(document, "Resumo Operacional");
    ResumoDados resumo = calcularResumo(agendamentos);

    Table tabelaResumo = criarTabelaResumo(resumo, agendamentos.size());
    document.add(tabelaResumo);
    document.add(new Paragraph("\n"));
  }

  private static void adicionarSubTitulo(Document document, String tituloTexto) {
    Paragraph titulo = new Paragraph(tituloTexto)
        .setFontSize(14)
        .setBold()
        .setTextAlignment(TextAlignment.LEFT);
    document.add(titulo);
  }

  private static ResumoDados calcularResumo(List<Agendamento> agendamentos) {
    ResumoDados resumo = new ResumoDados();

    for (Agendamento ag : agendamentos) {
      switch (ag.getStatus()) {
        case RECUSADO -> {
          resumo.recusados++;
        }
        case PENDENTE -> {
          resumo.pendentes++;
        }
        case CANCELADO, CANCELADO_PELO_CLIENTE -> resumo.cancelados++;
        default -> {
        }
      }

      if (ag.getSubStatus() != null) {
        switch (ag.getSubStatus()) {
          case FINALIZADO -> {
            resumo.finalizados++;
          }
          case AUSENTE -> {
            resumo.ausentes++;
          }
          default -> {
          }
        }
      }
    }

    return resumo;
  }

  private static Table criarTabelaResumo(ResumoDados resumo, int totalAgendamentos) {
    Table table = new Table(UnitValue.createPercentArray(new float[] { 3, 1 }))
        .setWidth(UnitValue.createPercentValue(100))
        .setMarginTop(10);

    adicionarLinhaResumo(table, "Finalizados:", resumo.finalizados);
    adicionarLinhaResumo(table, "Ausentes:", resumo.ausentes);
    adicionarLinhaResumo(table, "Pendentes:", resumo.pendentes);
    adicionarLinhaResumo(table, "Cancelados:", resumo.cancelados);
    adicionarLinhaResumo(table, "Recusados:", resumo.recusados);
    adicionarLinhaResumo(table, "Total de agendamentos:", totalAgendamentos);

    return table;
  }

  private static void adicionarLinhaResumo(Table table, String label, long valor) {
    table.addCell(Celula.header(label));
    table.addCell(Celula.normal(String.valueOf(valor)));
  }

  private static class ResumoDados {
    long finalizados = 0;
    long ausentes = 0;
    long pendentes = 0;
    long cancelados = 0;
    long recusados = 0;
  }
}
