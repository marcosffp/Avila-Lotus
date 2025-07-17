package avila.lotus.back.excecao.auth;

public class LoginException extends RuntimeException {
  public LoginException(String message) {
    super(message);
  }
}
