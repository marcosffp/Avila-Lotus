package avila.lotus.back.modelos.dto.profissional;

import jakarta.validation.constraints.NotBlank;

public record SolicitacaoSobreMimProfissional(
    @NotBlank(message = "sobreMim não pode ser vazio") String sobreMim) {
}
