package avila.lotus.back.util.relatorio.gerador;

import avila.lotus.back.modelos.entidades.Agendamento;
import avila.lotus.back.util.relatorio.auxiliar.AuxiliarCalculadoraDeIndicadores;
import avila.lotus.back.util.relatorio.financeiro.ResumoFinanceiro;

import java.util.List;

public class GeradorDeTextoResumoCliente {

  public static String gerar(String nome, List<Agendamento> lista) {
    long total = lista.size();
    long realizados = AuxiliarCalculadoraDeIndicadores.contarRealizados(lista);
    long naoRealizados = AuxiliarCalculadoraDeIndicadores.contarNaoRealizados(lista);
    long emAberto = AuxiliarCalculadoraDeIndicadores.contarEmAberto(lista);

    double realizadosPercent = AuxiliarCalculadoraDeIndicadores.calcularPorcentagemRealizados(lista);
    double naoRealizadosPercent = AuxiliarCalculadoraDeIndicadores.calcularPorcentagemNaoRealizados(lista);
    double emAbertoPercent = AuxiliarCalculadoraDeIndicadores.calcularPorcentagemEmAberto(lista);

    double faturamento = ResumoFinanceiro.calcularFaturamento(lista);

    return String.format(
        "- %s\n" +
            "• Agendamentos: %d\n" +
            "• Realizados: %d (%.2f%%)\n" +
            "• Não realizados: %d (%.2f%%)\n" +
            "• Em aberto: %d (%.2f%%)\n" +
            "• Faturamento: R$ %.2f",
        nome, total,
        realizados, realizadosPercent,
        naoRealizados, naoRealizadosPercent,
        emAberto, emAbertoPercent,
        faturamento);
  }
}
