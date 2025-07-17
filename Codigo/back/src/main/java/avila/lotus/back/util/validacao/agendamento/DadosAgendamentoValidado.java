package avila.lotus.back.util.validacao.agendamento;

import avila.lotus.back.modelos.entidades.Cliente;
import avila.lotus.back.modelos.entidades.Disponibilidade;

public record DadosAgendamentoValidado(
    Cliente cliente,
    Disponibilidade disponibilidade) {

}
