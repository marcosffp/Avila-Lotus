package avila.lotus.back.repositorio;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import avila.lotus.back.modelos.entidades.Agendamento;
import avila.lotus.back.modelos.enumeradores.StatusAgendamento;

@Repository
public interface RepositorioAgendamento extends JpaRepository<Agendamento, Long> {

    // Buscar agendamentos por ID do cliente
    @Query("""
                SELECT a FROM Agendamento a
                JOIN FETCH a.disponibilidade
                WHERE a.cliente.id = :clienteId
            """)
    List<Agendamento> buscarPorIdCliente(@Param("clienteId") Long clienteId);

    // Buscar agendamentos por ID do profissional
    @Query("""
                SELECT a FROM Agendamento a
                JOIN FETCH a.disponibilidade
                JOIN FETCH a.cliente
                WHERE a.profissional.id = :profissionalId
            """)
    List<Agendamento> buscarPorIdProfissional(@Param("profissionalId") Long profissionalId);

    // Buscar agendamentos por status e ID do profissional
    @Query("""
            SELECT a FROM Agendamento a
            WHERE a.profissional.id = :profissionalId
            AND a.status = :status
            """)
    List<Agendamento> buscarPorIdProfissionalEStatus(
            @Param("profissionalId") Long profissionalId,
            @Param("status") StatusAgendamento status);

    // Buscar agendamentos por status e ID do cliente
    @Query("""
            SELECT a FROM Agendamento a
            WHERE a.cliente.id = :clienteId
            AND a.status = :status
            """)
    List<Agendamento> buscarPorIdClienteEStatus(
            @Param("clienteId") Long clienteId,
            @Param("status") StatusAgendamento status);

    // Buscar agendamentos por profissional e intervalo de datas
    @Query("""
            SELECT a FROM Agendamento a
            WHERE a.profissional.id = :profissionalId
            AND a.data BETWEEN :inicio AND :fim
            """)
    List<Agendamento> buscarPorIdProfissionalEDataEntre(
            @Param("profissionalId") Long profissionalId,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim);

    // Buscar agendamentos por cliente e intervalo de datas
    @Query("""
            SELECT a FROM Agendamento a
            WHERE a.cliente.id = :clienteId
            AND a.data BETWEEN :inicio AND :fim
            """)
    List<Agendamento> buscarPorIdClienteEDataEntre(
            @Param("clienteId") Long clienteId,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim);

    // Buscar agendamentos em uma data específica e intervalo de horários
    @Query("""
            SELECT a FROM Agendamento a
            JOIN FETCH a.profissional
            WHERE a.profissional.id = :profissionalId
            AND a.data = :data
            AND a.hora BETWEEN :horaInicio AND :horaFim
            """)
    List<Agendamento> buscarPorIdProfissionalEDataEHoraEntre(
            @Param("profissionalId") Long profissionalId,
            @Param("data") LocalDate data,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFim") LocalTime horaFim);

}
