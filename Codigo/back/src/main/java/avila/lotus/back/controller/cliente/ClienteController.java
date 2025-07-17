package avila.lotus.back.controller.cliente;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import avila.lotus.back.modelos.dto.cliente.SolicitacaoAnamneseCliente;
import avila.lotus.back.modelos.dto.cliente.SolicitacaoPerfilCliente;
import avila.lotus.back.modelos.dto.cliente.RespostaChavePixCliente;
import avila.lotus.back.modelos.dto.cliente.SolicitacaoCadastroCliente;
import avila.lotus.back.modelos.dto.cliente.RespostaCompletaCliente;
import avila.lotus.back.servico.cliente.ServicoCliente;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/cliente")
public class ClienteController {

  @Autowired
  private ServicoCliente ServicoCliente;

  @PostMapping("/cadastrar")
  public ResponseEntity<?> cadastrarCliente(@RequestBody @Valid SolicitacaoCadastroCliente SolicitacaoCadastroCliente) {
    String token = ServicoCliente.cadastrarCliente(SolicitacaoCadastroCliente);
    return ResponseEntity.ok(Map.of("token", token));
  }

  @PreAuthorize("hasRole('CLIENTE')")
  @PutMapping("/editar-anamnese")
  public ResponseEntity<Void> editarAnamnese(HttpServletRequest request,
      @RequestBody @Valid SolicitacaoAnamneseCliente anamneseRequest) {
    String token = request.getHeader("Authorization").substring(7);
    ServicoCliente.editarAnamnese(token, anamneseRequest);
    return ResponseEntity.noContent().build();
  }

  @PreAuthorize("hasRole('CLIENTE')")
  @PutMapping("/editar-perfil")
  public ResponseEntity<Void> editarPerfil(HttpServletRequest request,
      @RequestBody @Valid SolicitacaoPerfilCliente SolicitacaoCadastroCliente) {
    String token = request.getHeader("Authorization").substring(7);
    ServicoCliente.editarPerfil(token, SolicitacaoCadastroCliente);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/perfil-full")
  public ResponseEntity<RespostaCompletaCliente> getRespostaCompletaCliente(@RequestParam("email") String email) {
    return ResponseEntity.ok(ServicoCliente.obterClienteCompleto(email));
  }

  @GetMapping("/pix")
  public ResponseEntity<RespostaChavePixCliente> getClientePix(@RequestParam("email") String email) {
    return ResponseEntity.ok(ServicoCliente.obterPixCliente(email));
  }
}
