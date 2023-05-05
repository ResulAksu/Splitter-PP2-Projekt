package propra.splitter.application;

import java.time.LocalDateTime;
import java.util.Set;
import org.springframework.stereotype.Service;
import propra.splitter.application.exceptions.AlreadyClosedException;
import propra.splitter.application.exceptions.NotAllowedException;
import propra.splitter.application.exceptions.NotPossibleException;
import propra.splitter.domain.model.Ausgabe;
import propra.splitter.domain.model.Gruppe;

@Service
public class GruppenImpl implements Gruppen {

  private final GruppenConnection connection;

  public GruppenImpl(GruppenConnection connection) {
    this.connection = connection;
  }

  @Override
  public long neueGruppe(String caller, String title) {
    Gruppe gruppe = Gruppe.of(caller, title);

    return connection.saveGruppe(gruppe);
  }

  @Override
  public void neuerTeilnehmer(String caller, long id, String teilnehmer) {
    Gruppe gruppe = connection.getGruppe(id);

    authenticate(caller, gruppe);

    gruppe.teilnehmerHinzufuegen(teilnehmer);
    connection.saveGruppe(gruppe);
  }

  @Override
  public void neueAusgabe(String caller, long id, String grund, String glaeubiger, int cents,
      Set<String> schuldner) {
    Gruppe gruppe = connection.getGruppe(id);

    authenticate(caller, gruppe);
    if (!gruppe.teilnehmer().containsAll(schuldner) || !gruppe.teilnehmer().contains(glaeubiger)) {
      throw new NotPossibleException(id, caller);
    }

    Ausgabe ausgabe = new Ausgabe(grund, cents, glaeubiger, schuldner, LocalDateTime.now());
    gruppe.ausgabeHinzufuegen(ausgabe);
    connection.saveGruppe(gruppe);
  }

  @Override
  public void schliessen(String caller, long id) {
    Gruppe gruppe = connection.getGruppe(id);

    authenticate(caller, gruppe);

    connection.saveGruppe(gruppe.schliessen());
  }

  @Override
  public Gruppe getGruppe(String caller, long id) {
    Gruppe gruppe = connection.getGruppe(id);

    if (!gruppe.teilnehmer().contains(caller)) {
      throw new NotAllowedException();
    }

    return gruppe;
  }

  @Override
  public Set<Gruppe> getGruppen(String user) {
    return connection.getGruppen(user);
  }

  private void authenticate(String caller, Gruppe gruppe) {
    if (gruppe.geschlossen()) {
      throw new AlreadyClosedException(gruppe.id(), caller);
    }
    if (!gruppe.teilnehmer().contains(caller)) {
      throw new NotAllowedException();
    }
  }
}
