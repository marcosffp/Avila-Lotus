package avila.lotus.back.controller.profissional;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import avila.lotus.back.modelos.dto.profissional.SolicitacaoNomeProfissional;
import avila.lotus.back.modelos.dto.profissional.RespostaPixENomeProfissional;
import avila.lotus.back.modelos.dto.profissional.SolicitacaoPixProfissional;
import avila.lotus.back.modelos.dto.profissional.RespostaPixProfissional;
import avila.lotus.back.modelos.dto.profissional.SolicitacaoProfissional;
import avila.lotus.back.modelos.dto.profissional.SolicitacaoServicosProfissional;
import avila.lotus.back.modelos.dto.profissional.RespostaServicosProfissional;
import avila.lotus.back.modelos.dto.profissional.SolicitacaoSobreMimProfissional;
import avila.lotus.back.servico.profissional.ServicoProfissional;
import avila.lotus.back.modelos.dto.profissional.RespostaSobreMimProfissional;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/prof")
public class ProfissionalController {

  @Autowired
  private ServicoProfissional ServicoProfissional;

  @PostMapping("/cadastrar")
  public ResponseEntity<?> cadastrarProfissional(@RequestBody @Valid SolicitacaoProfissional SolicitacaoProfissional) {
    String token = ServicoProfissional.cadastrarProfissional(SolicitacaoProfissional);
    return ResponseEntity.ok(Map.of("token", token));
  }

  @PreAuthorize("hasRole('PROFISSIONAL')")
  @PutMapping("/editar-servicos")
  public ResponseEntity<Void> editarServicos(HttpServletRequest request,
      @RequestBody @Valid SolicitacaoServicosProfissional SolicitacaoServicosProfissional) {
    String token = request.getHeader("Authorization").substring(7);
    ServicoProfissional.atualizarServicos(token, SolicitacaoServicosProfissional);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/servicos")
  public ResponseEntity<RespostaServicosProfissional> getServicos() {
    RespostaServicosProfissional response = ServicoProfissional.consultarServicos();
    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasRole('PROFISSIONAL')")
  @PutMapping("/editar-sobre-mim")
  public ResponseEntity<Void> editarSobreMim(HttpServletRequest request,
      @RequestBody @Valid SolicitacaoSobreMimProfissional sobreMimRequest) {
    String token = request.getHeader("Authorization").substring(7);
    ServicoProfissional.atualizarSobreMim(token, sobreMimRequest);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/sobre-mim")
  public ResponseEntity<RespostaSobreMimProfissional> getSobreMim() {
    return ResponseEntity.ok(ServicoProfissional.consultarSobreMim());
  }

  @GetMapping("/pix-e-nome")
  public ResponseEntity<RespostaPixENomeProfissional> getPixENomeProfissional() {
    return ResponseEntity.ok(ServicoProfissional.consultarPixENome());
  }

  @GetMapping("/pix")
  public ResponseEntity<RespostaPixProfissional> getPixProfissional() {
    return ResponseEntity.ok(ServicoProfissional.consultarChavePix());
  }

  @PreAuthorize("hasRole('PROFISSIONAL')")
  @PutMapping("/editar-nome")
  public ResponseEntity<Void> editarNomeProfissional(HttpServletRequest request,
      @RequestBody @Valid SolicitacaoNomeProfissional nomeRequest) {
    String token = request.getHeader("Authorization").substring(7);
    ServicoProfissional.atualizarNome(token, nomeRequest);
    return ResponseEntity.noContent().build();
  }

  @PreAuthorize("hasRole('PROFISSIONAL')")
  @PutMapping("/editar-pix")
  public ResponseEntity<Void> editarPixProfissional(HttpServletRequest request,
      @RequestBody @Valid SolicitacaoPixProfissional pixRequest) {
    String token = request.getHeader("Authorization").substring(7);
    ServicoProfissional.registrarChavePix(token, pixRequest);
    return ResponseEntity.noContent().build();
  }
}
