package propra.splitter.web;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;

import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import propra.splitter.application.Gruppen;
import propra.splitter.application.exceptions.AlreadyClosedException;
import propra.splitter.application.exceptions.NotPossibleException;
import propra.splitter.domain.model.Gruppe;
import propra.splitter.web.displayentities.DisplayGruppe;
import propra.splitter.web.requestentities.NeueAusgabeForm;
import propra.splitter.web.requestentities.NeuerTeilnehmerForm;

@Controller
@RequestMapping("/gruppen/")
public class GruppenController {

  Gruppen groups;

  public GruppenController(Gruppen groups) {
    this.groups = groups;
  }

  @GetMapping("/{id}")
  public String group(@PathVariable long id, Model model, @ModelAttribute("username") String user) {
    setUpModel(id, model, user);
    emptyForms(model);
    return "gruppe";
  }

  @ResponseStatus(CREATED)
  @PostMapping(value = "/{id}", params = "newParticipant")
  public String addNewMitglied(@Valid NeuerTeilnehmerForm neuerTeilnehmerForm,
      BindingResult bindingResult,
      Model model, @PathVariable long id, @ModelAttribute("username") String user) {
    if (bindingResult.hasErrors()) {
      setUpModel(id, model, user);
      withParticipantForm(model, neuerTeilnehmerForm);
      return "gruppe";
    }

    groups.neuerTeilnehmer(user, id, neuerTeilnehmerForm.getUsername());

    setUpModel(id, model, user);
    emptyForms(model);
    return "gruppe";
  }

  @ResponseStatus(CREATED)
  @PostMapping(value = "/{id}", params = "newExpense")
  public String addNewExpense(@Valid NeueAusgabeForm neueAusgabeForm, BindingResult bindingResult,
      Model model, @PathVariable long id, @ModelAttribute("username") String user) {
    if (bindingResult.hasErrors()) {
      setUpModel(id, model, user);
      withExpenseForm(model, neueAusgabeForm);
      return "gruppe";
    }

    groups.neueAusgabe(user, id, neueAusgabeForm.getGrund(), neueAusgabeForm.getGlaeubiger(),
        neueAusgabeForm.getCentVal(), neueAusgabeForm.getTeilnehmer());

    setUpModel(id, model, user);
    emptyForms(model);
    return "gruppe";
  }

  @PostMapping(value = "/{id}", params = "close")
  public String closeGroup(Model model, @PathVariable long id,
      @ModelAttribute("username") String user) {
    groups.schliessen(user, id);

    setUpModel(id, model, user);
    emptyForms(model);
    return "gruppe";
  }

  private void setUpModel(long id, Model model, String user) {
    Gruppe gruppe = groups.getGruppe(user, id);
    DisplayGruppe displayGruppe = Adapter.toDisplayGruppe(gruppe);

    model.addAttribute("gruppe", displayGruppe);
  }

  private void emptyForms(Model model) {
    model.addAttribute("teilnehmer_form", new NeuerTeilnehmerForm());
    model.addAttribute("ausgabe_form", new NeueAusgabeForm());
  }

  private void withParticipantForm(Model model, NeuerTeilnehmerForm form) {
    model.addAttribute("teilnehmer_form", form);
    model.addAttribute("ausgabe_form", new NeueAusgabeForm());
  }

  private void withExpenseForm(Model model, NeueAusgabeForm form) {
    model.addAttribute("teilnehmer_form", new NeuerTeilnehmerForm());
    model.addAttribute("ausgabe_form", form);
  }

  @ExceptionHandler(AlreadyClosedException.class)
  @ResponseStatus(BAD_REQUEST)
  public String handleException(Model model, AlreadyClosedException e) {
    setUpModel(e.getId(), model, e.getUser());
    emptyForms(model);
    model.addAttribute("display_already_closed", true);
    return "group";
  }

  @ExceptionHandler(NotPossibleException.class)
  @ResponseStatus(BAD_REQUEST)
  public String handleNotPossibleException(Model model, NotPossibleException e) {
    setUpModel(e.getId(), model, e.getUser());
    emptyForms(model);
    model.addAttribute("not_possible", true);
    return "group";
  }
}
