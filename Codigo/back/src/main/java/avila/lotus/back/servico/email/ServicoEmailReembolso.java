package avila.lotus.back.servico.email;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
public class ServicoEmailReembolso {

  @Qualifier("agendamentosMailSender")
  @Autowired
  private JavaMailSender mailSender;

  @Autowired
  private RepositorioAgendamento RepositorioAgendamento;

  @Value("${agendamentos.username}")
  private String fromEmail;

  private String emailTemplate;

  @PostConstruct
  private void loadTemplateOnce() throws IOException {
    ClassPathResource resource = new ClassPathResource("static/agendamento_reembolso.html");
    try (InputStream inputStream = resource.getInputStream()) {
      this.emailTemplate = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }
  }

  public void prepararEnvioEmail(String subject, Long id, byte[] pdfBytes, String nomeArquivo) {
    Agendamento agendamento = RepositorioAgendamento.findById(id)
        .orElseThrow(() -> new RuntimeException("Agendamento não encontrado com ID: " + id));

    enviarEmailAssincrono(
        subject,
        agendamento.getCliente().getEmail(),
        agendamento.getCliente().getNome(),
        agendamento.getData(),
        agendamento.getHora(),
        agendamento.getObservacao(),
        pdfBytes,
        nomeArquivo);
  }

  @Async
  public void enviarEmailAssincrono(String subject, String clienteEmail, String nomeCliente,
      LocalDate data, LocalTime hora, String observacao, byte[] pdfBytes, String nomeArquivo) {
    try {

      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true);

      helper.setFrom(fromEmail);
      helper.setTo(clienteEmail);
      helper.setSubject(subject);

      String conteudo = gerarConteudoEmail(nomeCliente, data, hora, observacao);
      helper.setText(conteudo, true);

      // Adiciona o anexo (PDF)
      helper.addAttachment(nomeArquivo, new jakarta.activation.DataSource() {
        @Override
        public InputStream getInputStream() throws IOException {
          return new ByteArrayInputStream(pdfBytes);
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
          throw new IOException("Read-only data");
        }

        @Override
        public String getContentType() {
          return "application/pdf";
        }

        @Override
        public String getName() {
          return nomeArquivo;
        }
      });

      mailSender.send(message);

    } catch (MessagingException e) {
      throw new RuntimeException("Erro ao montar ou enviar e-mail: " + e.getMessage(), e);
    }
  }

  private String gerarConteudoEmail(String nomeCliente,
      LocalDate data, LocalTime hora,
      String observacao) {

    String dataFormatada = data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    String horaFormatada = hora.format(DateTimeFormatter.ofPattern("HH:mm"));

    return emailTemplate
        .replace("{{NOMECLIENTE}}", safe(nomeCliente))
        .replace("{{DATADOAGENDAMENTO}}", safe(dataFormatada))
        .replace("{{HORARIO}}", safe(horaFormatada))
        .replace("{{OBSERVACAO}}", safe(observacao));
  }

  private String safe(String value) {
    return value != null ? value : "";
  }
}