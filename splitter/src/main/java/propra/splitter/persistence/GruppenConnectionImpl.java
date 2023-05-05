package propra.splitter.persistence;

import java.util.Set;
import org.springframework.stereotype.Component;
import propra.splitter.application.GruppenConnection;
import propra.splitter.application.exceptions.GroupNotFoundException;
import propra.splitter.domain.model.Gruppe;

@Component
public class GruppenConnectionImpl implements GruppenConnection {

  private final GruppenRepository repo;

  public GruppenConnectionImpl(GruppenRepository repo) {
    this.repo = repo;
  }


  @Override
  public long saveGruppe(Gruppe gruppe) {
    return repo.save(Adapter.gruppeToPersistence(gruppe)).id();
  }

  @Override
  public Gruppe getGruppe(long id) {
    return Adapter.gruppeToDomain(repo.findById(id).orElseThrow(GroupNotFoundException::new));
  }

  @Override
  public Set<Gruppe> getGruppen(String user) {
    return Adapter.gruppenToDomain(repo.findAllByParticipantsContains(user));
  }
}
