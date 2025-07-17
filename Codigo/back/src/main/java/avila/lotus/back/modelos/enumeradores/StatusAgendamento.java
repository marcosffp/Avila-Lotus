package avila.lotus.back.modelos.enumeradores;

import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusAgendamento {

  PENDENTE("PENDENTE"),
  APROVADO("APROVADO"),
  RECUSADO("RECUSADO"),
  CANCELADO("CANCELADO"),
  CANCELADO_PELO_CLIENTE("CANCELADO_PELO_CLIENTE");

  private final String nome;

  StatusAgendamento(String nome) {
    this.nome = nome;
  }

  @JsonValue
  public String getNome() {
    return nome;
  }

  @JsonCreator
  public static StatusAgendamento fromString(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("O STATUS DO AGENDAMENTO NÃO PODE SER NULO OU VAZIO.");
    }

    String normalizado = value.trim().toUpperCase().replace("-", "_").replace(" ", "_");

    return Arrays.stream(StatusAgendamento.values())
        .filter(s -> s.name().equals(normalizado) || s.nome.equalsIgnoreCase(normalizado))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("STATUS DE AGENDAMENTO INVÁLIDO: " + value));
  }

  public static String getNome(StatusAgendamento statusAgendamento) {
    return Arrays.stream(StatusAgendamento.values())
        .filter(s -> s.name().equals(statusAgendamento.name()))
        .map(s -> s.getNome())
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("STATUS DE AGENDAMENTO INVÁLIDO: " + statusAgendamento));
  }

  public static StatusAgendamento obterPorNome(String nome) {
    return Arrays.stream(StatusAgendamento.values())
        .filter(s -> s.nome.equalsIgnoreCase(nome))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("STATUS DE AGENDAMENTO INVÁLIDO: " + nome));
  }
}
