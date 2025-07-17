package avila.lotus.back.modelos.dto.cliente;

import jakarta.validation.constraints.NotNull;

public record SolicitacaoAnamneseCliente(
        @NotNull(message = "O campo cirurgia não pode ser nulo") Boolean cirurgia,

        @NotNull(message = "O campo remedio não pode ser nulo") Boolean remedio,

        @NotNull(message = "O campo anticoncepcional não pode ser nulo") Boolean anticoncepcional,

        @NotNull(message = "O campo alergiaMedicamento não pode ser nulo") Boolean alergiaMedicamento,

        @NotNull(message = "O campo pressao não pode ser nulo") Boolean pressao,

        @NotNull(message = "O campo tratamento não pode ser nulo") Boolean tratamento,

        @NotNull(message = "O campo gestante não pode ser nulo") Boolean gestante,

        @NotNull(message = "O campo problemas não pode ser nulo") Boolean problemas,

        @NotNull(message = "O campo fumante não pode ser nulo") Boolean fumante,

        @NotNull(message = "O campo hepatite não pode ser nulo") Boolean hepatite,

        @NotNull(message = "O campo diabetes não pode ser nulo") Boolean diabetes,

        @NotNull(message = "O campo asma não pode ser nulo") Boolean asma,

        @NotNull(message = "O campo cardiaco não pode ser nulo") Boolean cardiaco,

        @NotNull(message = "O campo convulsao não pode ser nulo") Boolean convulsao,

        @NotNull(message = "O campo renal não pode ser nulo") Boolean renal,

        @NotNull(message = "O campo tontura não pode ser nulo") Boolean tontura,

        String nomeCirurgia,
        String nomeRemedio,
        String nomeAnticoncepcional,
        String nomeAlergiaMedicamento,
        String valorPressao,
        String nomeTratamento,
        String queixas) {
}
