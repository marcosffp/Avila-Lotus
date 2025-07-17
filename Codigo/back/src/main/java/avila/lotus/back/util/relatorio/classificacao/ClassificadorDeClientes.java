package avila.lotus.back.util.relatorio.classificacao;

public class ClassificadorDeClientes {
  private static final double LIMITE_NAO_REALIZADOS_PREMIUM = 30.0; 
  private static final double LIMITE_NAO_REALIZADOS_PROBLEMATICO = 50.0; 

  public static String classificar(double faturamentoRealizado, double naoRealizadosPercent, double p80) {
    boolean faturamentoAcimaP80 = faturamentoRealizado >= p80;

    if (faturamentoAcimaP80) {
      if (naoRealizadosPercent <= LIMITE_NAO_REALIZADOS_PREMIUM) {
        return "Premium";
      } else {
        return "Rentável/Arriscado";
      }
    } else {
      if (naoRealizadosPercent > LIMITE_NAO_REALIZADOS_PROBLEMATICO) {
        return "Problemático";
      } else {
        return "Neutro";
      }
    }
  }
}