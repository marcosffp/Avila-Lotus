package avila.lotus.back.servico.disponibilidade;

import avila.lotus.back.excecao.DisponibilidadeException;
import avila.lotus.back.modelos.dto.disponibilidade.SolicitacaoDisponibilidade;
import avila.lotus.back.modelos.entidades.Disponibilidade;
import avila.lotus.back.modelos.entidades.Profissional;
import avila.lotus.back.repositorio.RepositorioDisponibilidade;

import avila.lotus.back.util.consulta.profissional.BuscaProfissional;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Objects;

@Service
public class ServicoDisponibilidade {

        @Autowired
        private RepositorioDisponibilidade disponibilidadeRepositorioProfisisonal;

        @Autowired
        private BuscaProfissional buscaProfissional;

        @Cacheable(value = "disponibilidades", key = "#profissionalId")
        public List<Disponibilidade> buscarDisponibilidadesPorProfissional(Long profissionalId) {
                return disponibilidadeRepositorioProfisisonal.buscarPorIdProfissional(profissionalId);
        }

        @CacheEvict(value = "disponibilidades", key = "#profissionalId")
        public void atualizarDisponibilidade(Long profissionalId, Disponibilidade disponibilidade) {
                disponibilidadeRepositorioProfisisonal.save(disponibilidade);
        }

        @Transactional
        public void cadastrarDisponibilidade(SolicitacaoDisponibilidade request) {
                Profissional profissional = getProfissional();

                List<Disponibilidade> sobrepostas = disponibilidadeRepositorioProfisisonal
                                .verificarSobreposicaoHorarios(profissional.getId(), request.data(),
                                                request.horaInicio(),
                                                request.horaInicio().plusHours(
                                                                1));

                if (!sobrepostas.isEmpty()) {
                        throw new DisponibilidadeException(
                                        "O horário informado se sobrepõe a uma disponibilidade existente.");
                }

                Disponibilidade disponibilidade = new Disponibilidade(
                                profissional, request.data(), request.horaInicio());

                disponibilidadeRepositorioProfisisonal.save(disponibilidade);
        }

        @Transactional
        public void excluirDisponibilidade(Long id) {
                Disponibilidade disponibilidade = disponibilidadeRepositorioProfisisonal.findById(id)
                                .orElseThrow(() -> new DisponibilidadeException(
                                                "Disponibilidade não encontrada"));
                disponibilidadeRepositorioProfisisonal.delete(disponibilidade);
        }

        public Profissional getProfissional() {
                return buscaProfissional.getProfissional();
        }

        public List<String> getDiasDisponiveis() {
                int anoAtual = LocalDate.now().getYear();

                return disponibilidadeRepositorioProfisisonal
                                .buscarDatasDisponiveisPorProfissional(getProfissional().getId())
                                .stream()
                                .filter(data -> data.getYear() == anoAtual)

                                .map(data -> data.getDayOfMonth() + "-" + data.getMonthValue())

                                .distinct()
                                .collect(Collectors.toList());
        }

        public List<Map<String, Object>> getHorariosDisponiveis(String diaMes) {
                if (diaMes == null || !diaMes.matches("\\d{1,2}-\\d{1,2}")) {
                        throw new IllegalArgumentException(
                                        "Formato inválido para diaMes. Esperado: 'dia-mês' (ex: 5-3)");
                }

                String[] partes = diaMes.split("-");
                int dia = Integer.parseInt(partes[0]);
                int mes = Integer.parseInt(partes[1]);
                int anoAtual = LocalDate.now().getYear();
                Optional<LocalDate> dataCorrespondente = disponibilidadeRepositorioProfisisonal
                                .buscarDatasDisponiveisPorProfissional(getProfissional().getId())
                                .stream()
                                .filter(data -> data.getDayOfMonth() == dia && data.getMonthValue() == mes
                                                && data.getYear() == anoAtual)
                                .findFirst();

                if (dataCorrespondente.isEmpty()) {
                        return Collections.emptyList();
                }

                return disponibilidadeRepositorioProfisisonal
                                .buscarHorariosDisponiveisPorProfissionalEData(getProfissional().getId(),
                                                dataCorrespondente.get())
                                .stream()
                                .map(result -> {
                                        if (result.length < 2 || result[0] == null || result[1] == null) {
                                                return null;
                                        }
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("id", result[0] instanceof Number ? ((Number) result[0]).longValue()
                                                        : result[0]);
                                        map.put("horaInicio", result[1].toString());
                                        return map;
                                })
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList());
        }

}