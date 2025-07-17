package avila.lotus.back.excecao;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import avila.lotus.back.excecao.auth.*;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new LinkedHashMap<>();

    ex.getBindingResult().getFieldErrors().stream()
        .filter(error -> "NotBlank".equals(error.getCode()))
        .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

    ex.getBindingResult().getFieldErrors().stream()
        .filter(error -> !"NotBlank".equals(error.getCode()))
        .filter(error -> !errors.containsKey(error.getField())) // Evita sobrescrever erros de @NotBlank
        .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

    log.warn("Erros de validação detectados: {}", errors);
    return ResponseEntity.badRequest().body(errors);
  }

  @ExceptionHandler({
      ClienteException.class,
      ProfissionalException.class,
      TokenException.class,
      LoginException.class,
      TokenInvalidoException.class,
      EmailException.class,
      AgendamentoException.class,
      RelatorioException.class
  })
  public ResponseEntity<Map<String, String>> handleCustomExceptions(RuntimeException ex) {
    HttpStatus status = HttpStatus.BAD_REQUEST;

    if (ex instanceof TokenException || ex instanceof LoginException) {
      status = HttpStatus.UNAUTHORIZED;
    } else if (ex instanceof EmailException) {
      status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    log.warn("Exceção capturada: {} - {}", ex.getClass().getSimpleName(), ex.getMessage());
    return ResponseEntity.status(status).body(Map.of("erro", ex.getMessage()));
  }

  @ExceptionHandler({
      ComprovanteException.class
  })
  public ResponseEntity<Map<String, String>> handleBusinessExceptions(RuntimeException ex) {
    log.warn("Erro de validação de negócio: {}", ex.getMessage());
    return ResponseEntity.badRequest().body(Map.of("erro", ex.getMessage()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
    log.error("Erro interno no servidor: ", ex);

    Throwable causa = ex.getCause();
    if (causa instanceof DisponibilidadeException dex) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(Map.of("erro", dex.getMessage()));
    }

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of("erro", "Erro interno do servidor. Contate o suporte."));
  }

  @ExceptionHandler(DisponibilidadeException.class)
  public ResponseEntity<Map<String, String>> handleDisponibilidadeException(DisponibilidadeException ex) {
    log.warn("Erro de disponibilidade: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(Map.of("erro", ex.getMessage()));
  }

}