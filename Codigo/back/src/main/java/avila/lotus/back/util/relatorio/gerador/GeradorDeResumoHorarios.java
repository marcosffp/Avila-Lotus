package avila.lotus.back.util.relatorio.gerador;

import avila.lotus.back.modelos.entidades.Agendamento;

import java.util.*;


public class GeradorDeResumoHorarios {

  public record BlocosInformacoes(List<String> melhores, List<String> piores) {
  }

  public static BlocosInformacoes gerarResumoPorHorario(List<Agendamento> agendamentos) {
    var melhores = GeradorAnaliseDeHorarios.melhoresHorarios(agendamentos);
    var piores = GeradorAnaliseDeHorarios.pioresHorarios(agendamentos);

    List<String> melhoresFormatados = melhores.stream()
        .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
        .map(e -> GeradorTextoResumoHorario.gerarTextoMelhor(e.getKey(), e.getValue(), agendamentos))
        .toList();

    List<String> pioresFormatados = piores.stream()
        .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
        .map(e -> GeradorTextoResumoHorario.gerarTextoPior(e.getKey(), e.getValue(), agendamentos))
        .toList();

    return new BlocosInformacoes(melhoresFormatados, pioresFormatados);
  }
}
