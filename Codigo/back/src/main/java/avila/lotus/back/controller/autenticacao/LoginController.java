package avila.lotus.back.controller.autenticacao;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import avila.lotus.back.modelos.dto.autenticacao.SolicitacaoAutenticacao;
import avila.lotus.back.servico.autenticacao.ServicoLogin;

@RestController
@RequestMapping("/auth")
public class LoginController {

  @Autowired
  private ServicoLogin loginService;

  @PostMapping("/login")
  public ResponseEntity<Map<String, String>> login(@RequestBody SolicitacaoAutenticacao SolicitacaoAutenticacao) {
    String token = loginService.autenticar(SolicitacaoAutenticacao);
    return ResponseEntity.ok(Map.of("token", token));
  }
}
