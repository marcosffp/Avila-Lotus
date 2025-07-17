package avila.lotus.back.servico.email;

import java.io.IOException;
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


import java.io.InputStream;

import avila.lotus.back.modelos.entidades.Agendamento;
import avila.lotus.back.repositorio.RepositorioAgendamento;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class ServicoEmailAprovacao {
  @Qualifier("agendamentosMailSender")
  @Autowired
  private JavaMailSender enviadorEmail;

  @Autowired
  private RepositorioAgendamento repositorioAgendamento;

  @Value("${agendamentos.username}")
  private String emailRemetente;

  private String templateEmail;

  @PostConstruct
  private void carregarTemplate() throws IOException {
    ClassPathResource recurso = new ClassPathResource("static/agendamento_aprovado.html");
    try (InputStream inputStream = recurso.getInputStream()) {
      this.templateEmail = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }
  }

  public void prepararEmailAprovacao(String assunto, Long idAgendamento) {
    Agendamento agendamento = repositorioAgendamento.findById(idAgendamento)
        .orElseThrow(() -> new RuntimeException("Agendamento não encontrado com ID: " + idAgendamento));

    enviarEmailAssincrono(
        assunto,
        agendamento.getCliente().getEmail(),
        agendamento.getCliente().getNome(),
        agendamento.getData(),
        agendamento.getHora(),
        agendamento.getObservacao());
  }

  @Async
  public void enviarEmailAssincrono(String assunto, String emailCliente, String nomeCliente,
      LocalDate data, LocalTime hora, String observacao) {
    try {
      MimeMessage mensagem = enviadorEmail.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mensagem, true);

      helper.setFrom(emailRemetente);
      helper.setTo(emailCliente);
      helper.setSubject(assunto);

      String conteudo = gerarConteudoEmail(nomeCliente, data, hora, observacao);
      helper.setText(conteudo, true);

      enviadorEmail.send(mensagem);

    } catch (MessagingException e) {
      throw new RuntimeException("Falha ao enviar e-mail: " + e.getMessage(), e);
    }
  }

  private String gerarConteudoEmail(String nomeCliente, LocalDate data,
      LocalTime hora, String observacao) {

    String dataFormatada = data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    String horaFormatada = hora.format(DateTimeFormatter.ofPattern("HH:mm"));

    return templateEmail
        .replace("{{NOMECLIENTE}}", tratarValor(nomeCliente))
        .replace("{{DATADOAGENDAMENTO}}", tratarValor(dataFormatada))
        .replace("{{HORARIO}}", tratarValor(horaFormatada))
        .replace("{{OBSERVACAO}}", tratarValor(observacao));
  }

  private String tratarValor(String valor) {
    return valor != null ? valor : "";
  }
}