package avila.lotus.back.modelos.dto.disponibilidade;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

public record SolicitacaoDisponibilidade(
        @NotNull(message = "A data da disponibilidade é obrigatória") @Future(message = "A data da disponibilidade deve estar no futuro") LocalDate data,
        @NotNull(message = "O horário de início é obrigatório") LocalTime horaInicio) {
}
