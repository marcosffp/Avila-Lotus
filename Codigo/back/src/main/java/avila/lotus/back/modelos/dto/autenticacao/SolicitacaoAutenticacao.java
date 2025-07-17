package avila.lotus.back.modelos.dto.autenticacao;

import jakarta.validation.constraints.NotBlank;

public record SolicitacaoAutenticacao(
        @NotBlank(message = "email não pode ser vazio") String email,
        @NotBlank(message = "senha não pode ser vazio") String password) {
}
