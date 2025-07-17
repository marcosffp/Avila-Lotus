package avila.lotus.back.config.mail;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;


@Configuration
public class MailAgendamentosConfig {

  @Value("${agendamentos.host}")
  private String host;

  @Value("${agendamentos.port}")
  private int port;

  @Value("${agendamentos.username}")
  private String username;

  @Value("${agendamentos.password}")
  private String password;

  @Bean(name = "agendamentosMailSender")
  public JavaMailSender agendamentosMailSender() {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    mailSender.setHost(host);
    mailSender.setPort(port);
    mailSender.setUsername(username);
    mailSender.setPassword(password);

    Properties props = mailSender.getJavaMailProperties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.starttls.required", "true");
    props.put("mail.smtp.connectiontimeout", "5000");
    props.put("mail.smtp.timeout", "5000");
    props.put("mail.smtp.writetimeout", "5000");

    return mailSender;
  }
}
