package propra.splitter.domain.model;

public record Transaktion(String schuldner, String glaeubiger, int cents) {

  public Transaktion(String schuldner, String glaeubiger, int cents) {
    this.schuldner = schuldner;
    this.glaeubiger = glaeubiger;
    this.cents = cents;
  }
}
