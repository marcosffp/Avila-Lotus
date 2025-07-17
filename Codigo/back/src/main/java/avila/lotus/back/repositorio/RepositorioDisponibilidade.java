package avila.lotus.back.repositorio;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import avila.lotus.back.modelos.entidades.Disponibilidade;
import jakarta.persistence.LockModeType;

@Repository
public interface RepositorioDisponibilidade extends JpaRepository<Disponibilidade, Long> {

    // Buscar disponibilidades de um profissional por ID (somente disponíveis)
    @Query("""
             SELECT d FROM Disponibilidade d
             WHERE d.profissional.id = :profissionalId
             AND d.disponivel = true
            """)
    List<Disponibilidade> buscarPorIdProfissional(@Param("profissionalId") Long profissionalId);

    // Verificar se há sobreposição de disponibilidades para um profissional
    @Query("""
            SELECT d FROM Disponibilidade d
            WHERE d.profissional.id = :profissionalId
            AND d.data = :data
            AND (
                (:horaInicio >= d.horaInicio AND :horaInicio < d.horaFim) OR
                (:horaFim > d.horaInicio AND :horaFim <= d.horaFim) OR
                (d.horaInicio >= :horaInicio AND d.horaInicio < :horaFim)
            )
            """)
    List<Disponibilidade> verificarSobreposicaoHorarios(
            @Param("profissionalId") Long profissionalId,
            @Param("data") LocalDate data,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFim") LocalTime horaFim);

    // Retorna todas as datas disponíveis (dias e meses) para um profissional
    @Query("""
                SELECT d.data
                FROM Disponibilidade d
                WHERE d.profissional.id = :profissionalId
                AND d.disponivel = true
            """)
    List<LocalDate> buscarDatasDisponiveisPorProfissional(@Param("profissionalId") Long profissionalId);

    // Retorna os horários disponíveis para um profissional em um dia específico
    @Query("""
                SELECT d.id, d.horaInicio
                FROM Disponibilidade d
                WHERE d.profissional.id = :profissionalId
                AND d.data = :data
                AND d.disponivel = true
            """)
    List<Object[]> buscarHorariosDisponiveisPorProfissionalEData(
            @Param("profissionalId") Long profissionalId,
            @Param("data") LocalDate data);

    // Retorna uma disponibilidade específica por ID (somente disponíveis)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM Disponibilidade d WHERE d.id = :id")
    Optional<Disponibilidade> findByIdComLock(@Param("id") Long id);

}