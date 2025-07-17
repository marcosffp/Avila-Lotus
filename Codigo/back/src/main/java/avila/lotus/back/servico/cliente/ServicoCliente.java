package avila.lotus.back.servico.cliente;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import avila.lotus.back.modelos.dto.cliente.SolicitacaoAnamneseCliente;
import avila.lotus.back.modelos.dto.cliente.SolicitacaoPerfilCliente;
import avila.lotus.back.excecao.ClienteException;
import avila.lotus.back.modelos.dto.cliente.RespostaChavePixCliente;
import avila.lotus.back.modelos.dto.cliente.SolicitacaoCadastroCliente;
import avila.lotus.back.modelos.dto.cliente.RespostaCompletaCliente;
import avila.lotus.back.modelos.entidades.Cliente;
import avila.lotus.back.repositorio.RepositorioCliente;
import avila.lotus.back.servico.autenticacao.ServicoToken;


@Service
public class ServicoCliente {

  @Autowired
  private RepositorioCliente repositorioCliente;

  @Autowired
  private PasswordEncoder codificadorSenha;

  @Autowired
  private ServicoToken servicoToken;

  public String cadastrarCliente(SolicitacaoCadastroCliente SolicitacaoCadastroCliente) {

    String email = SolicitacaoCadastroCliente.email().trim().toLowerCase();

    if (repositorioCliente.buscarPorEmail(email).isPresent()) {
      throw new ClienteException("O email informado já está cadastrado.");
    }

    String encodedPassword = codificadorSenha.encode(SolicitacaoCadastroCliente.password());
    Cliente cliente = new Cliente(email, encodedPassword);

    repositorioCliente.save(cliente);
    return servicoToken.gerarToken(cliente.getId(), cliente.getEmail(), cliente.getTipo().toString());

  }

  @Transactional
  public void editarAnamnese(String token, SolicitacaoAnamneseCliente anamneseRequest) {
    String email = servicoToken.validarToken(token);
    Cliente cliente = repositorioCliente.buscarPorEmail(email)
        .orElseThrow(() -> new ClienteException("O usuário não existe"));

    cliente.setRealizouCirurgia(anamneseRequest.cirurgia());
    cliente.setFazUsoDeRemedios(anamneseRequest.remedio());
    cliente.setFazUsoDeRemedios(anamneseRequest.anticoncepcional());
    cliente.setTemAlergiaAMedicamento(anamneseRequest.alergiaMedicamento());
    cliente.setTemPressaoAlta(anamneseRequest.pressao());
    cliente.setFazTratamento(anamneseRequest.tratamento());
    cliente.setEstaGestante(anamneseRequest.gestante());
    cliente.setTemProblemasRinsFigado(anamneseRequest.problemas());
    cliente.setPessoaFumante(anamneseRequest.fumante());
    cliente.setTemHepatite(anamneseRequest.hepatite());
    cliente.setTemDiabetes(anamneseRequest.diabetes());
    cliente.setTemAsma(anamneseRequest.asma());
    cliente.setTemProblemaCardiaco(anamneseRequest.cardiaco());
    cliente.setTemConvulsao(anamneseRequest.convulsao());
    cliente.setTemProblemaRenal(anamneseRequest.renal());
    cliente.setTemTontura(anamneseRequest.tontura());
    cliente.setNomeCirurgia(anamneseRequest.nomeCirurgia());
    cliente.setNomeRemedio(anamneseRequest.nomeRemedio());
    cliente.setNomeAnticoncepcional(anamneseRequest.nomeAnticoncepcional());
    cliente.setNomeAlergiaMedicamento(anamneseRequest.nomeAlergiaMedicamento());
    cliente.setValorPressaoArterial(anamneseRequest.valorPressao());
    cliente.setNomeTratamento(anamneseRequest.nomeTratamento());
    cliente.setQueixas(anamneseRequest.queixas());

    repositorioCliente.save(cliente);
  }

  

  @Transactional
  public void editarPerfil(String token, SolicitacaoPerfilCliente SolicitacaoCadastroCliente) {
    String email = servicoToken.validarToken(token);
    Cliente cliente = repositorioCliente.buscarPorEmail(email)
        .orElseThrow(() -> new ClienteException("O usuário não existe"));
    cliente.setNome(SolicitacaoCadastroCliente.nome());
    cliente.setTelefone(SolicitacaoCadastroCliente.telefone());
    cliente.setDataDeNascimento(SolicitacaoCadastroCliente.dataNascimento());
    cliente.setCpf(SolicitacaoCadastroCliente.CPF());
    cliente.setPix(SolicitacaoCadastroCliente.PIX());
    repositorioCliente.save(cliente);
  }


  public RespostaCompletaCliente obterClienteCompleto(String email) {
    Cliente cliente = repositorioCliente.buscarPorEmail(email)
        .orElseThrow(() -> new ClienteException("Usuário não encontrado"));
    return new RespostaCompletaCliente(
        cliente.getNome(),
        cliente.getTelefone(),
        cliente.getDataDeNascimento(),
        cliente.getCpf(),
        cliente.getPix(),
        cliente.getEmail(),
        Boolean.TRUE.equals(cliente.getRealizouCirurgia()),
        Boolean.TRUE.equals(cliente.getFazUsoDeRemedios()),
        Boolean.TRUE.equals(cliente.getFazUsoDeAnticoncepcional()),
        Boolean.TRUE.equals(cliente.getTemAlergiaAMedicamento()),
        Boolean.TRUE.equals(cliente.getTemPressaoAlta()),
        Boolean.TRUE.equals(cliente.getFazTratamento()),
        Boolean.TRUE.equals(cliente.getEstaGestante()),
        Boolean.TRUE.equals(cliente.getTemProblemasRinsFigado()),
        Boolean.TRUE.equals(cliente.getPessoaFumante()),
        Boolean.TRUE.equals(cliente.getTemHepatite()),
        Boolean.TRUE.equals(cliente.getTemDiabetes()),
        Boolean.TRUE.equals(cliente.getTemAsma()),
        Boolean.TRUE.equals(cliente.getTemProblemaCardiaco()),
        Boolean.TRUE.equals(cliente.getTemConvulsao()),
        Boolean.TRUE.equals(cliente.getTemProblemaRenal()),
        Boolean.TRUE.equals(cliente.getTemTontura()),
        cliente.getNomeCirurgia(),
        cliente.getNomeRemedio(),
        cliente.getNomeAnticoncepcional(),
        cliente.getNomeAlergiaMedicamento(),
        cliente.getValorPressaoArterial(),
        cliente.getNomeTratamento(), cliente.getQueixas());
  }

  public RespostaChavePixCliente obterPixCliente(String email) {
    Cliente cliente = repositorioCliente.buscarPorEmail(email)
        .orElseThrow(() -> new ClienteException("Usuário não encontrado"));
    return new RespostaChavePixCliente(cliente.getPix());
  }

}
