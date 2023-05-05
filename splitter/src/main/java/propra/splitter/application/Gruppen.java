package propra.splitter.application;

import java.util.Set;
import org.springframework.transaction.annotation.Transactional;
import propra.splitter.domain.model.Gruppe;

public interface Gruppen {

  long neueGruppe(String caller, String titel);

  @Transactional
  void neuerTeilnehmer(String caller, long id, String teilnehmer);

  @Transactional
  void neueAusgabe(String caller, long id, String grund, String glaeubiger, int cents,
      Set<String> schuldner);

  @Transactional
  void schliessen(String caller, long id);

  @Transactional
  Gruppe getGruppe(String caller, long id);

  Set<Gruppe> getGruppen(String user);
}
