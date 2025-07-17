package avila.lotus.back.servico.email;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class ServicoEmailRedefinicaoSenha {

  @Qualifier("noReplyMailSender")
  @Autowired
  private JavaMailSender mailSender;

  @Value("${no-reply.username}")
  private String fromEmail;

  private String emailTemplate;

  @PostConstruct
  private void carregarTemplate() throws IOException {
    ClassPathResource resource = new ClassPathResource("static/codigo_troca_senha.html");
    try (InputStream inputStream = resource.getInputStream()) {
      this.emailTemplate = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }
  }

  @Async
  public void enviarEmailRedefinicao(String destinatario, String assunto, String codigoVerificacao) {
    try {
      MimeMessage mensagem = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mensagem, true);

      helper.setFrom(fromEmail);
      helper.setTo(destinatario);
      helper.setSubject(assunto);

      String conteudo = gerarConteudoEmail(codigoVerificacao);
      helper.setText(conteudo, true);

      mailSender.send(mensagem);
    } catch (MessagingException e) {
      throw new RuntimeException("Erro ao montar ou enviar e-mail de redefinição de senha", e);
    }
  }

  private String gerarConteudoEmail(String codigo) {
    return emailTemplate.replace("{{CODE}}", codigo != null ? codigo : "");
  }
}
