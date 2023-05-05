package propra.splitter.persistence;

import static org.springframework.data.relational.core.sql.LockMode.PESSIMISTIC_READ;
import static org.springframework.data.relational.core.sql.LockMode.PESSIMISTIC_WRITE;

import java.util.Optional;
import java.util.Set;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.relational.repository.Lock;
import org.springframework.data.repository.CrudRepository;
import propra.splitter.persistence.entities.GruppenEntity;

public interface GruppenRepository extends CrudRepository<GruppenEntity, Long> {

  @Override
  @Lock(PESSIMISTIC_WRITE)
  Optional<GruppenEntity> findById(Long id);

  @Lock(PESSIMISTIC_READ)
  @Query("SELECT * "
      + "FROM gruppen_entity "
      + "INNER JOIN gruppen_teilnehmer_entity "
      + "ON gruppen_entity.id = gruppen_teilnehmer_entity.gruppen_entity "
      + "WHERE gruppen_teilnehmer_entity.username = :participant")
  Set<GruppenEntity> findAllByParticipantsContains(String participant);
}
