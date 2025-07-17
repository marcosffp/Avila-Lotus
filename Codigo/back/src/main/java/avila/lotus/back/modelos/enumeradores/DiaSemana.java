package avila.lotus.back.modelos.enumeradores;

import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DiaSemana {

  SEGUNDA_FEIRA(1, "SEGUNDA-FEIRA"),
  TERCA_FEIRA(2, "TERÇA-FEIRA"),
  QUARTA_FEIRA(3, "QUARTA-FEIRA"),
  QUINTA_FEIRA(4, "QUINTA-FEIRA"),
  SEXTA_FEIRA(5, "SEXTA-FEIRA"),
  SABADO(6, "SÁBADO"),
  DOMINGO(7, "DOMINGO");

  private final int numero;
  private final String nomeCompleto;

  DiaSemana(int numero, String nomeCompleto) {
    this.numero = numero;
    this.nomeCompleto = nomeCompleto;
  }

  public int getNumero() {
    return numero;
  }

  @JsonValue
  public String getNomeCompleto() {
    return nomeCompleto;
  }

  @JsonCreator
  public static DiaSemana fromString(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("O DIA DA SEMANA NÃO PODE SER NULO OU VAZIO.");
    }

    String normalizado = value.trim().toUpperCase()
        .replace("-", "_")
        .replace(" ", "_");

    return Arrays.stream(DiaSemana.values())
        .filter(d -> d.name().equals(normalizado) ||
            d.nomeCompleto.replace("-", "_").equalsIgnoreCase(normalizado))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("DIA DA SEMANA INVÁLIDO: " + value));
  }

  public static String obterNomePorDiaSemana(DiaSemana diaSemana) {
    if (diaSemana == null) return "SEM DIA DA SEMANA";
    return Arrays.stream(DiaSemana.values())
        .filter(d -> d.name().equals(diaSemana.name()))
        .map(DiaSemana::getNomeCompleto)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("DIA DA SEMANA INVÁLIDO: " + diaSemana));
  }

  public static DiaSemana fromNumero(int numero) {
    return Arrays.stream(DiaSemana.values())
        .filter(d -> d.numero == numero)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("NÚMERO DO DIA DA SEMANA INVÁLIDO: " + numero));
  }
}
