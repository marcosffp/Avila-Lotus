package avila.lotus.back.util.relatorio.gerador;

import avila.lotus.back.util.relatorio.auxiliar.AuxiliarCalculadoraDeIndicadoresDeHorario;
import avila.lotus.back.modelos.entidades.Agendamento;
import java.util.List;


public class GeradorTextoResumoHorario {

  public static String gerarTextoMelhor(String horario, long quantidade, List<Agendamento> agendamentos) {
    double taxaOcupacao = AuxiliarCalculadoraDeIndicadoresDeHorario.calcularTaxaOcupacao(horario, agendamentos);
    double ticketMedio = AuxiliarCalculadoraDeIndicadoresDeHorario.calcularTicketMedio(horario, agendamentos);
    double ticketGeral = AuxiliarCalculadoraDeIndicadoresDeHorario.calcularTicketMedioGeral(agendamentos);

    return String.format(
        "- %s\n" +
            "• Agendamentos Aprovados: %d\n" +
            "• Taxa de Ocupação: %.2f%%\n" +
            "• Ticket Médio: R$ %.2f (vs. geral R$ %.2f)\n",
        horario, quantidade, taxaOcupacao, ticketMedio, ticketGeral);
  }

  public static String gerarTextoPior(String horario, long quantidade, List<Agendamento> agendamentos) {
    double percentualNovos = AuxiliarCalculadoraDeIndicadoresDeHorario.calcularPercentualClientesNovos(horario, agendamentos);
    double taxaReagendamento = AuxiliarCalculadoraDeIndicadoresDeHorario.calcularTaxaReagendamento(horario, agendamentos);


    return String.format(
        "- %s\n" +
            "• Cancelamentos/Recusas/Ausências: %d\n" +
            "• %.2f%% dos cancelamentos são de novos clientes\n" +
            "• Taxa de Reagendamento: %.2f%%",
        horario, quantidade,percentualNovos, taxaReagendamento);
  }
}
