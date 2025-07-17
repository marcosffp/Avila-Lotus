package avila.lotus.back.modelos.dto.agendamento;

import java.time.LocalDate;
import java.time.LocalTime;

public record RespostaAgendamento(
    Long id,
    String status,
    String subStatus,
    LocalDate data,
    LocalTime hora,
    String diaSemana,
    String observacoes,
    Double valorTotal) {
}
