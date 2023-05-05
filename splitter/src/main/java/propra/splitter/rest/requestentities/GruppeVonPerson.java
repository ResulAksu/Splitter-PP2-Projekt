package propra.splitter.rest.requestentities;

import java.util.Set;

public record GruppeVonPerson(String gruppe, String name, Set<String> personen) {

  public GruppeVonPerson(String gruppe, String name, Set<String> personen) {
    this.gruppe = gruppe;
    this.name = name;
    this.personen = Set.copyOf(personen);
  }

  @Override
  public Set<String> personen() {
    return Set.copyOf(personen);
  }
}
