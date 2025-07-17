package avila.lotus.back.servico.relatorio;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import avila.lotus.back.modelos.entidades.Agendamento;
import avila.lotus.back.repositorio.RepositorioAgendamento;
import avila.lotus.back.util.consulta.profissional.BuscaProfissional;
import avila.lotus.back.util.relatorio.GeradorPdfRelatorio;
import avila.lotus.back.util.validacao.relatorio.ValidacaoDadosRelatorio;

@Service
@Transactional(readOnly = true)
public class ServicoRelatorio {

  @Autowired
  private RepositorioAgendamento repositorioAgendamento;

  @Autowired
  private BuscaProfissional buscaProfissional;

  @Autowired
  private ValidacaoDadosRelatorio validacao;

  public byte[] gerarRelatorioMensal(int mes, int ano) throws IOException {
    validacao.validarMes(mes);
    YearMonth anoMes = YearMonth.of(ano, mes);
    LocalDate dataInicio = anoMes.atDay(1);
    LocalDate dataFim = anoMes.atEndOfMonth();

    List<Agendamento> agendamentos = buscarAgendamentos(dataInicio, dataFim);
    validacao.validarListaAgendamentos(agendamentos, mes, ano);

    return GeradorPdfRelatorio.gerar(agendamentos, mes, ano);
  }

  public byte[] gerarRelatorioPeriodo(String dataInicioStr, String dataFimStr) throws IOException {
    LocalDate inicio = validacao.parse(dataInicioStr);
    LocalDate fim = validacao.parse(dataFimStr);
    validacao.validarPeriodo(inicio, fim);

    List<Agendamento> agendamentos = buscarAgendamentos(inicio, fim);
    validacao.validarListaAgendamentos(agendamentos, inicio, fim);

    YearMonth anoMes = YearMonth.from(inicio);
    return GeradorPdfRelatorio.gerar(agendamentos, anoMes.getMonthValue(), anoMes.getYear());
  }

  
  private List<Agendamento> buscarAgendamentos(LocalDate inicio, LocalDate fim) {
    Long idProfissional = buscaProfissional.getProfissional().getId();
    return repositorioAgendamento.buscarPorIdProfissionalEDataEntre(idProfissional, inicio, fim);
  }
}
