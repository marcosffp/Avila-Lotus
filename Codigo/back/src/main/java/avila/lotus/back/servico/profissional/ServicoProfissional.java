package avila.lotus.back.servico.profissional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import jakarta.transaction.Transactional;
import avila.lotus.back.modelos.dto.profissional.*;
import avila.lotus.back.modelos.entidades.Profissional;
import avila.lotus.back.modelos.entidades.ServicoPrestado;
import avila.lotus.back.repositorio.RepositorioProfisisonal;
import avila.lotus.back.servico.autenticacao.ServicoToken;
import avila.lotus.back.util.consulta.profissional.BuscaProfissional;
import avila.lotus.back.util.validacao.profissional.ValidacaoDadosProfissional;

@Service
public class ServicoProfissional {

  @Autowired
  private RepositorioProfisisonal repositorioProfissional;

  @Autowired
  private PasswordEncoder codificadorSenha;

  @Autowired
  private ServicoToken servicoToken;

  @Autowired
  private BuscaProfissional buscaProfissional;

  @Autowired
  private ValidacaoDadosProfissional validacaoDadosProfissional;

  @Transactional
  public String cadastrarProfissional(SolicitacaoProfissional solicitacao) {
    String email = solicitacao.email().trim().toLowerCase();
    validacaoDadosProfissional.validarAntesCriar(email, solicitacao.codAutenticacao());
    Profissional profissional = criarNovoProfissional(solicitacao, email);
    repositorioProfissional.save(profissional);

    return servicoToken.gerarToken(
        profissional.getId(),
        profissional.getEmail(),
        profissional.getTipo().toString());
  }

@Transactional
public void atualizarServicos(String token, SolicitacaoServicosProfissional solicitacao) {
    Profissional profissional = buscaProfissional.buscarProfissionalPorToken(token);

    // Limpa os serviços anteriores
    profissional.getServicosPrestados().clear();

    // Converte os novos nomes de serviços em entidades
    List<ServicoPrestado> novosServicos = solicitacao.servicosPrestados().stream()
        .map(nome -> new ServicoPrestado(null, nome, profissional))
        .toList();

    // Adiciona à lista do profissional
    profissional.getServicosPrestados().addAll(novosServicos);

    // Persiste as alterações
    repositorioProfissional.save(profissional);
}


@Transactional
public RespostaServicosProfissional consultarServicos() {
  Profissional profissional = buscaProfissional.getProfissional();

  List<String> nomesServicos = profissional.getServicosPrestados().stream()
      .map(ServicoPrestado::getServico)
      .toList();

  return new RespostaServicosProfissional(nomesServicos);
}

  @Transactional
  public void atualizarSobreMim(String token, SolicitacaoSobreMimProfissional solicitacao) {
    Profissional profissional = buscaProfissional.buscarProfissionalPorToken(token);
    profissional.setSobreMim(solicitacao.sobreMim());
    repositorioProfissional.save(profissional);
  }

  public RespostaSobreMimProfissional consultarSobreMim() {
    Profissional profissional = buscaProfissional.getProfissional();
    return new RespostaSobreMimProfissional(profissional.getSobreMim());
  }

  @Transactional
  public void registrarChavePix(String token, SolicitacaoPixProfissional solicitacao) {
    Profissional profissional = buscaProfissional.buscarProfissionalPorToken(token);
    profissional.setPix(solicitacao.pix());
    repositorioProfissional.save(profissional);
  }

  public RespostaPixProfissional consultarChavePix() {
    Profissional profissional = buscaProfissional.getProfissional();
    return new RespostaPixProfissional(profissional.getPix());
  }

  public RespostaPixENomeProfissional consultarPixENome() {
    Profissional profissional = buscaProfissional.getProfissional();
    return new RespostaPixENomeProfissional(profissional.getNome(), profissional.getPix());
  }

  @Transactional
  public void atualizarNome(String token, SolicitacaoNomeProfissional solicitacao) {
    Profissional profissional = buscaProfissional.buscarProfissionalPorToken(token);
    profissional.setNome(solicitacao.nome());
    repositorioProfissional.save(profissional);
  }

  private Profissional criarNovoProfissional(SolicitacaoProfissional solicitacao, String email) {
    String senhaCodificada = codificadorSenha.encode(solicitacao.password());
    return new Profissional(
        email,
        senhaCodificada,
        solicitacao.codAutenticacao());
  }
}