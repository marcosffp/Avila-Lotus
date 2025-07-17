package avila.lotus.back.util.relatorio.gerador;

import java.util.List;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

public class GeradorDeBlocosPdf {

  public static void adicionarTitulo(Document document, String texto) {
    document.add(new Paragraph(texto)
        .setBold()
        .setFontSize(14)
        .setMarginBottom(10));
  }

  public static void adicionarSubtitulo(Document document, String texto, com.itextpdf.kernel.colors.Color cor) {
    document.add(new Paragraph(texto)
        .setBold()
        .setFontSize(12)
        .setFontColor(cor)
        .setMarginBottom(5));
  }

  public static void adicionarLinhaDivisoria(Document document) {
    document.add(new Paragraph("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        .setFontSize(9)
        .setFontColor(ColorConstants.GRAY));
  }

  public static void adicionarLinhaFinal(Document document) {
    document.add(new Paragraph("────────────────────────────────────────────────────────────")
        .setFontSize(8)
        .setFontColor(ColorConstants.LIGHT_GRAY)
        .setMarginBottom(8));
  }

  public static void adicionarTabelaDeDados(Document document, List<String> dados) {
    if (dados.isEmpty()) {
      document.add(new Paragraph("Nenhum item neste grupo.")
          .setFontSize(10)
          .setItalic()
          .setMarginBottom(8));
      return;
    }

    Table table = new Table(2).useAllAvailableWidth();

    for (String desc : dados) {
      Paragraph conteudo = new Paragraph(desc)
          .setFontSize(10)
          .setMarginBottom(5);
      table.addCell(conteudo);
    }

    if (dados.size() % 2 != 0) {
      table.addCell(new Paragraph(""));
    }

    document.add(table);
  }
}
