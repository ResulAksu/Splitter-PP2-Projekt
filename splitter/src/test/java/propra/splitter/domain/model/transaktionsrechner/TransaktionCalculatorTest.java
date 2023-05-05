package propra.splitter.domain.model.transaktionsrechner;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import propra.splitter.domain.model.Ausgabe;
import propra.splitter.domain.model.Transaktion;

@DisplayName("The calculator, ")
class TransaktionCalculatorTest {

  private final LocalDateTime dt = LocalDateTime.of(2003, 8, 2, 11, 32);

  @Test
  @DisplayName("calculates the liabilities with ausgaben including oneself correctly.")
  void test_01() {
    // Arrange
    Ausgabe e1 = new Ausgabe(null, 6000, "1", Set.of("1", "2", "3"), dt);
    Ausgabe e2 = new Ausgabe(null, 2000, "1", Set.of("1", "3"), dt);
    Ausgabe e3 = new Ausgabe(null, 1000, "1", Set.of("1", "2"), dt);

    // Act
    Set<Liability> liabilities = TransaktionsRechner.liabilities(Set.of(e1, e2, e3));

    // Assert
    Liability l1 = new Liability("1", money(5500));
    Liability l2 = new Liability("2", money(-2500));
    Liability l3 = new Liability("3", money(-3000));
    Set<Liability> expected = Set.of(l1, l2, l3);

    assertThat(liabilities).isEqualTo(expected);
  }

  @Test
  @DisplayName("calculates the liabilities with ausgaben excluding oneself correctly.")
  void test_02() {
    // Arrange
    Ausgabe e1 = new Ausgabe(null, 6000, "1", Set.of("2", "3"), dt);
    Ausgabe e2 = new Ausgabe(null, 2000, "1", Set.of("3"), dt);
    Ausgabe e3 = new Ausgabe(null, 1000, "1", Set.of("2"), dt);

    // Act
    Set<Liability> liabilities = TransaktionsRechner.liabilities(Set.of(e1, e2, e3));

    // Assert
    Liability l1 = new Liability("1", money(9000));
    Liability l2 = new Liability("2", money(-4000));
    Liability l3 = new Liability("3", money(-5000));
    Set<Liability> expected = Set.of(l1, l2, l3);

    assertThat(liabilities).isEqualTo(expected);
  }

  @Test
  @DisplayName("calculates the liabilities with ausgaben with mixed-cluding :) oneself correctly.")
  void test_03() {
    // Arrange
    Ausgabe e1 = new Ausgabe(null, 6000, "1", Set.of("1", "2", "3"), dt);
    Ausgabe e2 = new Ausgabe(null, 2000, "1", Set.of("3"), dt);
    Ausgabe e3 = new Ausgabe(null, 1000, "1", Set.of("1", "2"), dt);
    Ausgabe e4 = new Ausgabe(null, 1000, "1", Set.of("3", "2"), dt);

    // Act
    Set<Liability> liabilities = TransaktionsRechner.liabilities(Set.of(e1, e2, e3, e4));

    // Assert
    Liability l1 = new Liability("1", money(7500));
    Liability l2 = new Liability("2", money(-3000));
    Liability l3 = new Liability("3", money(-4500));
    Set<Liability> expected = Set.of(l1, l2, l3);

    assertThat(liabilities).isEqualTo(expected);
  }

  @Test
  @DisplayName("calculates the liabilities with ausgaben with mixed debtees correctly.")
  void test_04() {
    // Arrange
    Ausgabe e1 = new Ausgabe(null, 6000, "1", Set.of("1", "2", "3"), dt);
    Ausgabe e2 = new Ausgabe(null, 2000, "2", Set.of("3"), dt);
    Ausgabe e3 = new Ausgabe(null, 1000, "2", Set.of("1", "2"), dt);
    Ausgabe e4 = new Ausgabe(null, 1000, "3", Set.of("3", "2"), dt);

    // Act
    Set<Liability> liabilities = TransaktionsRechner.liabilities(Set.of(e1, e2, e3, e4));

    // Assert
    Liability l1 = new Liability("1", money(3500));
    Liability l2 = new Liability("2", money(0));
    Liability l3 = new Liability("3", money(-3500));
    Set<Liability> expected = Set.of(l1, l2, l3);

    assertThat(liabilities).isEqualTo(expected);
  }

  @Test
  @DisplayName("deals with scenario 1 correctly")
  void test_05() {
    // Arrange
    Ausgabe e1 = new Ausgabe(null, 1000, "A", Set.of("A", "B"), dt);
    Ausgabe e2 = new Ausgabe(null, 2000, "A", Set.of("A", "B"), dt);

    // Act
    Set<Transaktion> transaktionen = TransaktionsRechner.rechne(Set.of(e1, e2));

    // Assert
    Set<Transaktion> expected = Set.of(new Transaktion("B", "A", 1500));

    assertThat(transaktionen).hasSize(1);
    assertThat(transaktionen).isEqualTo(expected);
  }

  @Test
  @DisplayName("deals with scenario 2 correctly")
  void test_06() {
    // Arrange
    Ausgabe e1 = new Ausgabe(null, 1000, "A", Set.of("A", "B"), dt);
    Ausgabe e2 = new Ausgabe(null, 2000, "B", Set.of("A", "B"), dt);

    // Act
    Set<Transaktion> transaktionen = TransaktionsRechner.rechne(Set.of(e1, e2));

    // Assert
    Set<Transaktion> expected = Set.of(new Transaktion("A", "B", 500));

    assertThat(transaktionen).hasSize(1);
    assertThat(transaktionen).isEqualTo(expected);
  }

  @Test
  @DisplayName("deals with scenario 3 correctly")
  void test_07() {
    // Arrange
    Ausgabe e1 = new Ausgabe(null, 1000, "A", Set.of("B"), dt);
    Ausgabe e2 = new Ausgabe(null, 2000, "A", Set.of("A", "B"), dt);

    // Act
    Set<Transaktion> transaktionen = TransaktionsRechner.rechne(Set.of(e1, e2));

    // Assert
    Set<Transaktion> expected = Set.of(new Transaktion("B", "A", 2000));

    assertThat(transaktionen).hasSize(1);
    assertThat(transaktionen).isEqualTo(expected);
  }

  @Test
  @DisplayName("deals with scenario 4 correctly")
  void test_08() {
    // Arrange
    Ausgabe e1 = new Ausgabe(null, 1000, "A", Set.of("A", "B"), dt);
    Ausgabe e2 = new Ausgabe(null, 1000, "B", Set.of("B", "C"), dt);
    Ausgabe e3 = new Ausgabe(null, 1000, "C", Set.of("C", "A"), dt);

    // Act
    Set<Transaktion> transaktionen = TransaktionsRechner.rechne(Set.of(e1, e2, e3));

    // Assert
    Set<Transaktion> expected = Set.of();

    assertThat(transaktionen).hasSize(0);
    assertThat(transaktionen).isEqualTo(expected);
  }

  @Test
  @DisplayName("deals with scenario 5 correctly")
  void test_09() {
    // Arrange
    Ausgabe e1 = new Ausgabe(null, 6000, "A", Set.of("A", "B", "C"), dt);
    Ausgabe e2 = new Ausgabe(null, 3000, "B", Set.of("A", "B", "C"), dt);
    Ausgabe e3 = new Ausgabe(null, 10000, "C", Set.of("B", "C"), dt);

    // Act
    Set<Transaktion> transaktionen = TransaktionsRechner.rechne(Set.of(e1, e2, e3));

    // Assert
    Transaktion t1 = new Transaktion("B", "A", 3000);
    Transaktion t2 = new Transaktion("B", "C", 2000);
    Set<Transaktion> expected = Set.of(t1, t2);

    assertThat(transaktionen).hasSize(2);
    assertThat(transaktionen).isEqualTo(expected);
  }

  @Test
  @DisplayName("deals with scenario 6 correctly")
  void test_10() {
    // Arrange
    Ausgabe e1 = new Ausgabe(null, 56400, "A", Set.of("A", "B", "C", "D", "E", "F"), dt);
    Ausgabe e2 = new Ausgabe(null, 3858, "B", Set.of("A", "B"), dt);
    Ausgabe e3 = new Ausgabe(null, 3858, "B", Set.of("A", "B", "D"), dt);
    Ausgabe e4 = new Ausgabe(null, 8211, "C", Set.of("C", "E", "F"), dt);
    Ausgabe e5 = new Ausgabe(null, 9600, "D", Set.of("A", "B", "C", "D", "E", "F"), dt);
    Ausgabe e6 = new Ausgabe(null, 9537, "F", Set.of("B", "E", "F"), dt);

    // Act
    Set<Transaktion> transaktionen =
        TransaktionsRechner.rechne(Set.of(e1, e2, e3, e4, e5, e6));

    // Assert
    Transaktion t1 = new Transaktion("B", "A", 9678);
    Transaktion t2 = new Transaktion("C", "A", 5526);
    Transaktion t3 = new Transaktion("D", "A", 2686);
    Transaktion t4 = new Transaktion("E", "A", 16916);
    Transaktion t5 = new Transaktion("F", "A", 7379);
    Set<Transaktion> expected = Set.of(t1, t2, t3, t4, t5);

    assertThat(transaktionen).hasSize(5);
    assertThat(transaktionen).isEqualTo(expected);
  }

  @Test
  @DisplayName("deals with scenario 7 correctly")
  void test_12() {
    // Arrange
    Ausgabe e1 = new Ausgabe(null, 2000, "D", Set.of("D", "F"), dt);
    Ausgabe e2 = new Ausgabe(null, 1000, "G", Set.of("B"), dt);
    Ausgabe e3 = new Ausgabe(null, 7500, "E", Set.of("A", "C", "E"), dt);
    Ausgabe e4 = new Ausgabe(null, 5000, "F", Set.of("A", "F"), dt);
    Ausgabe e5 = new Ausgabe(null, 4000, "E", Set.of("D"), dt);
    Ausgabe e6 = new Ausgabe(null, 4000, "F", Set.of("B", "F"), dt);
    Ausgabe e7 = new Ausgabe(null, 500, "F", Set.of("C"), dt);
    Ausgabe e8 = new Ausgabe(null, 3000, "G", Set.of("A"), dt);

    // Act
    Set<Transaktion> transaktionen =
        TransaktionsRechner.rechne(Set.of(e1, e2, e3, e4, e5, e6, e7, e8));

    // Assert
    Transaktion t1 = new Transaktion("A", "F", 4000);
    Transaktion t2 = new Transaktion("A", "G", 4000);
    Transaktion t3 = new Transaktion("B", "E", 3000);
    Transaktion t4 = new Transaktion("C", "E", 3000);
    Transaktion t5 = new Transaktion("D", "E", 3000);
    Set<Transaktion> expected = Set.of(t1, t2, t3, t4, t5);

    assertThat(transaktionen).hasSize(5);
    assertThat(transaktionen).isEqualTo(expected);
  }

  @Test
  @DisplayName("calculates transaction correctly, when no subset fits and the bigger set of "
      + "liabilities has a smaller max.")
  void test_13() {
    // Arrange
    Ausgabe e1 = new Ausgabe(null, 600, "C", Set.of("A"), dt);
    Ausgabe e2 = new Ausgabe(null, 300, "D", Set.of("A"), dt);
    Ausgabe e3 = new Ausgabe(null, 300, "D", Set.of("B"), dt);
    Ausgabe e4 = new Ausgabe(null, 400, "E", Set.of("B"), dt);

    // Act
    Set<Transaktion> transaktionen = TransaktionsRechner.rechne(Set.of(e1, e2, e3, e4));

    // Assert
    Transaktion t11 = new Transaktion("B", "C", 600);
    Transaktion t12 = new Transaktion("B", "D", 100);
    Transaktion t13 = new Transaktion("A", "D", 500);
    Transaktion t4 = new Transaktion("A", "E", 400);
    Set<Transaktion> expectedOption1 = Set.of(t11, t12, t13, t4);

    Transaktion t21 = new Transaktion("B", "D", 600);
    Transaktion t22 = new Transaktion("B", "C", 100);
    Transaktion t23 = new Transaktion("A", "C", 500);
    Set<Transaktion> expectedOption2 = Set.of(t21, t22, t23, t4);

    Set<Set<Transaktion>> expected = Set.of(expectedOption1, expectedOption2);

    assertThat(transaktionen).hasSize(4);
    assertThat(transaktionen).isIn(expected);
  }

  @Test
  @DisplayName("deals with decimals correctly.")
  void test_14() {
    // Arrange
    Ausgabe e1 = new Ausgabe(null, 10000, "C", Set.of("A", "B", "C"), dt);

    // Act
    Set<Transaktion> transaktionen = TransaktionsRechner.rechne(Set.of(e1));

    // Assert
    Transaktion t11 = new Transaktion("A", "C", 3333);
    Transaktion t12 = new Transaktion("B", "C", 3334);
    Set<Transaktion> expectedOption1 = Set.of(t11, t12);

    Transaktion t21 = new Transaktion("A", "C", 3334);
    Transaktion t22 = new Transaktion("B", "C", 3333);
    Set<Transaktion> expectedOption2 = Set.of(t21, t22);

    Transaktion t31 = new Transaktion("A", "C", 3333);
    Transaktion t32 = new Transaktion("B", "C", 3333);
    Set<Transaktion> expectedOption3 = Set.of(t31, t32);

    Set<Set<Transaktion>> expected = Set.of(expectedOption1, expectedOption2, expectedOption3);

    assertThat(transaktionen).hasSize(2);
    assertThat(transaktionen).isIn(expected);
  }

  private MoneyWrapper money(int num) {
    return MoneyWrapper.ofCents(num);
  }
}