package propra.splitter.domain.model.transaktionsrechner;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

public class SubsetCalculator {

  static Set<Liability> smallestSubsetThatEquals(Set<Liability> liabilities, MoneyWrapper money) {
    Set<Liability> smallest = null;
    Queue<Set<Liability>> queued = new LinkedList<>(Set.of(liabilities));
    while (!queued.isEmpty()) {
      Set<Liability> head = queued.poll();
      if (isEqualToMoneyAndHasSmallerSize(money, smallest, head)) {
        smallest = head;
      }
      for (Liability liability : head) {
        Set<Liability> temp = new HashSet<>(head);
        temp.remove(liability);
        queued.add(temp);
      }
    }
    return smallest;
  }

  private static boolean isEqualToMoneyAndHasSmallerSize(MoneyWrapper money,
      Set<Liability> smallest, Set<Liability> poll) {
    boolean bool1 = MoneyWrapper.isClose(sumOfLiabilities(poll), money);
    boolean bool2 = poll.size() < (smallest != null ? smallest.size() : Integer.MAX_VALUE);
    return bool1 && bool2;
  }

  private static MoneyWrapper sumOfLiabilities(Set<Liability> temp) {
    return MoneyWrapper.sum(temp.stream().map(Liability::money).collect(Collectors.toList()));
  }
}
