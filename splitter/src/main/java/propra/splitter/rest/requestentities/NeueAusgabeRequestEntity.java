package propra.splitter.rest.requestentities;

import java.util.Set;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public record NeueAusgabeRequestEntity(@NotNull String grund, @NotNull String glaeubiger,
                                       @NotNull String cent,
                                       @NotNull @NotEmpty Set<String> schuldner) {

  public NeueAusgabeRequestEntity(String grund, String glaeubiger, String cent,
      Set<String> schuldner) {
    this.grund = grund;
    this.glaeubiger = glaeubiger;
    this.cent = cent;
    this.schuldner = Set.copyOf(schuldner);
  }

  public int centVal() {
    return Integer.parseInt(cent);
  }

  @Override
  public Set<String> schuldner() {
    return Set.copyOf(schuldner);
  }
}
