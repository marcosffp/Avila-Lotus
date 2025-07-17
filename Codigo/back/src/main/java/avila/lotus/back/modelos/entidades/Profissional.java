package avila.lotus.back.modelos.entidades;

import java.util.Collection;
import java.util.List;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import avila.lotus.back.modelos.enumeradores.TipoUsuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@DynamicUpdate
@BatchSize(size = 50)
@Table(name = "profissionais", indexes = {
    @Index(name = "idx_email_profissional", columnList = "email"),
    @Index(name = "idx_cod_autenticacao", columnList = "cod_autenticacao")
})
public class Profissional implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "email", nullable = false, unique = true, columnDefinition = "VARCHAR(255) COLLATE utf8mb4_bin")
  private String email;

  @Column(name = "password", nullable = false, length = 64)
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(name = "tipo", nullable = false, length = 20)
  private TipoUsuario tipo;

  @Column(name = "cod_autenticacao", nullable = false, length = 50)
  private String codAutenticacao;

  @Column(name = "sobre_mim", columnDefinition = "TEXT")
  private String sobreMim;

  @Column(name = "nome", length = 255)
  private String nome;

  @Column (name = "pix", length = 255)
  private String pix;

  @OneToMany(mappedBy = "profissional", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ServicoPrestado> servicosPrestados;  

  @OneToMany(mappedBy = "profissional", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @JsonManagedReference
  @OrderBy("data ASC, horaInicio ASC")
  private List<Disponibilidade> disponibilidades;

  @OneToMany(mappedBy = "profissional", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @JsonManagedReference
  private List<Agendamento> agendamentos;

  public Profissional(String email, String password, String codAutenticacao) {
    this.email = email;
    this.password = password;
    this.codAutenticacao = codAutenticacao;
    this.tipo = TipoUsuario.PROFISSIONAL;
  }
  
  public Profissional() {
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(tipo.name()));
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
