package avila.lotus.back.util.relatorio.estrutura;

import avila.lotus.back.modelos.entidades.Agendamento;
import avila.lotus.back.util.relatorio.gerador.GeradorDeResumoHorarios;
import avila.lotus.back.util.relatorio.gerador.GeradorDeBlocosPdf;
import avila.lotus.back.util.relatorio.gerador.GeradorDeResumoClientes;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.util.List;

public class TabelaInformacoesInteligentes {

        public static void adicionarInformacoes(Document document, List<Agendamento> agendamentos) {
                GeradorDeBlocosPdf.adicionarTitulo(document, "✅ INFORMAÇÕES INTELIGENTES");

                adicionarResumoPorCategoria(document, agendamentos);
                adicionarResumoPorHorario(document, agendamentos);
                adicionarObservacaoSeNecessario(document, agendamentos);
        }

        private static void adicionarResumoPorCategoria(Document document, List<Agendamento> agendamentos) {
                var blocos = GeradorDeResumoClientes.gerarResumoPorCategoria(agendamentos);

                adicionarGrupo(document, "🟢 Clientes Premium", blocos.premium(), ColorConstants.GREEN);
                adicionarGrupo(document, "🟡 Clientes Rentáveis/Arriscados", blocos.rentavel(), ColorConstants.ORANGE);
                adicionarGrupo(document, "🔴 Clientes Problemáticos", blocos.problematico(), ColorConstants.RED);
        }

        private static void adicionarResumoPorHorario(Document document, List<Agendamento> agendamentos) {
                var horarios = GeradorDeResumoHorarios.gerarResumoPorHorario(agendamentos);

                adicionarGrupo(document, "✅ Melhores Horários (Alta Demanda)", horarios.melhores(),
                                ColorConstants.BLUE);
                adicionarGrupo(document, "❌ Piores Horários (Alta Taxa de Cancelamento)", horarios.piores(),
                                ColorConstants.RED);
        }

        private static void adicionarGrupo(Document document, String tituloGrupo, List<String> dados,
                        com.itextpdf.kernel.colors.Color corTitulo) {
                GeradorDeBlocosPdf.adicionarSubtitulo(document, tituloGrupo, corTitulo);
                GeradorDeBlocosPdf.adicionarLinhaDivisoria(document);
                GeradorDeBlocosPdf.adicionarTabelaDeDados(document, dados);
                GeradorDeBlocosPdf.adicionarLinhaFinal(document);
        }

        private static void adicionarObservacaoSeNecessario(Document document, List<Agendamento> agendamentos) {
                var blocos = GeradorDeResumoClientes.gerarResumoPorCategoria(agendamentos);

                if (blocos.premium().isEmpty() && blocos.rentavel().isEmpty()) {
                        document.add(new Paragraph(
                                        "📄 Observação: Não há clientes classificados como Premium ou Rentáveis neste período, pois nenhum serviço foi efetivamente finalizado ou não houve faturamento suficiente para atender os critérios.")
                                        .setFontSize(10)
                                        .setItalic()
                                        .setMarginTop(5)
                                        .setMarginBottom(5));
                }
        }
}
