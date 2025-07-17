package avila.lotus.back.excecao.auth;


public class TokenException extends RuntimeException {
  public TokenException(String message) {
    super(message);
  }

  public TokenException(String message, Throwable cause) {
    super(message, cause);
  }
}
