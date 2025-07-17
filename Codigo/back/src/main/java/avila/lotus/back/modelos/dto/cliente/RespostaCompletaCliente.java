package avila.lotus.back.modelos.dto.cliente;

import java.time.LocalDate;

public record RespostaCompletaCliente(
    String nome,
    String telefone,
    LocalDate dataNascimento,
    String CPF,
    String PIX,
    String email,
    boolean cirurgia,
    boolean remedio,
    boolean anticoncepcional,
    boolean alergiaMedicamento,
    boolean pressao,
    boolean tratamento,
    boolean gestante,
    boolean problemas,
    boolean fumante,
    boolean hepatite,
    boolean diabetes,
    boolean asma,
    boolean cardiaco,
    boolean convulsao,
    boolean renal,
    boolean tontura,
    String nomeCirurgia,
    String nomeRemedio,
    String nomeAnticoncepcional,
    String nomeAlergiaMedicamento,
    String valorPressao,
    String nomeTratamento,
    String queixas) {

}
