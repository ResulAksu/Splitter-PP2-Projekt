package propra.splitter.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static propra.splitter.persistence.Adapter.gruppeToDomain;
import static propra.splitter.persistence.Adapter.gruppeToPersistence;

import java.time.LocalDateTime;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import propra.splitter.domain.model.Ausgabe;
import propra.splitter.domain.model.Gruppe;
import propra.splitter.persistence.entities.AusgabenEntity;
import propra.splitter.persistence.entities.AusgabenTeilnehmerEntity;
import propra.splitter.persistence.entities.GruppenEntity;
import propra.splitter.persistence.entities.GruppenTeilnehmerEntity;

@DisplayName("Der Transformer formt ")
public class AdapterTest {

  private final Gruppe Gruppe_WG;
  private final GruppenEntity GROUP_ENTITY_WG;

  {
    AusgabenTeilnehmerEntity at_alex = new AusgabenTeilnehmerEntity("Alex");
    AusgabenTeilnehmerEntity at_jens = new AusgabenTeilnehmerEntity("Jens");
    AusgabenTeilnehmerEntity at_leonie = new AusgabenTeilnehmerEntity("Leonie");
    GruppenTeilnehmerEntity gt_alex = new GruppenTeilnehmerEntity("Alex");
    GruppenTeilnehmerEntity gt_jens = new GruppenTeilnehmerEntity("Jens");
    GruppenTeilnehmerEntity gt_leonie = new GruppenTeilnehmerEntity("Leonie");
    LocalDateTime datetime = LocalDateTime.of(2001, 5, 7, 12, 0);

    Ausgabe Ausgabe_EIS =
        new Ausgabe("Eis", 360, "Alex", Set.of("Alex", "Jens", "Leonie"), datetime);
    AusgabenEntity EXPENSE_ENTITY_EIS =
        new AusgabenEntity(null, "Eis", 360, "Alex", Set.of(at_alex, at_jens, at_leonie), datetime);
    
    Gruppe_WG = new Gruppe(1L, "WG", Set.of("Alex", "Jens", "Leonie"), Set.of(Ausgabe_EIS), false);
    GROUP_ENTITY_WG =
        new GruppenEntity(1L, "WG", Set.of(gt_alex, gt_jens, gt_leonie), Set.of(EXPENSE_ENTITY_EIS),
            false);
  }


  @Test
  @DisplayName("eine Gruppen Entität (inkl. Ausgaben) in eine Domänen-Gruppe um")
  void test_01() {
    //Act
    Gruppe gruppe = gruppeToDomain(GROUP_ENTITY_WG);

    //Assert
    assertThat(gruppe).isEqualTo(Gruppe_WG);
  }

  @Test
  @DisplayName("eine Domänen-Gruppe (inkl. Ausgaben) in eine Gruppen Entität um")
  void test_03() {
    //Act
    GruppenEntity gruppenEntity = gruppeToPersistence(Gruppe_WG);

    //Assert
    assertThat(gruppenEntity).isEqualTo(GROUP_ENTITY_WG);
  }
}