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
public class ServicoEmailPendente {

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
        ClassPathResource recurso = new ClassPathResource("static/agendamento_solicitacao.html");
        try (InputStream inputStream = recurso.getInputStream()) {
            this.templateEmail = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
     }

    public void prepararEmailPendente(String assunto, Long idAgendamento, byte[] pdfBytes, String nomeArquivo) {
        Agendamento agendamento = repositorioAgendamento.findById(idAgendamento)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado com ID: " + idAgendamento));

        enviarEmailComAnexo(
                assunto,
                agendamento.getCliente().getEmail(),
                agendamento.getCliente().getNome(),
                agendamento.getData(),
                agendamento.getHora(),
                agendamento.getObservacao(),
                agendamento.getValorRecebido(),
                pdfBytes,
                nomeArquivo);
    }

    @Async
    public void enviarEmailComAnexo(
            String assunto,
            String emailCliente,
            String nomeCliente,
            LocalDate data,
            LocalTime hora,
            String observacao,
            double valorPago,
            byte[] pdfBytes,
            String nomeArquivo) {
        try {
            MimeMessage mensagem = enviadorEmail.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensagem, true);

            helper.setFrom(emailRemetente);
            helper.setTo(emailContato);
            helper.setSubject(assunto);

            String conteudo = gerarConteudoEmail(nomeCliente, emailCliente, data, hora, observacao, valorPago);
            helper.setText(conteudo, true);

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

            enviadorEmail.send(mensagem);

        } catch (MessagingException e) {
            throw new RuntimeException("Erro ao montar ou enviar e-mail com anexo: " + e.getMessage(), e);
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
