package propra.splitter.rest.requestentities;

import java.util.Set;

public record Ausgabe(String grund, String glaeubiger, int cent, Set<String> schuldner) {

  public Ausgabe(String grund, String glaeubiger, int cent, Set<String> schuldner) {
    this.grund = grund;
    this.glaeubiger = glaeubiger;
    this.cent = cent;
    this.schuldner = Set.copyOf(schuldner);
  }

  @Override
  public Set<String> schuldner() {
    return Set.copyOf(schuldner);
  }
}
