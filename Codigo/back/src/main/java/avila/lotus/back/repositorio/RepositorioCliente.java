package avila.lotus.back.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import avila.lotus.back.modelos.entidades.Cliente;

@Repository
public interface RepositorioCliente extends JpaRepository<Cliente, Long> {

    @Query("SELECT c FROM Cliente c WHERE c.email = :email")
    Optional<Cliente> buscarPorEmail(@Param("email") String email);
}
