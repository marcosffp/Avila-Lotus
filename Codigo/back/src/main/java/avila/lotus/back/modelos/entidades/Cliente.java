package avila.lotus.back.modelos.entidades;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import avila.lotus.back.modelos.enumeradores.TipoUsuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DynamicUpdate
@BatchSize(size = 50)
@Table(name = "clientes", indexes = {
    @Index(name = "idx_email", columnList = "email")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Cliente implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Email(message = "O email deve ser válido")
  @Column(name = "email", nullable = false, unique = true, columnDefinition = "VARCHAR(255) COLLATE utf8mb4_bin")
  private String email;

  @Column(name = "password", nullable = false, length = 64)
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(name = "tipo", nullable = true, length = 20)
  private TipoUsuario tipo;

  @Column(name = "nome", nullable = true)
  private String nome;

  @Column(name = "cpf", nullable = true)
  private String cpf;

  @Column(name = "telefone", nullable = true)
  private String telefone;

  @Column(name = "data_nascimento", nullable = true)
  private LocalDate dataDeNascimento;

  @Column(name = "pix", nullable = true)
  private String pix;

  @Column(name = "realizou_cirurgia", nullable = true)
  private Boolean realizouCirurgia;

  @Column(name = "faz_uso_de_remedios", nullable = true)
  private Boolean fazUsoDeRemedios;

  @Column(name = "faz_uso_de_anticoncepcional", nullable = true)
  private Boolean fazUsoDeAnticoncepcional;

  @Column(name = "tem_Alergia_AMedicamento", nullable = true)
  private Boolean temAlergiaAMedicamento;

  @Column(name = "tem_pressao_alta", nullable = true)
  private Boolean temPressaoAlta;

  @Column(name = "faz_tratamento", nullable = true)
  private Boolean fazTratamento;

  @Column(name = "esta_gestante", nullable = true)
  private Boolean estaGestante;

  @Column(name = "tem_problemas_rins_figado", nullable = true)
  private Boolean temProblemasRinsFigado;

  @Column(name = "pessoa_fumante", nullable = true)
  private Boolean pessoaFumante;

  @Column(name = "tem_hepatite", nullable = true)
  private Boolean temHepatite;

  @Column(name = "tem_diabetes", nullable = true)
  private Boolean temDiabetes;

  @Column(name = "tem_asma", nullable = true)
  private Boolean temAsma;

  @Column(name = "tem_problema_cardiaco", nullable = true)
  private Boolean temProblemaCardiaco;

  @Column(name = "tem_convulsao", nullable = true)
  private Boolean temConvulsao;

  @Column(name = "tem_problema_renal", nullable = true)
  private Boolean temProblemaRenal;

  @Column(name = "tem_tontura", nullable = true)
  private Boolean temTontura;

  @Column(name = "nome_cirurgia", length = 100, nullable = true)
  private String nomeCirurgia;

  @Column(name = "nome_remedio", length = 100, nullable = true)
  private String nomeRemedio;

  @Column(name = "nome_anticoncepcional", length = 100, nullable = true)
  private String nomeAnticoncepcional;

  @Column(name = "nome_alergia_medicamento", length = 100, nullable = true)
  private String nomeAlergiaMedicamento;

  @Column(name = "valor_pressao_arterial", length = 30, nullable = true)
  private String valorPressaoArterial;

  @Column(name = "nome_tratamento", length = 100, nullable = true)
  private String nomeTratamento;

  @Column(name = "queixas", length = 500, nullable = true)
  private String queixas;

  @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @JsonManagedReference
  private List<Agendamento> agendamentos;

  public Cliente(String email, String password) {
    this.email = email;
    this.password = password;
    this.tipo = TipoUsuario.CLIENTE;
  }

  public Cliente() {
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