package avila.lotus.back.modelos.dto.cliente;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SolicitacaoCadastroCliente(
        @NotBlank(message = "email não pode ser vazio") @Email(message = "Formato de email inválido") String email,

        @NotBlank(message = "password não pode ser vazio") @Size(min = 5, message = "A senha deve ter pelo menos 5 caracteres") @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{5,}$", message = "A senha deve ter pelo menos uma letra maiúscula, uma minúscula, um número e um símbolo") String password) {
}
