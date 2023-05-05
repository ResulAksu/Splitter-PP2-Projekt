package propra.splitter.domain.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public record Ausgabe(String grund, int cents, String glauebiger, Set<String> schuldner,
                      LocalDateTime datetime) {

  public Ausgabe(String grund, int cents, String glauebiger, Set<String> schuldner,
      LocalDateTime datetime) {
    this.grund = grund;
    this.cents = cents;
    this.glauebiger = glauebiger;
    this.schuldner = new HashSet<>(schuldner);
    this.datetime = LocalDateTime.from(datetime);
  }

  public Set<String> schuldner() {
    return Set.copyOf(schuldner);
  }

  @Override
  public LocalDateTime datetime() {
    return LocalDateTime.from(datetime);
  }

  public int anzahlTeilnehmer() {
    return schuldner.size();
  }
}
