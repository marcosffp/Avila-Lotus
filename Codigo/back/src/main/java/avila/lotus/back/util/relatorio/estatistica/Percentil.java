package avila.lotus.back.util.relatorio.estatistica;

import java.util.List;
import java.util.Collection;


public class Percentil {


  public static double calcularPercentil80(List<Double> valores) {
    return calcularPercentil(valores, 0.8);
  }

  private static double calcularPercentil(List<Double> valores, double percentil) {
    List<Double> ordenados = valores.stream()
        .sorted()
        .toList();
    int idx = (int) Math.ceil(ordenados.size() * percentil) - 1;
    return ordenados.isEmpty() ? 0 : ordenados.get(Math.max(0, idx));
  }


  private static double calcularPercentil(Collection<? extends Number> valores, double percentil) {
    List<Double> ordenados = valores.stream()
        .map(Number::doubleValue)
        .sorted()
        .toList();
    int idx = (int) Math.ceil(ordenados.size() * percentil) - 1;
    return ordenados.isEmpty() ? 0 : ordenados.get(Math.max(0, idx));
  }

  public static double calcularPercentil80(Collection<? extends Number> valores) {
    return calcularPercentil(valores, 0.8);
  }
}
