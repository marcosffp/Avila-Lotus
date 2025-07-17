package avila.lotus.back.excecao.auth;

public class TokenInvalidoException extends RuntimeException {
  public TokenInvalidoException(String message) {
    super(message);
  }
}
