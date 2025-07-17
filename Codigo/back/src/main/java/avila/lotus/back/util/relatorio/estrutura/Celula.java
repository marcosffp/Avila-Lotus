package avila.lotus.back.util.relatorio.estrutura;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;

public class Celula {

  public static Cell header(String text) {
    return baseCell(sanitize(text), true, null, null);
  }

  public static Cell normal(String text) {
    return baseCell(sanitize(text), false, null, null);
  }

  public static Cell normal(String text, DeviceRgb bg) {
    return baseCell(sanitize(text), false, null, bg);
  }

  public static Cell colored(String text, DeviceRgb fontColor) {
    return baseCell(sanitize(text), false, fontColor, null);
  }

  public static Cell colored(String text, DeviceRgb fontColor, DeviceRgb bgColor) {
    return baseCell(sanitize(text), false, fontColor, bgColor);
  }

  private static Cell baseCell(String text, boolean bold, DeviceRgb fontColor, DeviceRgb bgColor) {
    Paragraph p = new Paragraph(text).setFontSize(9);

    if (bold)
      p.setFontColor(ColorConstants.DARK_GRAY);
    if (fontColor != null)
      p.setFontColor(fontColor);

    Cell cell = new Cell().add(p)
        .setPaddingTop(2)
        .setPaddingBottom(2)
        .setPaddingLeft(5)
        .setPaddingRight(5)
        .setBorder(new SolidBorder(0.5f));

    if (bgColor != null)
      cell.setBackgroundColor(bgColor);
    return cell;
  }

  private static String sanitize(String texto) {
    return texto == null ? "" : texto;
  }
}
