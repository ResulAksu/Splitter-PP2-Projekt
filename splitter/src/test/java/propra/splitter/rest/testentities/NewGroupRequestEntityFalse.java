package propra.splitter.rest.testentities;

import java.util.Set;

public record NewGroupRequestEntityFalse(String name, Set<String> mitglieder) {

  public NewGroupRequestEntityFalse(String name, Set<String> mitglieder) {
    this.name = name;
    this.mitglieder = Set.copyOf(mitglieder);
  }

  @Override
  public Set<String> mitglieder() {
    return Set.copyOf(mitglieder);
  }
}
