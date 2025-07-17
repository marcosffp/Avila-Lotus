package avila.lotus.back.config;

import avila.lotus.back.excecao.auth.TokenException;
import avila.lotus.back.modelos.enumeradores.TipoUsuario;
import avila.lotus.back.repositorio.RepositorioCliente;
import avila.lotus.back.repositorio.RepositorioProfisisonal;
import avila.lotus.back.servico.autenticacao.ServicoToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class SecurityFilter extends OncePerRequestFilter {

  @Autowired
  private ServicoToken ServicoToken;

  @Autowired
  private RepositorioCliente RepositorioCliente;

  @Autowired
  private RepositorioProfisisonal RepositorioProfisisonal;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    try {
      String token = recoverToken(request);
      if (token != null) {
        String email;
        boolean isPasswordReset = false;

        try {
          email = ServicoToken.validarToken(token);
        } catch (TokenException e) {
          email = ServicoToken.validarTokenRedefinicaoSenha(token);
          isPasswordReset = true;
        }

        UserDetails user = loadUserByEmail(email);

        if (user != null) {
          TipoUsuario tipoUsuario = ServicoToken.obterTipoUsuario(token); // Recupera o tipo do usuário

          // Garante que o Spring Security reconheça a role corretamente
          List<SimpleGrantedAuthority> authorities = List.of(
              new SimpleGrantedAuthority("ROLE_" + tipoUsuario.name()));

          UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null,
              authorities);

          SecurityContextHolder.getContext().setAuthentication(authentication);

          if (isPasswordReset && !request.getRequestURI().equals("/auth/reset-password")) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter()
                .write("{\"erro\": \"Token de redefinição de senha só pode ser usado para redefinir a senha\"}");
            return;
          }
        }
      }
    } catch (Exception e) {
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      response.getWriter().write("{\"erro\": \"Acesso negado: Token inválido ou expirado\"}");
      return;
    }

    filterChain.doFilter(request, response);
  }

  private String recoverToken(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    return (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
  }

  private UserDetails loadUserByEmail(String email) {
    return RepositorioCliente.buscarPorEmail(email)
        .map(UserDetails.class::cast)
        .or(() -> RepositorioProfisisonal.buscarPorEmail(email).map(UserDetails.class::cast))
        .orElse(null);
  }
}