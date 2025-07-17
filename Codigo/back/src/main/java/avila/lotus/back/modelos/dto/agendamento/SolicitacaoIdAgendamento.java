package avila.lotus.back.modelos.dto.agendamento;

import jakarta.validation.constraints.NotNull;

public record SolicitacaoIdAgendamento(
    @NotNull(message = "O id do agendamento é obrigatório") Long idAgendamento
) {
}
