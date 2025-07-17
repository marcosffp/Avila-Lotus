package avila.lotus.back.servico.autenticacao;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;

import avila.lotus.back.excecao.auth.TokenException;
import avila.lotus.back.modelos.enumeradores.TipoUsuario;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class ServicoToken {

  @Value("${jwt.secret}")
  private String segredo;

  @Value("${jwt.expiration-minutes:60}")
  private long minutosExpiracao;

  private Algorithm algoritmo;

  /**
   * Inicializa o algoritmo uma vez, evitando recriação desnecessária.
   */
  @PostConstruct
  public void inicializar() {
    this.algoritmo = Algorithm.HMAC256(segredo);
  }

  /**
   * Gera um token JWT para o usuário.
   */
  public String gerarToken(Long id, String email, String tipo) {
    try {
      return JWT.create()
          .withIssuer("auth")
          .withSubject(email)
          .withClaim("id", id)
          .withClaim("tipo", tipo)
          .withExpiresAt(gerarDataExpiracao())
          .sign(algoritmo);
    } catch (JWTCreationException exception) {
      throw new TokenException("Erro ao gerar token", exception);
    }
  }

  /**
   * Valida o token JWT e retorna o email associado.
   */
  public String validarToken(String token) {
    try {
      return JWT.require(
          algoritmo)
          .withIssuer("auth")
          .build()
          .verify(token)
          .getSubject();
    } catch (JWTVerificationException exception) {
      throw new TokenException("Token inválido ou expirado", exception);
    }
  }

  /**
   * Gera um token JWT para redefinição de senha.
   */
  public String gerarTokenRedefinicaoSenha(String email) {
    try {
      return JWT.create()
          .withIssuer("password-reset")
          .withSubject(email)
          .withExpiresAt(gerarDataExpiracao(15)) // Token válido por 15 minutos
          .sign(algoritmo);
    } catch (JWTCreationException exception) {
      throw new TokenException("Erro ao gerar token de redefinição de senha", exception);
    }
  }

  /**
   * Define o tempo de expiração do token com minutos customizados.
   */
  private Instant gerarDataExpiracao(int minutosPersonalizados) {
    return LocalDateTime.now()
        .plusMinutes(
            minutosPersonalizados)
        .toInstant(ZoneOffset.of("-03:00"));
  }

  /**
   * Valida o token de redefinição de senha e retorna o email associado.
   */
  public String validarTokenRedefinicaoSenha(String token) {
    try {
      return JWT.require(
          algoritmo)
          .withIssuer("password-reset")
          .build()
          .verify(token)
          .getSubject();
    } catch (JWTVerificationException exception) {
      throw new TokenException("Token de redefinição de senha inválido ou expirado", exception);
    }
  }

  /**
   * Define o tempo de expiração do token.
   */
  private Instant gerarDataExpiracao() {
    return LocalDateTime.now()
        .plusMinutes(
            minutosExpiracao)
        .toInstant(ZoneOffset.of("-03:00"));
  }

  /*
   * Método para obter o tipo de usuário a partir do token.
   */
  public TipoUsuario obterTipoUsuario(String token) {
    try {
      String tipo = JWT.require(
          algoritmo)
          .withIssuer("auth")
          .build()
          .verify(token)
          .getClaim("tipo")
          .asString();

      return TipoUsuario.fromString(tipo);
    } catch (JWTVerificationException exception) {
      throw new TokenException("Token inválido ou expirado", exception);
    }
  }

}