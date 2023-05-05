package propra.splitter.domain.model.transaktionsrechner;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("The calculator calculates ")
class SubsetCalculatorTest {


  @Test
  @DisplayName("one subset including all liabilities.")
  void test_01() {
    // Arrange
    Set<Liability> liabilities =
        Set.of(new Liability("A", money(3000)), new Liability("B", money(3000)),
            new Liability("C", money(3000)));
    MoneyWrapper money = money(9000);

    // Act
    Set<Liability> smallest = SubsetCalculator.smallestSubsetThatEquals(liabilities, money);

    // Assert
    assertThat(smallest).hasSize(3);
    assertThat(smallest).isEqualTo(liabilities);
  }

  @Test
  @DisplayName("one subset including a part of the liabilities.")
  void test_02() {
    // Arrange
    Set<Liability> liabilities =
        Set.of(new Liability("A", money(2000)), new Liability("B", money(3000)),
            new Liability("C", money(1000)));
    MoneyWrapper money = money(4000);

    // Act
    Set<Liability> smallest = SubsetCalculator.smallestSubsetThatEquals(liabilities, money);

    // Assert
    Set<Liability> expected =
        Set.of(new Liability("B", money(3000)), new Liability("C", money(1000)));

    assertThat(smallest).hasSize(2);
    assertThat(smallest).isEqualTo(expected);
  }

  @Test
  @DisplayName("the smallest subset with multiple different-sized possibilities.")
  void test_03() {
    // Arrange
    Set<Liability> liabilities =
        Set.of(new Liability("A", money(2000)), new Liability("B", money(3000)),
            new Liability("C", money(1000)), new Liability("D", money(1000)));
    MoneyWrapper money = money(5000);

    // Act
    Set<Liability> smallest = SubsetCalculator.smallestSubsetThatEquals(liabilities, money);

    // Assert
    Set<Liability> expected =
        Set.of(new Liability("A", money(2000)), new Liability("B", money(3000)));

    assertThat(smallest).hasSize(2);
    assertThat(smallest).isEqualTo(expected);
  }

  @Test
  @DisplayName("the smallest subset with multiple same-sized possibilities.")
  void test_04() {
    // Arrange
    Set<Liability> liabilities =
        Set.of(new Liability("A", money(2000)), new Liability("B", money(3000)),
            new Liability("C", money(1000)), new Liability("D", money(4000)));
    MoneyWrapper money = money(5000);

    // Act
    Set<Liability> smallest = SubsetCalculator.smallestSubsetThatEquals(liabilities, money);

    // Assert
    Set<Liability> expected1 =
        Set.of(new Liability("A", money(2000)), new Liability("B", money(3000)));
    Set<Liability> expected2 =
        Set.of(new Liability("C", money(1000)), new Liability("D", money(4000)));

    assertThat(smallest).hasSize(2);
    assertThat(Set.of(expected1, expected2)).contains(smallest);
  }

  @Test
  @DisplayName("a perfect match.")
  void test_05() {
    // Arrange
    Set<Liability> liabilities =
        Set.of(new Liability("A", money(2000)), new Liability("B", money(2000)),
            new Liability("C", money(4000)));
    MoneyWrapper money = money(4000);

    // Act
    Set<Liability> smallest = SubsetCalculator.smallestSubsetThatEquals(liabilities, money);

    // Assert
    Set<Liability> expected = Set.of(new Liability("C", money(4000)));

    assertThat(smallest).hasSize(1);
    assertThat(smallest).isEqualTo(expected);
  }

  private MoneyWrapper money(int val) {
    return MoneyWrapper.ofCents(val);
  }
}