package avila.lotus.back.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Permitir acesso às rotas sem autenticação
                        .requestMatchers(HttpMethod.POST, "/cliente/cadastrar", "/prof/cadastrar", "/auth/login",
                                "/auth/forgot-password", "/auth/reset-password")
                        .permitAll()
                        // Permitir acesso ao perfil público de profissional
                        .requestMatchers(HttpMethod.GET, "/prof/pix-e-nome", "/prof/sobre-mim", "/prof/servicos")
                        .permitAll()
                        // Requer a role PROFISSIONAL para acessar endpoints específicos de
                        // profissionais
                        .requestMatchers(
                                "/prof/editar-servicos", "/prof/editar-sobre-mim",
                                "/disponibilidade/cadastrar",
                                "/disponibilidade/todas", "/disponibilidade/data",
                                "/disponibilidade/dia-semana", "/disponibilidade/entre-datas",
                                "/disponibilidade/excluir")
                        .hasRole("PROFISSIONAL")
                        // Requer a role CLIENTE para acessar endpoints específicos de clientes
                        .requestMatchers("/cliente/editar-anamnese", "/cliente/editar-perfil")
                        .hasRole("CLIENTE")
                        // Configuração dos endpoints de agendamento para as roles CLIENTE e
                        // PROFISSIONAL
                        .requestMatchers("/agendamentos/criar", "/agendamentos/cliente/status",
                                "/agendamentos/cliente/data", "/agendamentos/cliente", "/agendamentos/cancelar-cliente")
                        .hasRole("CLIENTE")
                        .requestMatchers("/agendamentos/aprovar", "/agendamentos/recusar", "/agendamentos/cancelar",
                                "/agendamentos/profissional/status", "/agendamentos/profissional/data")
                        .hasRole("PROFISSIONAL")
                        // Permitir o acesso autenticado para todas as outras rotas
                        .anyRequest().authenticated())
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("https://avila-lotus.onrender.com")); 
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Métodos permitidos
        configuration.setAllowedHeaders(List.of("*")); // Permite todos os headers
        configuration.setAllowCredentials(true); // Permite cookies/autenticação

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
