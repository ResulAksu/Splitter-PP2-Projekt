package propra.splitter.web;

import static propra.splitter.web.TransaktionsController.TransaktionsStatus.GLAEUBIGER;
import static propra.splitter.web.TransaktionsController.TransaktionsStatus.NICHT_BETEILIGT;
import static propra.splitter.web.TransaktionsController.TransaktionsStatus.SCHULDNER;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import propra.splitter.application.Gruppen;
import propra.splitter.domain.model.Gruppe;
import propra.splitter.web.displayentities.DisplayTransaktion;

@Controller
public class TransaktionsController {

  Gruppen groups;

  public TransaktionsController(Gruppen groups) {
    this.groups = groups;
  }

  public enum TransaktionsStatus {
    NICHT_BETEILIGT, SCHULDNER, GLAEUBIGER;

    static TransaktionsStatus ist(DisplayTransaktion transaktion, String user) {
      if (transaktion.von().equals(user)) {
        return SCHULDNER;
      }
      if (transaktion.an().equals(user)) {
        return GLAEUBIGER;
      }
      return NICHT_BETEILIGT;
    }
  }

  @GetMapping("/gruppen/{id}/transaktionen")
  public String transactions(@PathVariable long id, Model model,
      @ModelAttribute("username") String user) {
    Gruppe gruppe = groups.getGruppe(user, id);

    Set<DisplayTransaktion> displayTransaktionen =
        Adapter.toDisplayTransaktionen(gruppe.berechneTransaktionen());
    Map<TransaktionsStatus, Set<DisplayTransaktion>> geteilteTransaktionen =
        displayTransaktionen.stream().collect(
            Collectors.groupingBy(t -> TransaktionsStatus.ist(t, user), Collectors.toSet()));

    model.addAttribute("username", user);
    model.addAttribute("gruppentitel", gruppe.name());
    model.addAttribute("schuldner_transaktionen", geteilteTransaktionen.get(SCHULDNER));
    model.addAttribute("glaeubiger_transaktionen", geteilteTransaktionen.get(GLAEUBIGER));
    model.addAttribute("andere_transaktionen", geteilteTransaktionen.get(NICHT_BETEILIGT));

    return "transaktionen";
  }
}
