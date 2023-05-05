package propra.splitter.web.displayentities;

import java.time.LocalDateTime;
import java.util.Set;

public record DisplayAusgabe(String grund, String geld, String glauebiger, Set<String> schuldner,
                             LocalDateTime datetime) {

  public DisplayAusgabe(String grund, String geld, String glauebiger, Set<String> schuldner,
      LocalDateTime datetime) {
    this.grund = grund;
    this.geld = geld;
    this.glauebiger = glauebiger;
    this.schuldner = Set.copyOf(schuldner);
    this.datetime = LocalDateTime.from(datetime);
  }

  @Override
  public Set<String> schuldner() {
    return Set.copyOf(schuldner);
  }

  @Override
  public LocalDateTime datetime() {
    return LocalDateTime.from(datetime);
  }
}
