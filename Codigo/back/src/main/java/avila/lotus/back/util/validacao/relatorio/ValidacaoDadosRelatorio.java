package avila.lotus.back.util.validacao.relatorio;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;

import avila.lotus.back.excecao.RelatorioException;
import avila.lotus.back.modelos.entidades.Agendamento;

@Component
public class ValidacaoDadosRelatorio {

  public LocalDate parse(String data) {
    try {
      return LocalDate.parse(data);
    } catch (Exception e) {
      throw new RelatorioException("Data inválida: " + data + ". O formato correto é yyyy-MM-dd.");
    }
  }

  public void validarPeriodo(LocalDate inicio, LocalDate fim) {
    if (inicio.isAfter(fim)) {
      throw new RelatorioException("Data de início não pode ser depois da data de fim.");
    }
  }

  public void validarMes(int mes) {
    if (mes < 1 || mes > 12) {
      throw new RelatorioException("Mês inválido. Informe um valor entre 1 e 12.");
    }
  }

  public void validarListaAgendamentos(List<Agendamento> agendamentos, LocalDate inicio, LocalDate fim) {
    if (agendamentos == null || agendamentos.isEmpty()) {
      throw new RelatorioException("Nenhum agendamento encontrado entre " + inicio + " e " + fim + ".");
    }
  }

  public void validarListaAgendamentos(List<Agendamento> agendamentos, int mes, int ano) {
    if (agendamentos == null || agendamentos.isEmpty()) {
      throw new RelatorioException("Nenhum agendamento encontrado para o mês " + mes + " do ano " + ano + ".");
    }
  }
}
