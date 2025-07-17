package avila.lotus.back.modelos.dto.profissional;

import jakarta.validation.constraints.NotBlank;

public record SolicitacaoNomeProfissional(
        @NotBlank(message = "Nome não pode ser vazio") String nome) {

}
