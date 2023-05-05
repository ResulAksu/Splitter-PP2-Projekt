package propra.splitter.persistence.entities;

import java.util.Set;
import org.springframework.data.annotation.Id;

public record GruppenEntity(@Id Long id, String titel, Set<GruppenTeilnehmerEntity> teilnehmer,
                            Set<AusgabenEntity> ausgaben, boolean geschlossen) {

  public GruppenEntity(Long id, String titel, Set<GruppenTeilnehmerEntity> teilnehmer,
      Set<AusgabenEntity> ausgaben, boolean geschlossen) {
    this.id = id;
    this.titel = titel;
    this.teilnehmer = Set.copyOf(teilnehmer);
    this.ausgaben = ausgaben != null ? Set.copyOf(ausgaben) : Set.of();
    this.geschlossen = geschlossen;
  }

  public Set<AusgabenEntity> ausgaben() {
    return Set.copyOf(ausgaben);
  }

  public Set<GruppenTeilnehmerEntity> teilnehmer() {
    return Set.copyOf(teilnehmer);
  }

  public void addParticipant(GruppenTeilnehmerEntity participant) {
    teilnehmer.add(participant);
  }

  public void addExpense(AusgabenEntity expense) {
    ausgaben.add(expense);
  }

  public GruppenEntity close() {
    return geschlossen ? this : new GruppenEntity(id, titel, teilnehmer, ausgaben, true);
  }
}
