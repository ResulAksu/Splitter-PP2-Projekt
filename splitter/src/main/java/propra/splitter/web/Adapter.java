package propra.splitter.web;

import java.util.Set;
import java.util.stream.Collectors;
import propra.splitter.domain.model.Ausgabe;
import propra.splitter.domain.model.Gruppe;
import propra.splitter.domain.model.Transaktion;
import propra.splitter.web.displayentities.DisplayAusgabe;
import propra.splitter.web.displayentities.DisplayGruppe;
import propra.splitter.web.displayentities.DisplayTransaktion;

public class Adapter {

  static DisplayGruppe toDisplayGruppe(Gruppe gruppe) {
    Set<DisplayAusgabe> ausgaben =
        gruppe.ausgaben().stream().map(Adapter::toDisplayAusgabe).collect(Collectors.toSet());
    return new DisplayGruppe(gruppe.id(), gruppe.name(), gruppe.teilnehmer(), ausgaben,
        gruppe.hatAusgaben(), gruppe.geschlossen());
  }

  static DisplayAusgabe toDisplayAusgabe(Ausgabe ausgabe) {
    return new DisplayAusgabe(ausgabe.grund(), zuEuro(ausgabe.cents()), ausgabe.glauebiger(),
        ausgabe.schuldner(), ausgabe.datetime());
  }

  static Set<DisplayTransaktion> toDisplayTransaktionen(Set<Transaktion> transaktionen) {
    return transaktionen.stream().map(Adapter::toDisplayTransaktion).collect(Collectors.toSet());
  }

  static DisplayTransaktion toDisplayTransaktion(Transaktion transaktion) {
    return new DisplayTransaktion(transaktion.schuldner(), transaktion.glaeubiger(),
        zuEuro(transaktion.cents()));
  }

  private static String zuEuro(int cents) {
    return String.format("%d.%02d €", cents / 100, cents % 100);
  }
}
