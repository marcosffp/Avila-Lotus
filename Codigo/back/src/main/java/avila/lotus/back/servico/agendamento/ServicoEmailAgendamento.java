package avila.lotus.back.servico.agendamento;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import avila.lotus.back.servico.email.ServicoEmailAprovacao;
import avila.lotus.back.servico.email.ServicoEmailCancelamento;
import avila.lotus.back.servico.email.ServicoEmailCancelamentoPeloCliente;
import avila.lotus.back.servico.email.ServicoEmailPendente;
import avila.lotus.back.servico.email.ServicoEmailRecusado;
import avila.lotus.back.servico.email.ServicoEmailReembolso;

@Service
public class ServicoEmailAgendamento {

  @Autowired
  private ServicoEmailPendente servicoEmailPendente;

  @Autowired
  private ServicoEmailCancelamentoPeloCliente servicoEmailCancelamentoPeloCliente;

  @Autowired
  private ServicoEmailRecusado servicoEmailRecusado;

  @Autowired
  private ServicoEmailAprovacao servicoEmailAprovacao;

  @Autowired
  private ServicoEmailCancelamento servicoEmailCancelamento;

  @Autowired
  private ServicoEmailReembolso servicoEmailReembolso;

  public void enviarAgendamentoPendente(Long idAgendamento,
      MultipartFile comprovantePdf) throws Exception {
    servicoEmailPendente.prepararEmailPendente(
        "Solicitação de Agendamento",
        idAgendamento,
        comprovantePdf.getBytes(),
        comprovantePdf.getOriginalFilename());

  }

  public void enviarEmailAprovacao(long idAgendamento) {
    servicoEmailAprovacao.prepararEmailAprovacao("Aprovado seu Agendamento", idAgendamento);
  }

  public void enviarEmailRecusamento(long idAgendamento, MultipartFile comprovantePdf) throws Exception {
    servicoEmailRecusado.prepararEmailRecusado(
        "Recusado seu Agendamento",
        idAgendamento,
        comprovantePdf.getBytes(),
        comprovantePdf.getOriginalFilename());
  }

  public void enviarEmalCancelamentoPeloCliente(long idAgendamento) {

    servicoEmailCancelamentoPeloCliente.prepararEmailCancelamento("Cancelamento do Agendamento",
        idAgendamento);

  }

  public void enviarEmailCancelamento(long idAgendamento, MultipartFile comprovantePdf, String motivo)
      throws Exception {
    servicoEmailCancelamento.prepararEmailCancelamento(
        "Cancelamento seu Agendamento",
        idAgendamento,
        motivo,
        comprovantePdf.getBytes(),
        comprovantePdf.getOriginalFilename());

  }

  public void enviarEmailReembolso(long idAgendamento, MultipartFile comprovantePdf) throws Exception {

    servicoEmailReembolso.prepararEnvioEmail(
        "Recusado seu Agendamento",
        idAgendamento,
        comprovantePdf.getBytes(),
        comprovantePdf.getOriginalFilename());
  }

}
