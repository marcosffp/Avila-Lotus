package avila.lotus.back.util.relatorio.estrutura;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;

import avila.lotus.back.modelos.entidades.Agendamento;

public class Cabecalho {
  public static void adicionarCabecalho(Document document) {
    LocalDateTime agora = LocalDateTime.now();
    DateTimeFormatter dataHoraFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    Paragraph header = new Paragraph("RELATÓRIO DE AGENDAMENTOS")
        .setFontSize(14).setBold().setTextAlignment(TextAlignment.RIGHT);
    Paragraph codData = new Paragraph("Relatório idº " + UUID.randomUUID().toString().substring(0, 12) +
        "\nGerado em: " + agora.format(dataHoraFormat))
        .setFontSize(8).setTextAlignment(TextAlignment.RIGHT);

    document.add(header);
    document.add(codData);
    document.add(new Paragraph("\n"));
  }

  public static void adicionarInfoProfissional(Document document, List<Agendamento> agendamentos, int mes, int ano) {
    Agendamento exemplo = agendamentos.isEmpty() ? null : agendamentos.get(0);
    String nomeProfissional = exemplo != null ? exemplo.getProfissional().getNome() : "Profissional";
    String emailProfissional = exemplo != null ? exemplo.getProfissional().getEmail() : "email@dominio.com";
    Paragraph titulo = new Paragraph("Profissional - Dados atuais")
        .setBold()
        .setFontSize(10);
    document.add(titulo);

    LineSeparator linha = new LineSeparator(new SolidLine());
    linha.setMarginBottom(5f);
    document.add(linha);

    document.add(new Paragraph("Nome: " + nomeProfissional).setFontSize(9));
    document.add(new Paragraph("Email: " + emailProfissional).setFontSize(9));
    document.add(new Paragraph("Período do relatório: " + String.format("%02d/%d", mes, ano)).setFontSize(9));
    document.add(new Paragraph("\n"));
  }
}
