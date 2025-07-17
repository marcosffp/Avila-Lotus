package avila.lotus.back.modelos.entidades;

import java.time.LocalDate;
import java.time.LocalTime;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;

import avila.lotus.back.modelos.enumeradores.DiaSemana;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
@BatchSize(size = 50)
@Table(name = "disponibilidades", indexes = {
    @Index(name = "idx_disponibilidade_data", columnList = "data")
})
public class Disponibilidade {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "profissional_id", nullable = false)
  @JsonBackReference
  private Profissional profissional;

  @Future(message = "A data da disponibilidade deve estar no futuro")
  @Column(nullable = false)
  private LocalDate data;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
  @Column(nullable = false)
  private LocalTime horaInicio;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
  @Column(nullable = false)
  private LocalTime horaFim;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private DiaSemana diaSemana;

  @Column(nullable = false)
  private boolean disponivel;

  public Disponibilidade(Profissional profissional, LocalDate data, LocalTime horaInicio) {
    this.profissional = profissional;
    this.setData(data);
    this.setHoraInicio(horaInicio);
    this.disponivel = true;
  }

  public void setHoraInicio(LocalTime horaInicio) {
    this.horaInicio = horaInicio;
    this.horaFim = horaInicio.plusHours(1);
  }

  public void setData(LocalDate data) {
    this.data = data;
    this.diaSemana = DiaSemana.fromNumero(data.getDayOfWeek().getValue());
  }
}
