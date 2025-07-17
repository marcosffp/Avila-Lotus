package avila.lotus.back.controller.autenticacao;

import avila.lotus.back.modelos.dto.autenticacao.SolicitacaoDeSenhaEsquecida;
import avila.lotus.back.modelos.dto.autenticacao.SolicitacaoDeRedefinicaoDeSenha;
import avila.lotus.back.servico.autenticacao.ServicoRedefinicaoSenha;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class PasswordController {

  @Autowired
  private ServicoRedefinicaoSenha passwordResetService;

  @PostMapping("/forgot-password")
  public ResponseEntity<Map<String, Object>> forgotPassword(@RequestBody SolicitacaoDeSenhaEsquecida request) {
    String code = passwordResetService.gerarCodigoRedefinicaoSenha(request.email());

    Map<String, Object> response = new HashMap<>();
    response.put("message", "Código enviado para o e-mail");
    response.put("code", code);

    return ResponseEntity.ok(response);
  }

  @PostMapping("/reset-password")
  public ResponseEntity<Map<String, String>> resetPassword(@RequestBody SolicitacaoDeRedefinicaoDeSenha request) {
    passwordResetService.redefinirSenha(request.email(), request.code(), request.newPassword());

    Map<String, String> response = new HashMap<>();
    response.put("message", "Senha alterada com sucesso");

    return ResponseEntity.ok(response);
  }
}
