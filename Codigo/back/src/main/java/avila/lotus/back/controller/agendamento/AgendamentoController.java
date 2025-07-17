package avila.lotus.back.controller.agendamento;

import avila.lotus.back.modelos.dto.agendamento.*;
import avila.lotus.back.servico.agendamento.ServicoAgendamento;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

import java.util.List;

@RestController
@RequestMapping("/agendamentos")
public class AgendamentoController {

  @Autowired
  private ServicoAgendamento ServicoAgendamento;

  @PreAuthorize("hasRole('CLIENTE')")
  @PostMapping(value = "/criar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> criarAgendamento(HttpServletRequest jwt,
      @RequestParam("disponibilidadeId") Long disponibilidadeId,
      @RequestParam("observacao") String observacao,
      @RequestParam("valorServico50Porcent") BigDecimal valorServico,
      @RequestParam("comprovante") MultipartFile comprovantePdf) throws Exception {
    String token = jwt.getHeader("Authorization").substring(7);
    ServicoAgendamento.criarAgendamento(token, disponibilidadeId, observacao, valorServico.doubleValue(),
        comprovantePdf);
    return ResponseEntity.noContent().build();
  }

  @PreAuthorize("hasRole('PROFISSIONAL')")
  @PutMapping("/aprovar")
  public ResponseEntity<?> aprovarAgendamento(@RequestBody @Valid SolicitacaoIdAgendamento request) {
    ServicoAgendamento.aprovarAgendamento(request);
    return ResponseEntity.noContent().build();
  }

  @PreAuthorize("hasRole('PROFISSIONAL')")
  @PutMapping(value = "/recusar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> recusarAgendamento(
      @RequestParam("idAgendamento") Long idAgendamento,
      @RequestParam("comprovante") MultipartFile comprovantePdf) throws Exception {
    ServicoAgendamento.recusarAgendamento(idAgendamento, comprovantePdf);
    return ResponseEntity.noContent().build();
  }

  @PreAuthorize("hasRole('CLIENTE')")
  @PutMapping("/cancelar-cliente")
  public ResponseEntity<?> cancelarAgendamentoPeloCliente(HttpServletRequest jwt,
      @RequestBody @Valid SolicitacaoIdAgendamento request) {
    String token = jwt.getHeader("Authorization").substring(7);
    ServicoAgendamento.cancelarAgendamentoPeloCliente(token, request);
    return ResponseEntity.noContent().build();
  }

  @PreAuthorize("hasRole('PROFISSIONAL')")
  @PutMapping(value = "/cancelar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> cancelarAgendamento(
      @RequestParam("idAgendamento") Long idAgendamento,
      @RequestParam("motivoCancelamento") String motivoCancelamento,
      @RequestParam("comprovante") MultipartFile comprovantePdf) throws Exception {
    ServicoAgendamento.cancelarAgendamento(idAgendamento, comprovantePdf, motivoCancelamento);
    return ResponseEntity.noContent().build();
  }

  @PreAuthorize("hasRole('PROFISSIONAL')")
  @GetMapping("/profissional")
  public ResponseEntity<List<RespostaAgendamentoProfissional>> buscarAgendamentosPorProfissional(
      HttpServletRequest jwt) {
    String token = jwt.getHeader("Authorization").substring(7);
    return ResponseEntity.ok(ServicoAgendamento.buscarAgendamentosPorProfissional(token));
  }

  @PreAuthorize("hasRole('CLIENTE')")
  @GetMapping("/cliente")
  public ResponseEntity<List<RespostaAgendamento>> buscarAgendamentosPorCliente(HttpServletRequest jwt) {
    String token = jwt.getHeader("Authorization").substring(7);
    return ResponseEntity.ok(ServicoAgendamento.buscarAgendamentosPorCliente(token));
  }

  @PreAuthorize("hasRole('PROFISSIONAL')")
  @PutMapping("/finalizar")
  public ResponseEntity<?> finalizarAgendamento(@RequestBody @Valid SolicitacaoIdAgendamento request) {
    ServicoAgendamento.finalizarAgendamento(request);
    return ResponseEntity.noContent().build();
  }

  @PreAuthorize("hasRole('PROFISSIONAL')")
  @PutMapping("/ausente")
  public ResponseEntity<?> marcarAgendamentoComoAusente(@RequestBody @Valid SolicitacaoIdAgendamento request) {
    ServicoAgendamento.ausenteAgendamento(request);
    return ResponseEntity.noContent().build();
  }

  @PreAuthorize("hasRole('PROFISSIONAL')")
  @PutMapping(value = "/reembolsar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> reembolsarAgendamento(
      @RequestParam("idAgendamento") Long idAgendamento,
      @RequestParam("comprovante") MultipartFile comprovantePdf) throws Exception {
    ServicoAgendamento.reembolsarAgendamento(idAgendamento, comprovantePdf);
    return ResponseEntity.noContent().build();
  }

}
