package avila.lotus.back.util.validacao.comprovante;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import avila.lotus.back.excecao.ComprovanteException;

@Component
public class ValidacaoDadoComprovante {

  public void validarComprovantePdf(MultipartFile arquivo) {
    if (arquivo == null || arquivo.isEmpty()) {
      throw new ComprovanteException("O comprovante é obrigatório.");
    }
    if (!"application/pdf".equals(arquivo.getContentType())) {
      throw new ComprovanteException("Apenas arquivos PDF são aceitos.");
    }
  }
}
