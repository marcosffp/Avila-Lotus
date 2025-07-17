package avila.lotus.back.modelos.enumeradores;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SubStatus {
  REEMBOLSADO("REEMBOLSADO"),
  REEMBOLSO_PENDENTE("REEMBOLSO PENDENTE"),
  FINALIZADO("FINALIZADO"),
  AUSENTE("AUSENTE");

  private final String nome;

  SubStatus(String nome) {
    this.nome = nome;
  }

  @JsonValue
  public String getNome() {
    return nome;
  }

  @JsonCreator
  public static SubStatus fromString(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("O SUBSTATUS DO AGENDAMENTO NÃO PODE SER NULO OU VAZIO.");
    }

    String normalizado = value.trim().toUpperCase().replace("-", "_").replace(" ", "_");

    return Arrays.stream(SubStatus.values())
        .filter(s -> s.name().equals(normalizado) || s.nome.equalsIgnoreCase(normalizado))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("SUBSTATUS DE AGENDAMENTO INVÁLIDO: " + value));
  }

  public static String obterNomePorSubstatus(SubStatus subStatus) {
    if (subStatus == null) return "SEM SUBSTATUS";
    return Arrays.stream(SubStatus.values())
        .filter(s -> s.name().equals(subStatus.name()))
        .map(
            SubStatus::getNome)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("SUBSTATUS DE AGENDAMENTO INVÁLIDO: " + subStatus));
  }

  public static SubStatus obterPorNome(String nome) {
    return Arrays.stream(SubStatus.values())
        .filter(s -> s.nome.equalsIgnoreCase(nome))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("SUBSTATUS DE AGENDAMENTO INVÁLIDO: " + nome));
  }
}
