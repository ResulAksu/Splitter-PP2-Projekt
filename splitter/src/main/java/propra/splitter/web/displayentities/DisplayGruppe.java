package propra.splitter.web.displayentities;

import java.util.Set;

public record DisplayGruppe(long id, String name, Set<String> teilnehmer,
                            Set<DisplayAusgabe> ausgaben, boolean teilGeschlossen,
                            boolean geschlossen) {

  public DisplayGruppe(long id, String name, Set<String> teilnehmer, Set<DisplayAusgabe> ausgaben,
      boolean teilGeschlossen, boolean geschlossen) {
    this.id = id;
    this.name = name;
    this.teilnehmer = Set.copyOf(teilnehmer);
    this.ausgaben = Set.copyOf(ausgaben);
    this.teilGeschlossen = teilGeschlossen;
    this.geschlossen = geschlossen;
  }

  @Override
  public Set<String> teilnehmer() {
    return Set.copyOf(teilnehmer);
  }

  @Override
  public Set<DisplayAusgabe> ausgaben() {
    return Set.copyOf(ausgaben);
  }
}
