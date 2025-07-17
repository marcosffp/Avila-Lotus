package avila.lotus.back.util.relatorio;

import java.util.List;
import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;

import avila.lotus.back.modelos.entidades.Agendamento;
import avila.lotus.back.util.relatorio.auxiliar.AuxiliarLogoPdf;
import avila.lotus.back.util.relatorio.estrutura.Cabecalho;
import avila.lotus.back.util.relatorio.estrutura.TabelaResumoOperacional;
import avila.lotus.back.util.relatorio.estrutura.Rodape;
import avila.lotus.back.util.relatorio.estrutura.TabelaInformacoesInteligentes;
import avila.lotus.back.util.relatorio.estrutura.TabelaLancamento;
import avila.lotus.back.util.relatorio.estrutura.TabelaResumoFinanceiro;

public class GeradorPdfRelatorio {

  public static byte[] gerar(List<Agendamento> agendamentos, int mes, int ano) throws IOException {
    double saldo = 0.0;
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PdfWriter writer = new PdfWriter(outputStream);
    PdfDocument pdf = new PdfDocument(writer);
    Document document = new Document(pdf);

    AuxiliarLogoPdf.adicionarLogo(document);
    Cabecalho.adicionarCabecalho(document);
    Cabecalho.adicionarInfoProfissional(document, agendamentos, mes, ano);
    saldo = TabelaLancamento.adicionarTabelaLancamentos(document, agendamentos, saldo);
    TabelaResumoOperacional.adicionarResumo(document, agendamentos);
    TabelaResumoFinanceiro.adicionarResumoFinanceiro(document, agendamentos, 0.0);
    TabelaInformacoesInteligentes.adicionarInformacoes(document, agendamentos);
    Rodape.adicionarRodape(document, agendamentos, saldo);

    document.close();
    return outputStream.toByteArray();
  }

}
