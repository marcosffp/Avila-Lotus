package avila.lotus.back.modelos.entidades;

import java.time.LocalDate;
import java.time.LocalTime;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicUpdate;

import avila.lotus.back.modelos.enumeradores.StatusAgendamento;
import avila.lotus.back.modelos.enumeradores.SubStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DynamicUpdate
@BatchSize(size = 50)
@Table(name = "agendamentos", indexes = {
    @Index(name = "idx_agendamento_cliente", columnList = "cliente_id"),
    @Index(name = "idx_agendamento_profissional", columnList = "profissional_id"),
    @Index(name = "idx_agendamento_status", columnList = "status")
})
public class Agendamento {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "cliente_id", nullable = false)
  private Cliente cliente;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "profissional_id", nullable = false)
  private Profissional profissional;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "disponibilidade_id", nullable = false)
  private Disponibilidade disponibilidade;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private StatusAgendamento status;

  @Enumerated(EnumType.STRING)
  @Column(nullable = true, length = 20)
  private SubStatus subStatus;

  @Column(nullable = false)
  private LocalTime hora;

  @Column(nullable = false)
  private LocalDate data;

  @Column(name = "valor_recebido", nullable = false)
  private Double valorRecebido;

  @Column(name = "observacao")
  private String observacao;

  public Agendamento(Cliente cliente, Profissional profissional, Disponibilidade disponibilidade, double valorRecebido,
      String observacao) {
    this.valorRecebido = valorRecebido;
    this.observacao = observacao;
    this.cliente = cliente;
    this.profissional = profissional;
    this.disponibilidade = disponibilidade;
    this.extrairDataDaDisponibilidade(disponibilidade);
    this.extrairHoraDaDisponibilidade(disponibilidade);
    this.disponibilidade.setDisponivel(false);
    this.status = StatusAgendamento.PENDENTE;
    this.subStatus = null;
  }

  public void aprovar() {
    this.status = StatusAgendamento.APROVADO;
  }

  public void cancelarPeloCliente() {
    this.status = StatusAgendamento.CANCELADO_PELO_CLIENTE;
    this.subStatus = SubStatus.REEMBOLSO_PENDENTE;
    this.liberarDisponibilidade();
  }

  public void recusar() {
    this.status = StatusAgendamento.RECUSADO;
    this.subStatus = SubStatus.REEMBOLSADO;
    this.liberarDisponibilidade();
  }

  public void cancelar() {
    this.status = StatusAgendamento.CANCELADO;
    this.subStatus = SubStatus.REEMBOLSADO;
    this.liberarDisponibilidade();
  }

  public void reembolsar() {
    this.status = StatusAgendamento.CANCELADO_PELO_CLIENTE;
    this.subStatus = SubStatus.REEMBOLSADO;
  }

  private void liberarDisponibilidade() {
    this.disponibilidade.setDisponivel(true);
  }

  public void extrairDataDaDisponibilidade(Disponibilidade disponibilidade) {
    this.data = disponibilidade.getData();
  }

  public void extrairHoraDaDisponibilidade(Disponibilidade disponibilidade) {
    this.hora = disponibilidade.getHoraInicio();
  }

  public void finalizar() {
    this.subStatus = SubStatus.FINALIZADO;
  }

  public void ausente() {
    this.subStatus = SubStatus.AUSENTE;
  }
}
