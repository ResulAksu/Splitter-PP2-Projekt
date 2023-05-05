package propra.splitter.application;

import java.time.LocalDateTime;
import java.util.Set;
import org.springframework.stereotype.Service;
import propra.splitter.application.exceptions.AlreadyClosedException;
import propra.splitter.application.exceptions.NotPossibleException;
import propra.splitter.domain.model.Ausgabe;
import propra.splitter.domain.model.Gruppe;

@Service
public class RestGruppenImpl implements RestGruppen {

  private final GruppenConnection connection;

  public RestGruppenImpl(GruppenConnection connection) {
    this.connection = connection;
  }

  @Override
  public long neueGruppe(Set<String> teilnehmer, String title) {
    Gruppe gruppe = Gruppe.of(teilnehmer, title);

    return connection.saveGruppe(gruppe);
  }

  @Override
  public void neueAusgabe(long id, String grund, String glaeubiger, int cents,
      Set<String> schuldner) {
    Gruppe gruppe = connection.getGruppe(id);

    if (gruppe.geschlossen()) {
      throw new AlreadyClosedException();
    }
    if (!gruppe.teilnehmer().containsAll(schuldner) && !gruppe.teilnehmer().contains(glaeubiger)) {
      throw new NotPossibleException();
    }

    Ausgabe ausgabe = new Ausgabe(grund, cents, glaeubiger, schuldner, LocalDateTime.now());
    gruppe.ausgabeHinzufuegen(ausgabe);
    connection.saveGruppe(gruppe);
  }

  @Override
  public void schliessen(long id) {
    Gruppe gruppe = connection.getGruppe(id);

    connection.saveGruppe(gruppe.schliessen());
  }

  @Override
  public Gruppe getGruppe(long id) {
    return connection.getGruppe(id);
  }

  @Override
  public Set<Gruppe> getGruppen(String user) {
    return connection.getGruppen(user);
  }
}
