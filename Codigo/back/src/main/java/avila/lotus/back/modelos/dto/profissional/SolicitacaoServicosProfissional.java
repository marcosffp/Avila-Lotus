package avila.lotus.back.modelos.dto.profissional;

import java.util.List;
import jakarta.validation.constraints.NotNull;

public record SolicitacaoServicosProfissional(
        @NotNull(message = "A lista de serviços prestados é obrigatória") List<String> servicosPrestados) {
}
