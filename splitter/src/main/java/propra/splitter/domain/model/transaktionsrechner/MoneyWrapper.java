package propra.splitter.domain.model.transaktionsrechner;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Objects;
import org.javamoney.moneta.Money;

public class MoneyWrapper implements Comparable<MoneyWrapper> {

  public static final MoneyWrapper ONE_CENT = of(new BigDecimal("0.01"));

  Money money;

  public MoneyWrapper(Money money) {
    this.money = Money.from(money);
  }

  public static MoneyWrapper from(MoneyWrapper wrapper) {
    return MoneyWrapper.ofCents(wrapper.getCents());
  }

  public static MoneyWrapper ofCents(int cents) {
    return MoneyWrapper.of(new BigDecimal(cents)).divide(100);
  }

  private static MoneyWrapper of(BigDecimal money) {
    return new MoneyWrapper(euroMoney(money));
  }

  private static Money euroMoney(Number num) {
    return Money.of(num, "EUR");
  }

  public static boolean isClose(MoneyWrapper val1, MoneyWrapper val2) {
    return val1.subtract(val2).abs().isApproxOneCent();
  }

  private boolean isApproxOneCent() {
    return this.getCents() <= 1;
  }

  public boolean isZero() {
    return this.money.isZero();
  }

  public boolean isPositive() {
    return this.money.isPositive();
  }

  public boolean isLessThan(MoneyWrapper money) {
    return this.getCents() < money.getCents();
  }

  public boolean isGreaterThan(MoneyWrapper money) {
    return this.getCents() > money.getCents();
  }

  public MoneyWrapper abs() {
    return new MoneyWrapper(this.money.abs());
  }

  public MoneyWrapper negate() {
    return new MoneyWrapper(this.money.negate());
  }

  public MoneyWrapper subtract(MoneyWrapper money) {
    return new MoneyWrapper(this.money.subtract(money.money));
  }

  public MoneyWrapper add(MoneyWrapper money) {
    return new MoneyWrapper(this.money.add(money.money));
  }

  public MoneyWrapper addCents(int cents) {
    return new MoneyWrapper(this.money.add(euroMoney(cents).divide(100)));
  }

  public MoneyWrapper divide(Number value) {
    return new MoneyWrapper(this.money.divide(value));
  }

  public static MoneyWrapper sum(Collection<MoneyWrapper> set) {
    return set.stream().reduce(MoneyWrapper.ofCents(0), MoneyWrapper::add);
  }

  public int getCents() {
    return money.multiply(100).getNumberStripped().intValue();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MoneyWrapper that = (MoneyWrapper) o;
    return getCents() == that.getCents();
  }

  @Override
  public int hashCode() {
    return Objects.hash(money);
  }

  @Override
  public int compareTo(MoneyWrapper o) {
    return getCents() - o.getCents();
  }

  @Override
  public String toString() {
    return money.toString();
  }
}
