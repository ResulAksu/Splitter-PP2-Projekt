package propra.splitter.domain.model.transaktionsrechner;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Liability implements Comparable<Liability> {

  String user;
  MoneyWrapper money;

  public Liability(String user, MoneyWrapper money) {
    this.user = user;
    this.money = MoneyWrapper.from(money);
  }

  public String user() {
    return user;
  }

  public MoneyWrapper money() {
    return MoneyWrapper.from(money);
  }

  public void setAbsolute() {
    this.money = this.money.abs();
  }

  public void subtractMoney(MoneyWrapper money) {
    this.money = this.money.subtract(money);
  }

  static Liability toLiability(Entry<String, MoneyWrapper> entry) {
    return new Liability(entry.getKey(), entry.getValue());
  }

  static Set<Liability> mapToSet(Map<String, MoneyWrapper> map) {
    return map.entrySet().stream().map(Liability::toLiability).collect(Collectors.toSet());
  }

  @Override
  public int compareTo(Liability o) {
    return this.equals(o) ? 0 : this.money.compareTo(o.money());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Liability liability = (Liability) o;
    return Objects.equals(user, liability.user) && Objects.equals(money, liability.money);
  }

  @Override
  public int hashCode() {
    return Objects.hash(user, money);
  }

  @Override
  public String toString() {
    return "Liability{" + "user='" + user + '\'' + ", money=" + money + '}';
  }
}