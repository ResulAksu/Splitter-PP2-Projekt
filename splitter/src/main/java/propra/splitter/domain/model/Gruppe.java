package propra.splitter.domain.model;

import java.util.HashSet;
import java.util.Set;
import propra.splitter.domain.model.transaktionsrechner.TransaktionsRechner;

public record Gruppe(Long id, String name, Set<String> teilnehmer, Set<Ausgabe> ausgaben,
                     boolean geschlossen) {

  public Gruppe(Long id, String name, Set<String> teilnehmer, Set<Ausgabe> ausgaben,
      boolean geschlossen) {
    this.id = id;
    this.name = name;
    this.teilnehmer = new HashSet<>(teilnehmer);
    this.ausgaben = ausgaben != null ? new HashSet<>(ausgaben) : new HashSet<>();
    this.geschlossen = geschlossen;
  }

  public static Gruppe of(String gruender, String titel) {
    return new Gruppe(null, titel, Set.of(gruender), Set.of(), false);
  }

  public static Gruppe of(Set<String> teilnehmer, String titel) {
    return new Gruppe(null, titel, Set.copyOf(teilnehmer), Set.of(), false);
  }

  public Set<String> teilnehmer() {
    return Set.copyOf(teilnehmer);
  }

  public Set<Ausgabe> ausgaben() {
    return Set.copyOf(ausgaben);
  }

  public void teilnehmerHinzufuegen(String teilnehmer) {
    this.teilnehmer.add(teilnehmer);
  }

  public void ausgabeHinzufuegen(Ausgabe ausgabe) {
    this.ausgaben.add(ausgabe);
  }

  public Gruppe schliessen() {
    return new Gruppe(id, name, teilnehmer, ausgaben, true);
  }

  public boolean hatAusgaben() {
    return ausgaben.size() > 0;
  }

  public boolean hatTeilnehmer(String teilnehmer) {
    return this.teilnehmer.contains(teilnehmer);
  }

  public Set<Transaktion> berechneTransaktionen() {
    return TransaktionsRechner.rechne(ausgaben);
  }
}
