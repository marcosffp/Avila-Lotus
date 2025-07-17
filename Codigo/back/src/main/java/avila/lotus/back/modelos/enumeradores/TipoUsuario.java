package avila.lotus.back.modelos.enumeradores;

public enum TipoUsuario {
  CLIENTE, PROFISSIONAL;

  public static TipoUsuario fromString(String tipo) {
    if (tipo == null || tipo.trim().isEmpty()) {
      throw new IllegalArgumentException("Tipo de usuário não pode ser nulo ou vazio.");
    }
    return TipoUsuario.valueOf(tipo.trim().toUpperCase());
  }

  @Override
  public String toString() {
    return name(); 
  }
}
