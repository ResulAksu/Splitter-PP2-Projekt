package propra.splitter.rest;

import java.util.Set;
import java.util.stream.Collectors;
import propra.splitter.domain.model.Ausgabe;
import propra.splitter.domain.model.Gruppe;
import propra.splitter.domain.model.Transaktion;
import propra.splitter.rest.requestentities.Ausgleichszahlung;
import propra.splitter.rest.requestentities.GruppeVonPerson;
import propra.splitter.rest.requestentities.GruppenInfo;

public class Adapter {

  static propra.splitter.rest.requestentities.Ausgabe toJsonAusgabe(Ausgabe ausgabe) {
    return new propra.splitter.rest.requestentities.Ausgabe(ausgabe.grund(), ausgabe.glauebiger(),
        ausgabe.cents(), ausgabe.schuldner());
  }

  static Set<Ausgleichszahlung> toJsonAusgleichszahlungen(Set<Transaktion> transaktions) {
    return transaktions.stream().map(Adapter::toJsonAusgleichszahlung).collect(Collectors.toSet());
  }

  private static Ausgleichszahlung toJsonAusgleichszahlung(Transaktion transaktion) {
    return new Ausgleichszahlung(transaktion.schuldner(), transaktion.glaeubiger(),
        transaktion.cents());
  }

  static GruppenInfo toJsonGruppenInfo(Gruppe gruppe) {
    Set<propra.splitter.rest.requestentities.Ausgabe> expens =
        gruppe.ausgaben().stream().map(Adapter::toJsonAusgabe).collect(Collectors.toSet());
    return new GruppenInfo(String.valueOf(gruppe.id()), gruppe.name(), gruppe.teilnehmer(),
        gruppe.geschlossen(), expens);
  }

  static Set<GruppeVonPerson> toJsonPersonGruppen(Set<Gruppe> gruppen) {
    return gruppen.stream().map(Adapter::toJsonPersonGruppe).collect(Collectors.toSet());
  }

  private static GruppeVonPerson toJsonPersonGruppe(Gruppe gruppe) {
    return new GruppeVonPerson(String.valueOf(gruppe.id()), gruppe.name(), gruppe.teilnehmer());
  }
}
