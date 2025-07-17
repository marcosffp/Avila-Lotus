package avila.lotus.back.servico.email;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import avila.lotus.back.modelos.entidades.Agendamento;
import avila.lotus.back.repositorio.RepositorioAgendamento;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class ServicoEmailCancelamentoPeloCliente {

  @Qualifier("agendamentosMailSender")
  @Autowired
  private JavaMailSender enviadorEmail;

  @Autowired
  private RepositorioAgendamento repositorioAgendamento;

  @Value("${agendamentos.username}")
  private String emailRemetente;

  @Value("${contato.username}")
  private String emailContato;

  private String templateEmail;

  @PostConstruct
  private void carregarTemplate() throws IOException {
    ClassPathResource recurso = new ClassPathResource("static/agendamento_cancelado_cliente.html");
    try (InputStream inputStream = recurso.getInputStream()) {
      this.templateEmail = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }
  }

  public void prepararEmailCancelamento(String assunto, Long idAgendamento) {
    Agendamento agendamento = repositorioAgendamento.findById(idAgendamento)
        .orElseThrow(() -> new RuntimeException("Agendamento não encontrado com ID: " + idAgendamento));

    enviarEmailAssincrono(
        assunto,
        agendamento.getCliente().getEmail(),
        agendamento.getCliente().getNome(),
        agendamento.getData(),
        agendamento.getHora(),
        agendamento.getObservacao(),
        agendamento.getValorRecebido());
  }

  @Async
  public void enviarEmailAssincrono(String assunto, String emailCliente, String nomeCliente,
      LocalDate data, LocalTime hora, String observacao,
      double valorPago) {
    try {
      MimeMessage mensagem = enviadorEmail.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mensagem, true);

      helper.setFrom(emailRemetente);
      helper.setTo(emailContato);
      helper.setSubject(assunto);

      String conteudo = gerarConteudoEmail(nomeCliente, emailCliente, data, hora, observacao, valorPago);
      helper.setText(conteudo, true);

      enviadorEmail.send(mensagem);

    } catch (MessagingException e) {
      throw new RuntimeException("Falha ao enviar e-mail de cancelamento: " + e.getMessage(), e);
    }
  }

  private String gerarConteudoEmail(String nomeCliente, String emailCliente,
      LocalDate data, LocalTime hora,
      String observacao, double valorPago) {

    String dataFormatada = data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    String horaFormatada = hora.format(DateTimeFormatter.ofPattern("HH:mm"));
    String valorFormatado = String.format("R$ %.2f", valorPago);

    return templateEmail
        .replace("{{NOMEDOCLIENTE}}", tratarValor(nomeCliente))
        .replace("{{EMAILDOCLIENTE}}", tratarValor(emailCliente))
        .replace("{{DATADOAGENDAMENTO}}", tratarValor(dataFormatada))
        .replace("{{HORARIO}}", tratarValor(horaFormatada))
        .replace("{{VALORPAGO}}", tratarValor(valorFormatado))
        .replace("{{OBSERVACAO}}", tratarValor(observacao));
  }

  private String tratarValor(String valor) {
    return valor != null ? valor : "";
  }
}