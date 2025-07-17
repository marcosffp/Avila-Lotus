package avila.lotus.back.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import avila.lotus.back.modelos.entidades.Profissional;

@Repository
public interface RepositorioProfisisonal extends JpaRepository<Profissional, Long> {

  @Query("SELECT p FROM Profissional p WHERE p.email = :email")
  Optional<Profissional> buscarPorEmail(@Param("email") String email);

}
