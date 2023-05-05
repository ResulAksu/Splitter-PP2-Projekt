package propra.splitter.web;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import propra.splitter.application.Gruppen;
import propra.splitter.domain.model.Gruppe;
import propra.splitter.web.requestentities.NeueGruppeForm;

@Controller
public class MainController {

  Gruppen gruppen;

  public MainController(Gruppen gruppen) {
    this.gruppen = gruppen;
  }

  @GetMapping("/")
  public String main(@ModelAttribute("username") String username, Model model) {
    setUpModel(username, model);
    model.addAttribute("neue_gruppe_form", new NeueGruppeForm());

    return "main";
  }

  @PostMapping("/")
  public String addNewGruppe(@Valid NeueGruppeForm neueGruppeForm, BindingResult bindingResult,
      Model model, @ModelAttribute("username") String username) {
    if (bindingResult.hasErrors()) {
      System.out.println(bindingResult.getFieldErrors());
      setUpModel(username, model);
      model.addAttribute("neue_gruppe_form", neueGruppeForm);
      return "main";
    }

    long id = gruppen.neueGruppe(username, neueGruppeForm.getGruppenTitel());
    return "redirect:/gruppen/" + id;
  }

  private void setUpModel(String username, Model model) {
    Set<Gruppe> gruppen = this.gruppen.getGruppen(username);
    Map<Boolean, Set<Gruppe>> statusOfGroup = gruppen.stream()
        .collect(Collectors.partitioningBy(Gruppe::geschlossen, Collectors.toSet()));

    model.addAttribute("offene_gruppen", statusOfGroup.get(false));
    model.addAttribute("geschlossene_gruppen", statusOfGroup.get(true));
  }
}
