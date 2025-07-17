package avila.lotus.back.util.validacao.agendamento;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import avila.lotus.back.excecao.AgendamentoException;
import avila.lotus.back.modelos.entidades.Agendamento;
import avila.lotus.back.modelos.entidades.Cliente;
import avila.lotus.back.modelos.entidades.Disponibilidade;

import avila.lotus.back.modelos.enumeradores.StatusAgendamento;
import avila.lotus.back.modelos.enumeradores.SubStatus;
import avila.lotus.back.util.consulta.agendamento.BuscaAgendamento;
import avila.lotus.back.util.consulta.cliente.BuscaCliente;
import avila.lotus.back.util.consulta.disponibilidade.BuscaDisponibilidade;
import avila.lotus.back.util.validacao.comprovante.ValidacaoDadoComprovante;
import avila.lotus.back.util.validacao.disponibilidade.ValidacaoDisponibilidade;

@Component
public class ValidacaoDadoAgendamento {

  private static final int TEMPO_MAXIMO_CANCELAMENTO = 24;

  @Value("${contato.username}")
  private String emailPermitido;

  @Autowired
  private ValidacaoDadoComprovante validacaoDadoComprovante;

  @Autowired
  private ValidacaoDisponibilidade validacaoDisponibilidade;

  @Autowired
  private BuscaAgendamento buscaAgendamento;

  @Autowired
  private BuscaDisponibilidade buscaDisponibilidade;

  @Autowired
  private BuscaCliente buscaCliente;

  public DadosAgendamentoValidado validarAntesCriacao(String token, long disponibilidadeId,
      MultipartFile comprovantePdf) {
    validacaoDadoComprovante.validarComprovantePdf(comprovantePdf);
    Cliente cliente = buscaCliente.buscarClientePorToken(token);
    Disponibilidade disponibilidade = buscaDisponibilidade.buscarDisponibilidadePorId(disponibilidadeId);
    validacaoDisponibilidade.validarDisponibilidade(disponibilidade);
    return new DadosAgendamentoValidado(cliente, disponibilidade);
  }

  public Agendamento validarAntesAprovar(long idAgendamento) {
    Agendamento agendamento = buscaAgendamento.buscarAgendamentoPorId(idAgendamento);
    if (agendamento.getStatus() != StatusAgendamento.PENDENTE) {
      throw new AgendamentoException("O agendamento pode ser aprovado apenas se estiver pendente, atualmente agendamento está com o status: "
          + agendamento.getStatus());
    }
    return agendamento;
  }

  public Agendamento validarAntesRecusar(long idAgendamento, MultipartFile comprovantePdf)
      throws Exception {
    Agendamento agendamento = buscaAgendamento.buscarAgendamentoPorId(idAgendamento);
    validacaoDadoComprovante.validarComprovantePdf(comprovantePdf);
    if (agendamento.getStatus() != StatusAgendamento.PENDENTE) {
      throw new AgendamentoException("O agendamento não está pendente, atualmente agendamento está com o status: "
          + agendamento.getStatus());
    }
    return agendamento;
  }

  public Agendamento validarAntesCancelar(long idAgendamento,
      MultipartFile comprovantePdf,String motivo) throws Exception {
    Agendamento agendamento = buscaAgendamento.buscarAgendamentoPorId(idAgendamento);
    if (motivo.isBlank()) {
      throw new AgendamentoException("O motivo do cancelamento não pode ser vazio.");
      
    }
    if (agendamento.getStatus() != StatusAgendamento.APROVADO) {
      throw new AgendamentoException("O agendamento não está aprovado, atualmente agendamento está com o status: "
          + agendamento.getStatus());
    }
    validacaoDadoComprovante.validarComprovantePdf(comprovantePdf);
    return agendamento;
  }

  public Agendamento validarAntesCancelarPeloCliente(String token, long agendamentoId) {
    Agendamento agendamento = buscaAgendamento.buscarAgendamentoPorId(agendamentoId);
    Cliente cliente = buscaCliente.buscarClientePorToken(token);

    if (!agendamento.getCliente().getId().equals(cliente.getId())) {
      throw new AgendamentoException("Este agendamento não pertence ao cliente.");
    }

    ZoneId zona = ZoneId.of("America/Sao_Paulo");
    ZonedDateTime agendamentoDataHora = agendamento.getData()
        .atTime(agendamento.getHora())
        .atZone(zona);

    ZonedDateTime agora = ZonedDateTime.now(zona);
    ZonedDateTime prazoCancelamento = agendamentoDataHora.minusHours(TEMPO_MAXIMO_CANCELAMENTO);
    StatusAgendamento status = agendamento.getStatus();

    if (status == StatusAgendamento.APROVADO && agora.isAfter(prazoCancelamento)) {
      throw new AgendamentoException("Cancelamento não permitido. O prazo de " + TEMPO_MAXIMO_CANCELAMENTO + " horas não foi atendido.");
    } else if (status != StatusAgendamento.PENDENTE) {
      throw new AgendamentoException("Somente agendamentos pendentes ou aprovados podem ser cancelados. Status atual: " + status);
    }

    return agendamento;
  }

  public Agendamento validarAntesFinalizar(long idAgendamento) {
    Agendamento agendamento = buscaAgendamento.buscarAgendamentoPorId(idAgendamento);
    if (agendamento.getStatus() != StatusAgendamento.APROVADO) {
      throw new AgendamentoException("O agendamento não está aprovado, atualmente agendamento está com o status: "
          + agendamento.getStatus());
    }
    ZoneId saoPauloZone = ZoneId.of("America/Sao_Paulo");
    ZonedDateTime dataHoraAgendamento = agendamento.getData().atTime(agendamento.getHora())
        .atZone(saoPauloZone);
    ZonedDateTime agora = ZonedDateTime.now(saoPauloZone);

    if (agora.isBefore(dataHoraAgendamento)) {
      throw new AgendamentoException("O agendamento só pode ser finalizado após o horário agendado.");
    }
    return agendamento;
  }

  public Agendamento validarAntesAusente(long idAgendamento) {
    Agendamento agendamento = buscaAgendamento.buscarAgendamentoPorId(idAgendamento);
    if (agendamento.getStatus() != StatusAgendamento.APROVADO) {
      throw new AgendamentoException("O agendamento não está aprovado, atualmente agendamento está com o status: "
          + agendamento.getStatus());
    }
    ZoneId saoPauloZone = ZoneId.of("America/Sao_Paulo");
    ZonedDateTime dataHoraAgendamento = agendamento.getData().atTime(agendamento.getHora())
        .atZone(saoPauloZone);
    ZonedDateTime agora = ZonedDateTime.now(saoPauloZone);

    if (agora.isBefore(dataHoraAgendamento)) {
      throw new AgendamentoException("O agendamento só pode ser ausente após o horário agendado.");
    }
    return agendamento;
  }

  public Agendamento validarAntesReembolsar(long idAgendamento,
      MultipartFile comprovantePdf) throws Exception {
    Agendamento agendamento = buscaAgendamento.buscarAgendamentoPorId(idAgendamento);
    if (agendamento.getSubStatus() != SubStatus.REEMBOLSO_PENDENTE) {
      throw new AgendamentoException(
          "O agendamento já foi reembolsado, atualmente agendamento está com o status: "
              + agendamento.getSubStatus());
    }
    if (agendamento.getStatus() != StatusAgendamento.CANCELADO_PELO_CLIENTE) {
      throw new AgendamentoException("O agendamento não está cancelado pelo cliente, atualmente agendamento está com o status: "
          + agendamento.getStatus());
    }
    validacaoDadoComprovante.validarComprovantePdf(comprovantePdf);
    return agendamento;
  }

}
