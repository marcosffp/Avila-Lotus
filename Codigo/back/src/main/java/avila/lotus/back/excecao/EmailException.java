package avila.lotus.back.excecao;

public class EmailException extends RuntimeException {
  public EmailException(String message, Throwable cause) {
    super(message, cause);
  }
}
