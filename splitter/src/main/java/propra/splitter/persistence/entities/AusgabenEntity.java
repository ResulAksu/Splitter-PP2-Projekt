package propra.splitter.persistence.entities;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import org.springframework.data.annotation.Id;

public record AusgabenEntity(@Id Long id, String titel, int cents, String glaeubiger,
                             Set<AusgabenTeilnehmerEntity> teilnehmer, LocalDateTime datetime) {

  public AusgabenEntity(Long id, String titel, int cents, String glaeubiger,
      Set<AusgabenTeilnehmerEntity> teilnehmer, LocalDateTime datetime) {
    this.id = id;
    this.titel = titel;
    this.cents = cents;
    this.glaeubiger = glaeubiger;
    this.teilnehmer = Set.copyOf(teilnehmer);
    this.datetime = datetime != null ? LocalDateTime.from(datetime) : null;
  }

  public Set<AusgabenTeilnehmerEntity> teilnehmer() {
    return Set.copyOf(teilnehmer);
  }

  @Override
  public LocalDateTime datetime() {
    return LocalDateTime.from(datetime);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AusgabenEntity that = (AusgabenEntity) o;
    return Objects.equals(id, that.id) && Objects.equals(titel, that.titel)
        && cents == that.cents && Objects.equals(glaeubiger, that.glaeubiger)
        && Objects.equals(teilnehmer, that.teilnehmer) && Objects.equals(datetime,
        that.datetime);
  }

  /**
   * Wir mussten {@link java.math.BigDecimal#doubleValue()}, da die interne Representation eines
   * BigDecimals mit Wert 3.6 und 3.60 Unterschiedlich ist. Ist hier jedoch legitim, da wir nur auf
   * zwei Nachkommastellen runden.
   *
   * @return hashcode
   */
  @Override
  public int hashCode() {
    return Objects.hash(id, titel, cents, glaeubiger, teilnehmer, datetime);
  }
}

