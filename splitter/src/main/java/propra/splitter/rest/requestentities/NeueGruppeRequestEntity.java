package propra.splitter.rest.requestentities;

import java.util.Set;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public record NeueGruppeRequestEntity(@NotNull String name,
                                      @NotNull @NotEmpty Set<String> personen) {

  public NeueGruppeRequestEntity(String name, Set<String> personen) {
    this.name = name;
    this.personen = Set.copyOf(personen);
  }

  @Override
  public Set<String> personen() {
    return Set.copyOf(personen);
  }
}
