package avila.lotus.back.modelos.dto.autenticacao;

public record SolicitacaoDeRedefinicaoDeSenha(String email, String code, String newPassword) {
}
