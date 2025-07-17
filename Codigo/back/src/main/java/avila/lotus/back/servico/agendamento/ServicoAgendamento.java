package avila.lotus.back.servico.agendamento;

import avila.lotus.back.modelos.dto.agendamento.RespostaAgendamento;
import avila.lotus.back.modelos.dto.agendamento.RespostaAgendamentoProfissional;
import avila.lotus.back.modelos.dto.agendamento.SolicitacaoIdAgendamento;
import avila.lotus.back.modelos.entidades.Agendamento;
import avila.lotus.back.modelos.entidades.Cliente;
import avila.lotus.back.modelos.entidades.Disponibilidade;
import avila.lotus.back.modelos.entidades.Profissional;
import avila.lotus.back.modelos.enumeradores.DiaSemana;
import avila.lotus.back.modelos.enumeradores.StatusAgendamento;
import avila.lotus.back.modelos.enumeradores.SubStatus;
import avila.lotus.back.repositorio.RepositorioAgendamento;
import avila.lotus.back.repositorio.RepositorioDisponibilidade;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import avila.lotus.back.util.consulta.cliente.BuscaCliente;
import avila.lotus.back.util.consulta.profissional.BuscaProfissional;
import avila.lotus.back.util.validacao.agendamento.DadosAgendamentoValidado;
import avila.lotus.back.util.validacao.agendamento.ValidacaoDadoAgendamento;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServicoAgendamento {

        @Autowired
        private RepositorioAgendamento repositorioAgendamento;

        @Autowired
        private RepositorioDisponibilidade RepositorioDisponibilidade;

        @Autowired
        private ValidacaoDadoAgendamento validacaoDadoAgendamento;

        @Autowired
        private ServicoEmailAgendamento servicoEmailAgendamento;

        @Autowired
        private BuscaCliente buscaCliente;

        @Autowired
        private BuscaProfissional buscaProfissional;

        @Transactional
        public void criarAgendamento(String token, long disponibilidadeId, String observacao, double valorServico,
                        MultipartFile comprovantePdf) throws Exception {
                DadosAgendamentoValidado dados = validacaoDadoAgendamento.validarAntesCriacao(token,
                                disponibilidadeId,
                                comprovantePdf);
                Agendamento agendamento = new Agendamento(dados.cliente(), dados.disponibilidade().getProfissional(),
                                dados.disponibilidade(),
                                valorServico, observacao);
                repositorioAgendamento.save(agendamento);
                RepositorioDisponibilidade.save(dados.disponibilidade());
                servicoEmailAgendamento.enviarAgendamentoPendente(agendamento.getId(), comprovantePdf);

        }

        @Transactional
        public void aprovarAgendamento(SolicitacaoIdAgendamento request) {
                Agendamento agendamento = validacaoDadoAgendamento.validarAntesAprovar(request.idAgendamento());
                agendamento.aprovar();
                repositorioAgendamento.save(agendamento);
                servicoEmailAgendamento.enviarEmailAprovacao(agendamento.getId());
        }

        @Transactional
        public void recusarAgendamento(long idAgendamento, MultipartFile comprovantePdf) throws Exception {
                Agendamento agendamento = validacaoDadoAgendamento.validarAntesRecusar(idAgendamento,
                                comprovantePdf);
                agendamento.recusar();
                repositorioAgendamento.save(agendamento);
                Disponibilidade disponibilidade = agendamento.getDisponibilidade();
                RepositorioDisponibilidade.save(disponibilidade);
                servicoEmailAgendamento.enviarEmailRecusamento(idAgendamento, comprovantePdf);
        }

        @Transactional
        public void cancelarAgendamento(long idAgendamento, MultipartFile comprovantePdf, String motivo)
                        throws Exception {
                Agendamento agendamento = validacaoDadoAgendamento.validarAntesCancelar(idAgendamento, comprovantePdf,motivo);
                agendamento.cancelar();
                repositorioAgendamento.save(agendamento);
                Disponibilidade disponibilidade = agendamento.getDisponibilidade();
                RepositorioDisponibilidade.save(disponibilidade);
                servicoEmailAgendamento.enviarEmailCancelamento(idAgendamento, comprovantePdf, motivo);

        }

        @Transactional
        public void cancelarAgendamentoPeloCliente(String token,
                        SolicitacaoIdAgendamento request) {
                Agendamento agendamento = validacaoDadoAgendamento.validarAntesCancelarPeloCliente(token,
                                request.idAgendamento());
                agendamento.cancelarPeloCliente();
                repositorioAgendamento.save(agendamento);
                RepositorioDisponibilidade.save(agendamento.getDisponibilidade());
                servicoEmailAgendamento.enviarEmalCancelamentoPeloCliente(agendamento.getId());

        }

        @Transactional
        public void finalizarAgendamento(SolicitacaoIdAgendamento request) {
                Agendamento agendamento = validacaoDadoAgendamento.validarAntesFinalizar(request.idAgendamento());
                agendamento.finalizar();
                repositorioAgendamento.save(agendamento);
        }

        public void ausenteAgendamento(SolicitacaoIdAgendamento request) {
                Agendamento agendamento = validacaoDadoAgendamento
                                .validarAntesAusente(request.idAgendamento());
                agendamento.ausente();
                repositorioAgendamento.save(agendamento);
        }

        @Transactional
        public void reembolsarAgendamento(long idAgendamento, MultipartFile comprovantePdf) throws Exception {

                Agendamento agendamento = validacaoDadoAgendamento.validarAntesReembolsar(idAgendamento,
                                comprovantePdf);
                agendamento.reembolsar();
                repositorioAgendamento.save(agendamento);
                Disponibilidade disponibilidade = agendamento.getDisponibilidade();
                RepositorioDisponibilidade.save(disponibilidade);
                servicoEmailAgendamento.enviarEmailReembolso(idAgendamento, comprovantePdf);
        }

        public List<RespostaAgendamento> buscarAgendamentosPorCliente(String token) {
                Cliente cliente = buscaCliente.buscarClientePorToken(token);
                Long clienteId = cliente.getId();
                List<Agendamento> agendamentos = repositorioAgendamento.buscarPorIdCliente(clienteId);
                return agendamentos.stream()
                                .map(agendamento -> new RespostaAgendamento(
                                                agendamento.getId(),
                                                StatusAgendamento.getNome(agendamento.getStatus()),
                                                SubStatus.obterNomePorSubstatus(agendamento.getSubStatus()),
                                                agendamento.getData(),
                                                agendamento.getHora(),
                                                DiaSemana.obterNomePorDiaSemana(
                                                                agendamento.getDisponibilidade().getDiaSemana()),
                                                agendamento.getObservacao(),
                                                agendamento.getValorRecebido() * 2))
                                .collect(Collectors.toList());
        }

        public List<RespostaAgendamentoProfissional> buscarAgendamentosPorProfissional(String token) {
                Profissional profissional = buscaProfissional.buscarProfissionalPorToken(token);
                Long profissionalId = profissional.getId();
                List<Agendamento> agendamentos = repositorioAgendamento.buscarPorIdProfissional(profissionalId);
                return agendamentos.stream()
                                .map(agendamento -> new RespostaAgendamentoProfissional(
                                                agendamento.getId(),
                                                agendamento.getCliente().getNome(),
                                                agendamento.getCliente().getEmail(),
                                                StatusAgendamento.getNome(agendamento.getStatus()),
                                                SubStatus.obterNomePorSubstatus(agendamento.getSubStatus()),
                                                agendamento.getData(),
                                                agendamento.getHora(),
                                                DiaSemana.obterNomePorDiaSemana(
                                                                agendamento.getDisponibilidade().getDiaSemana()),
                                                agendamento.getObservacao()))
                                .collect(Collectors.toList());
        }

}
