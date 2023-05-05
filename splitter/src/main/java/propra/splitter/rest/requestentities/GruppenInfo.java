package propra.splitter.rest.requestentities;

import java.util.Set;

public record GruppenInfo(String gruppe, String name, Set<String> personen,
                          boolean geschlossen, Set<Ausgabe> ausgaben) {

  public GruppenInfo(String gruppe, String name, Set<String> personen, boolean geschlossen,
      Set<Ausgabe> ausgaben) {
    this.gruppe = gruppe;
    this.name = name;
    this.personen = Set.copyOf(personen);
    this.geschlossen = geschlossen;
    this.ausgaben = Set.copyOf(ausgaben);
  }

  @Override
  public Set<String> personen() {
    return Set.copyOf(personen);
  }

  @Override
  public Set<Ausgabe> ausgaben() {
    return Set.copyOf(ausgaben);
  }
}
