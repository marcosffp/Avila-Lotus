package avila.lotus.back.controller.relatorio;

import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ContentDisposition;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import avila.lotus.back.servico.relatorio.ServicoRelatorio;

@RestController
@RequestMapping("/relatorio")
public class RelatorioController {

  private final ServicoRelatorio relatorioService;

  public RelatorioController(ServicoRelatorio relatorioService) {
    this.relatorioService = relatorioService;
  }

  @PreAuthorize("hasRole('PROFISSIONAL')")
  @GetMapping("/mensal")
  public ResponseEntity<byte[]> downloadRelatorioMensal(
      @RequestParam int mes,
      @RequestParam int ano) {

    try {
      byte[] pdfBytes = relatorioService.gerarRelatorioMensal(mes, ano);

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_PDF);
      headers.setContentDisposition(
          ContentDisposition.attachment()
              .filename("relatorio_" + mes + "_" + ano + ".pdf")
              .build());

      return ResponseEntity.ok()
          .headers(headers)
          .body(pdfBytes);

    } catch (IOException e) {
      e.printStackTrace();
      return ResponseEntity.internalServerError().build();
    }
  }

  @PreAuthorize("hasRole('PROFISSIONAL')")
  @GetMapping("/periodo")
  public ResponseEntity<byte[]> downloadRelatorioPeriodo(@RequestParam String dataInicio,
      @RequestParam String dataFim) {

    try {
      byte[] pdfBytes = relatorioService.gerarRelatorioPeriodo(dataInicio, dataFim);

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_PDF);
      headers.setContentDisposition(
          ContentDisposition.attachment()
              .filename("relatorio_" + dataInicio + "_a_" + dataFim + ".pdf")
              .build());

      return ResponseEntity.ok()
          .headers(headers)
          .body(pdfBytes);

    } catch (IOException e) {
      e.printStackTrace();
      return ResponseEntity.internalServerError().build();
    }
  }
}
