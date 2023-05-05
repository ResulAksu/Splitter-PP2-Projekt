package propra.splitter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.test.context.jdbc.Sql;
import propra.splitter.application.GruppenConnection;
import propra.splitter.domain.model.Ausgabe;
import propra.splitter.domain.model.Gruppe;

@DataJdbcTest
@DisplayName("Die Gruppen-Connection ")
public class GruppenConnectionTest {

  @Autowired
  private GruppenRepository repo;

  private GruppenConnection connection;


  LocalDateTime dt = LocalDateTime.of(2003, 2, 8, 11, 38, 56);
  Set<Ausgabe> ausgaben_big_gruppe =
      Set.of(new Ausgabe("Flug", 100000, "Alex", Set.of("Jens", "Leonie", "Markus", "Kristin"), dt),
          new Ausgabe("Bootstour", 8000, "Jens", Set.of("Jens", "Alex"), dt),
          new Ausgabe("Restaurant", 12099, "Leonie", Set.of("Kristin", "Alex"), dt));

  Gruppe solo_gruppe = new Gruppe(1L, "Urlaub", Set.of("Alex"), Set.of(), false);
  Gruppe small_gruppe = new Gruppe(1L, "Urlaub", Set.of("Alex", "Jens"), Set.of(), false);
  Gruppe big_gruppe =
      new Gruppe(1L, "Urlaub", Set.of("Alex", "Jens", "Leonie", "Markus", "Kristin"),
          ausgaben_big_gruppe, false);
  Set<Gruppe> multi_gruppen = Set.of(new Gruppe(1L, "Urlaub", Set.of("Alex"), Set.of(), false),
      new Gruppe(2L, "Ski Money-Pool", Set.of("Alex"), Set.of(), false),
      new Gruppe(4L, "Seafood-Hater", Set.of("Jens", "Alex"), Set.of(), false),
      new Gruppe(5L, "MacBook", Set.of("Leonie", "Alex"), Set.of(), false));

  @BeforeEach
  void setUp() {
    connection = new GruppenConnectionImpl(repo);
  }

  @Test
  @Sql("solo_gruppe.sql")
  @DisplayName("holt eine Gruppe per Teilnehmer mit nur einer Person")
  void test_01() {
    // Act
    Set<Gruppe> gruppen = this.connection.getGruppen("Alex");

    // Assert
    assertThat(gruppen).hasSize(1);
    assertThat(gruppen).containsExactly(solo_gruppe);
  }

  @Test
  @Sql("small_gruppe.sql")
  @DisplayName("holt eine Gruppe per Teilnehmer mit mehreren Personen")
  void test_02() {
    // Act
    Set<Gruppe> gruppen = this.connection.getGruppen("Alex");

    // Assert
    assertThat(gruppen).hasSize(1);
    assertThat(gruppen).containsExactly(small_gruppe);
  }

  @Test
  @Sql({"big_gruppe.sql", "ausgaben_big_gruppe.sql"})
  @DisplayName("holt eine Gruppe per Teilnehmer mit mehreren Personen und Ausgaben")
  void test_03() {
    // Act
    Set<Gruppe> gruppen = this.connection.getGruppen("Alex");

    // Assert
    assertThat(gruppen).hasSize(1);
    assertThat(gruppen).containsExactly(big_gruppe);
  }

  @Test
  @Sql("multi_gruppen.sql")
  @DisplayName("holt mehrere Gruppen per Teilnehmer")
  void test_04() {
    // Act
    Set<Gruppe> gruppen = this.connection.getGruppen("Alex");

    // Assert
    assertThat(gruppen).hasSize(4);
    assertThat(gruppen).isEqualTo(multi_gruppen);
  }
}
