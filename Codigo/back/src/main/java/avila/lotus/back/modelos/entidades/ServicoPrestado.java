package avila.lotus.back.modelos.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "servicos_prestados")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServicoPrestado {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "servico", nullable = false, length = 255)
  private String servico;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "profissional_id", nullable = false)
  private Profissional profissional;
}