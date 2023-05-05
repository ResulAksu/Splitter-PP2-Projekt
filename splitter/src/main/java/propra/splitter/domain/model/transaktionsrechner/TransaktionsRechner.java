package propra.splitter.domain.model.transaktionsrechner;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import propra.splitter.domain.model.Ausgabe;
import propra.splitter.domain.model.Transaktion;

public class TransaktionsRechner {

  public static Set<Transaktion> rechne(Set<Ausgabe> expenses) {
    Set<Liability> liabilities = liabilities(expenses);
    return runAlgorithm(liabilities);
  }

  public static Set<Liability> liabilities(Set<Ausgabe> expenses) {
    Map<String, MoneyWrapper> liabilities = new HashMap<>();
    for (Ausgabe ausgabe : expenses) {
      MoneyWrapper share =
          MoneyWrapper.ofCents(ausgabe.cents()).divide(ausgabe.anzahlTeilnehmer());
      for (String participant : ausgabe.schuldner()) {
        liabilities.compute(participant, (p, m) -> m == null ? share.negate() : m.subtract(share));
      }
      liabilities.compute(ausgabe.glauebiger(),
          (p, m) -> m == null ? MoneyWrapper.ofCents(ausgabe.cents())
              : m.addCents(ausgabe.cents()));
    }
    return Liability.mapToSet(liabilities);
  }

  private static Set<Transaktion> runAlgorithm(Set<Liability> liabilities) {
    Map<Boolean, List<Liability>> temp = liabilities.stream().filter(e -> !e.money().isZero())
        .collect(Collectors.partitioningBy(e -> e.money().isPositive()));
    temp.get(false).forEach(Liability::setAbsolute);

    // der Algorithmus gleicht die kleinere Menge aus
    boolean flip = temp.get(true).size() < temp.get(false).size();

    List<Liability> smaller = temp.get(!flip);
    List<Liability> bigger = temp.get(flip);

    smaller.sort(Comparator.naturalOrder());
    bigger.sort(Comparator.reverseOrder());

    Set<Transaktion> transaktions = runAlgorithm(smaller, bigger);

    // flip, wenn zu Beginn Schuldner und Verschuldete getauscht wurden
    if (flip) {
      transaktions =
          transaktions.stream().map(TransaktionsRechner::flip).collect(Collectors.toSet());
    }
    return transaktions;
  }

  private static Set<Transaktion> runAlgorithm(List<Liability> smaller, List<Liability> bigger) {
    Set<Transaktion> transaktions = new HashSet<>();

    while (!smaller.isEmpty()) {
      Liability compensateeLiability = smaller.get(0);
      Set<Liability> smallestSubset = SubsetCalculator.smallestSubsetThatEquals(Set.copyOf(bigger),
          compensateeLiability.money());
      if (smallestSubset != null) {
        compensateBySmallestSubset(bigger, transaktions, compensateeLiability.user(),
            smallestSubset);
      } else {
        compensateByBiggest(bigger, transaktions, compensateeLiability);
      }
      smaller.remove(0);
    }

    return transaktions;
  }

  private static void compensateBySmallestSubset(List<Liability> bigger,
      Set<Transaktion> transaktions, String compensateUser, Set<Liability> smallestSubset) {
    transaktions.addAll(smallestSubset.stream()
        .map(l -> new Transaktion(l.user(), compensateUser, l.money.getCents()))
        .collect(Collectors.toSet()));
    bigger.removeAll(smallestSubset);
  }

  private static void compensateByBiggest(List<Liability> bigger, Set<Transaktion> transaktions,
      Liability compensateeLiability) {
    MoneyWrapper compensateeMoney = compensateeLiability.money();
    String compensateeUser = compensateeLiability.user();

    while (!MoneyWrapper.isClose(compensateeMoney, MoneyWrapper.ONE_CENT)) {
      Liability compensator = bigger.get(0);
      MoneyWrapper compensatorMoney = compensator.money();
      String compensatorUser = compensator.user();

      if (MoneyWrapper.isClose(compensateeMoney, compensatorMoney)) {
        transaktions.add(
            new Transaktion(compensatorUser, compensateeUser, compensatorMoney.getCents()));
        break;
      } else if (compensateeMoney.isLessThan(compensatorMoney)) {
        transaktions.add(
            new Transaktion(compensatorUser, compensateeUser, compensateeMoney.getCents()));
        compensator.subtractMoney(compensateeMoney);
        break;
      } else if (compensateeMoney.isGreaterThan(compensatorMoney)) {
        transaktions.add(
            new Transaktion(compensatorUser, compensateeUser, compensatorMoney.getCents()));
        compensateeMoney = compensateeMoney.subtract(compensatorMoney);
      }

      bigger.remove(0);
    }
  }

  private static Transaktion flip(Transaktion origin) {
    return new Transaktion(origin.glaeubiger(), origin.schuldner(), origin.cents());
  }
}
