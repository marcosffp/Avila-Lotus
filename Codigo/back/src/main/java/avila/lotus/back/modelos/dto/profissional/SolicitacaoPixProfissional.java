package avila.lotus.back.modelos.dto.profissional;

import jakarta.validation.constraints.NotBlank;

public record SolicitacaoPixProfissional(
        @NotBlank(message = "Chave pix não pode ser vazia") String pix) {

}
