package propra.splitter.rest.testentities;

import java.util.Set;


public record NewExpenseRequestEntityFalseJson(String grund, String glaeubiger, String geld,
                                               Set<String> schuldner) {

  public NewExpenseRequestEntityFalseJson(String grund, String glaeubiger, String geld,
      Set<String> schuldner) {
    this.grund = grund;
    this.glaeubiger = glaeubiger;
    this.geld = geld;
    this.schuldner = Set.copyOf(schuldner);
  }

  @Override
  public Set<String> schuldner() {
    return Set.copyOf(schuldner);
  }
}
