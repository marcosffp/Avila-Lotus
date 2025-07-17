package avila.lotus.back.util.consulta.disponibilidade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import avila.lotus.back.excecao.DisponibilidadeException;
import avila.lotus.back.modelos.entidades.Disponibilidade;
import avila.lotus.back.repositorio.RepositorioDisponibilidade;

@Component
public class BuscaDisponibilidade {

  @Autowired
  private RepositorioDisponibilidade repositorioDisponibilidade;

  public Disponibilidade buscarDisponibilidadePorId(long disponibilidadeId) {
    return repositorioDisponibilidade.findById(disponibilidadeId)
        .orElseThrow(
            () -> new DisponibilidadeException("Disponibilidade não encontrada com o ID: " + disponibilidadeId));
  }

}
