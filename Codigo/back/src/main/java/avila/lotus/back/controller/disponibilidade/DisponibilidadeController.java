package avila.lotus.back.controller.disponibilidade;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import avila.lotus.back.modelos.dto.disponibilidade.SolicitacaoDisponibilidade;
import avila.lotus.back.servico.disponibilidade.ServicoDisponibilidade;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/disponibilidade")
public class DisponibilidadeController {

  @Autowired
  private ServicoDisponibilidade ServicoDisponibilidade;

  @PreAuthorize("hasRole('PROFISSIONAL')")
  @PostMapping("/cadastrar")
  public ResponseEntity<Map<String, String>> cadastrarDisponibilidade(
      @RequestBody @Valid SolicitacaoDisponibilidade request) {

    ServicoDisponibilidade.cadastrarDisponibilidade(request);
    return ResponseEntity.noContent().build();
  }

  @PreAuthorize("hasAnyRole('PROFISSIONAL', 'CLIENTE')")
  @GetMapping("/dias")
  public ResponseEntity<List<String>> getDiasDisponibilidadePorProfissional() {
    return ResponseEntity.ok(ServicoDisponibilidade.getDiasDisponiveis());
  }

  @PreAuthorize("hasAnyRole('PROFISSIONAL', 'CLIENTE')")
  @GetMapping("/horarios")
  public ResponseEntity<List<Map<String, Object>>> getHorariosDisponibilidadePorProfissional(
      @RequestParam("diaMes") String diaMes) {
    return ResponseEntity.ok(ServicoDisponibilidade.getHorariosDisponiveis(diaMes));
  }

  @PreAuthorize("hasRole('PROFISSIONAL')")
  @DeleteMapping("/excluir")
  public ResponseEntity<Map<String, String>> excluirDisponibilidade(@RequestParam("id") Long id) {
    ServicoDisponibilidade.excluirDisponibilidade(id);
    return ResponseEntity.noContent().build();
  }
}
