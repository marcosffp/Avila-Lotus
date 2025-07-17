package avila.lotus.back.util.relatorio.estrutura;

import java.util.List;

import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;

import avila.lotus.back.modelos.entidades.Agendamento;

public class Rodape {
    public static void adicionarRodape(Document document, List<Agendamento> agendamentos, double saldo) {

        document.add(new Paragraph(
                "----------------------------------------------------------------------------------------------------------")
                .setTextAlignment(TextAlignment.CENTER).setFontSize(10));
        document.add(new Paragraph(
                "\nObservações:\nEsse relatório é gerado automaticamente pelo sistema de agendamento.\nConsulte a plataforma para detalhes adicionais ou relatórios específicos.")
                .setFontSize(8).setTextAlignment(TextAlignment.CENTER).setMarginTop(10));

        document.add(new Paragraph("\nAtendimento: lotusavilaagendamentos@gmail.com | SAC: 0800-123-456")
                .setFontSize(8).setTextAlignment(TextAlignment.CENTER));
    }
}
