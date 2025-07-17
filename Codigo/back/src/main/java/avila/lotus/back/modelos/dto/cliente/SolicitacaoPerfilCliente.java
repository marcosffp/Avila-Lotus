package avila.lotus.back.modelos.dto.cliente;

import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

public record SolicitacaoPerfilCliente(
    @NotBlank(message = "O nome não pode estar em branco") String nome,
    @NotBlank(message = "O telefone não pode estar em branco") @Pattern(regexp = "\\(\\d{2}\\) \\d{4,5}-\\d{4}", message = "Telefone deve estar no formato (XX) XXXXX-XXXX ou (XX) XXXX-XXXX") String telefone,
    @NotNull(message = "Data de nascimento não pode ser nula.") @Past(message = "Data de nascimento deve ser no passado") LocalDate dataNascimento,
    @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", message = "CPF deve estar no formato XXX.XXX.XXX-XX.") @NotBlank(message = "O CPF não pode estar em branco") String CPF,
    @NotBlank(message = "A chave pix não pode ser vazia") String PIX) {
}
