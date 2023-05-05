package propra.splitter.rest;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;

import java.util.Set;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import propra.splitter.application.RestGruppen;
import propra.splitter.domain.model.Gruppe;
import propra.splitter.domain.model.Transaktion;
import propra.splitter.rest.requestentities.NeueAusgabeRequestEntity;
import propra.splitter.rest.requestentities.NeueGruppeRequestEntity;

@RestController
@RequestMapping("/api")
public class RestApiController {

  RestGruppen restGruppen;

  public RestApiController(RestGruppen restGruppen) {
    this.restGruppen = restGruppen;
  }

  @GetMapping("/user/{username}/gruppen")
  Object getPersonGruppen(@PathVariable String username) {
    Set<Gruppe> gruppen = this.restGruppen.getGruppen(username);
    return Adapter.toJsonPersonGruppen(gruppen);
  }

  @GetMapping("/gruppen/{id}")
  Object getGruppenInfo(@PathVariable Long id) {
    Gruppe gruppe = this.restGruppen.getGruppe(id);
    return Adapter.toJsonGruppenInfo(gruppe);
  }

  @GetMapping("/gruppen/{id}/ausgleich")
  Object getAusgleich(@PathVariable Long id) {
    Gruppe gruppe = this.restGruppen.getGruppe(id);
    Set<Transaktion> transaktionen = gruppe.berechneTransaktionen();
    return Adapter.toJsonAusgleichszahlungen(transaktionen);
  }

  @ResponseStatus(CREATED)
  @PostMapping("/gruppen")
  Object postGruppe(@RequestBody @Valid NeueGruppeRequestEntity entity) {
    return this.restGruppen.neueGruppe(entity.personen(), entity.name());
  }

  @ResponseStatus(CREATED)
  @PostMapping("/gruppen/{id}/auslagen")
  ResponseEntity<String> postAusgabe(@RequestBody @Valid NeueAusgabeRequestEntity entity,
      BindingResult br, @PathVariable long id) {
    HttpStatus status;
    String body;

    if (br.hasErrors()) {
      status = BAD_REQUEST;
      body = "JSON Dokument fehlerhaft.";
    } else {
      this.restGruppen.neueAusgabe(id, entity.grund(), entity.glaeubiger(), entity.centVal(),
          entity.schuldner());
      status = CREATED;
      body = "Auslage erzeugt.";
    }
    return new ResponseEntity<>(body, status);
  }

  @PostMapping("/gruppen/{id}/schliessen")
  String postSchliessen(@PathVariable long id) {
    this.restGruppen.schliessen(id);
    return "Gruppe geschlossen.";
  }
}
