package propra.splitter.application;

import java.util.Set;
import propra.splitter.domain.model.Gruppe;

public interface GruppenConnection {

  long saveGruppe(Gruppe gruppe);

  Gruppe getGruppe(long id);

  Set<Gruppe> getGruppen(String user);
}
