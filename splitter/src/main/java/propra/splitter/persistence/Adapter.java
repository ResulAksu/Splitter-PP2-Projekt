package propra.splitter.persistence;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import propra.splitter.domain.model.Ausgabe;
import propra.splitter.domain.model.Gruppe;
import propra.splitter.persistence.entities.AusgabenEntity;
import propra.splitter.persistence.entities.AusgabenTeilnehmerEntity;
import propra.splitter.persistence.entities.GruppenEntity;
import propra.splitter.persistence.entities.GruppenTeilnehmerEntity;

public class Adapter {

  static Set<Gruppe> gruppenToDomain(Collection<GruppenEntity> entities) {
    return entities.stream().map(Adapter::gruppeToDomain).collect(Collectors.toSet());
  }

  static Gruppe gruppeToDomain(GruppenEntity entity) {
    Set<String> participants = entity.teilnehmer().stream().map(GruppenTeilnehmerEntity::username)
        .collect(Collectors.toSet());
    Set<Ausgabe> expens =
        entity.ausgaben().stream().map(Adapter::ausgabeToDomain).collect(Collectors.toSet());
    return new Gruppe(entity.id(), entity.titel(), participants, expens, entity.geschlossen());
  }

  private static Ausgabe ausgabeToDomain(AusgabenEntity entity) {
    Set<String> participants = entity.teilnehmer().stream().map(AusgabenTeilnehmerEntity::username)
        .collect(Collectors.toSet());
    return new Ausgabe(entity.titel(), entity.cents(), entity.glaeubiger(), participants,
        entity.datetime());
  }

  static GruppenEntity gruppeToPersistence(Gruppe gruppe) {
    Set<GruppenTeilnehmerEntity> participants =
        gruppe.teilnehmer().stream().map(GruppenTeilnehmerEntity::new).collect(Collectors.toSet());
    Set<AusgabenEntity> expenses =
        gruppe.ausgaben().stream().map(Adapter::ausgabeToPersistence).collect(Collectors.toSet());
    return new GruppenEntity(gruppe.id(), gruppe.name(), participants, expenses,
        gruppe.geschlossen());
  }

  private static AusgabenEntity ausgabeToPersistence(Ausgabe ausgabe) {
    Set<AusgabenTeilnehmerEntity> participants =
        ausgabe.schuldner().stream().map(AusgabenTeilnehmerEntity::new).collect(Collectors.toSet());
    return new AusgabenEntity(null, ausgabe.grund(), ausgabe.cents(), ausgabe.glauebiger(),
        participants, ausgabe.datetime());
  }
}
