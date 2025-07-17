package avila.lotus.back.util.relatorio.gerador;


import avila.lotus.back.modelos.entidades.Agendamento;
import avila.lotus.back.util.relatorio.auxiliar.AuxiliarAgrupadorDeAgendamentos;
import avila.lotus.back.util.relatorio.auxiliar.AuxiliarCalculadoraDeIndicadores;
import avila.lotus.back.util.relatorio.classificacao.ClassificadorDeClientes;
import avila.lotus.back.util.relatorio.estatistica.Percentil;
import avila.lotus.back.util.relatorio.financeiro.ResumoFinanceiro;
import java.util.*;
import java.util.stream.Collectors;

public class GeradorDeResumoClientes {

  public record BlocosInformacoes(
      List<String> premium,
      List<String> rentavel,
      List<String> problematico) {
  }

  public static BlocosInformacoes gerarResumoPorCategoria(List<Agendamento> agendamentos) {
    Map<String, List<Agendamento>> porCliente = AuxiliarAgrupadorDeAgendamentos.agruparPorCliente(agendamentos);

    List<Double> faturamentos = porCliente.values().stream()
        .map(ResumoFinanceiro::calcularFaturamento)
        .toList();

    double p80 = Percentil.calcularPercentil80(faturamentos);

    Map<String, List<Agendamento>> premiumMap = new HashMap<>();
    Map<String, List<Agendamento>> rentavelMap = new HashMap<>();
    Map<String, List<Agendamento>> problematicoMap = new HashMap<>();

    porCliente.forEach((nome, lista) -> {
      double faturamentoRealizado = ResumoFinanceiro.calcularReceitaFinalizados(lista);
      double naoRealizadosPercent = AuxiliarCalculadoraDeIndicadores.calcularPorcentagemNaoRealizados(lista);

      String classificacao = ClassificadorDeClientes.classificar(faturamentoRealizado, naoRealizadosPercent, p80);

      switch (classificacao) {
        case "Premium" -> premiumMap.put(nome, lista);
        case "Rentável/Arriscado" -> rentavelMap.put(nome, lista);
        case "Problemático" -> problematicoMap.put(nome, lista);
        default -> {
     }
      }
    });

    return new BlocosInformacoes(
        ordenarEFormatar(premiumMap),
        ordenarEFormatar(rentavelMap),
        ordenarEFormatar(problematicoMap));
  }

  private static List<String> ordenarEFormatar(Map<String, List<Agendamento>> mapa) {
    return mapa.entrySet().stream()
        .sorted(Comparator.comparingDouble(
            e -> -ResumoFinanceiro.calcularFaturamento(e.getValue())))
        .limit(5)
        .map(e -> GeradorDeTextoResumoCliente.gerar(e.getKey(), e.getValue()))
        .collect(Collectors.toList());
  }
}
