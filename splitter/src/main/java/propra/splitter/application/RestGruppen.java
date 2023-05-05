package propra.splitter.application;

import java.util.Set;
import org.springframework.transaction.annotation.Transactional;
import propra.splitter.domain.model.Gruppe;

public interface RestGruppen {

  long neueGruppe(Set<String> teilnehmer, String title);

  @Transactional
  void neueAusgabe(long id, String grund, String glaeubiger, int cents,
      Set<String> schuldner);

  @Transactional
  void schliessen(long id);

  @Transactional
  Gruppe getGruppe(long id);

  Set<Gruppe> getGruppen(String user);
}
