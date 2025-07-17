package avila.lotus.back.modelos.dto.agendamento;

import java.time.LocalDate;
import java.time.LocalTime;

public record RespostaAgendamentoProfissional(
        Long id,
        String clienteNome,
        String clienteEmail,
        String status,
        String subStatus,
        LocalDate data,
        LocalTime hora,
        String diaSemana,
        String observacoes) {
}
