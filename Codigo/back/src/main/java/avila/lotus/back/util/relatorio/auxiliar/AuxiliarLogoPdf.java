package avila.lotus.back.util.relatorio.auxiliar;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;

import avila.lotus.back.excecao.RelatorioException;

import java.io.InputStream;

public class AuxiliarLogoPdf {
  public static void adicionarLogo(Document document) {
    try {
      // Carrega a imagem usando ClassLoader
      InputStream logoStream = AuxiliarLogoPdf.class.getClassLoader()
          .getResourceAsStream("static/logo_relatorio.png");

      if (logoStream != null) {
        // Lê todos os bytes de uma vez (adequado para imagens pequenas)
        byte[] imageBytes = logoStream.readAllBytes();
        ImageData imageData = ImageDataFactory.create(imageBytes);
        Image logo = new Image(imageData)
            .scaleToFit(80, 80)
            .setFixedPosition(36, 750);
        document.add(logo);
      } else {
        throw new RelatorioException("Logo não encontrado: /static/logo_relatorio.png.");
      }
    } catch (Exception e) {
      throw new RelatorioException("Erro ao adicionar logo ao PDF: " + e.getMessage());
    }
  }
}