package propra.splitter.rest;


import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import propra.splitter.SpringConfiguration;
import propra.splitter.application.RestGruppen;
import propra.splitter.application.exceptions.AlreadyClosedException;
import propra.splitter.application.exceptions.GroupNotFoundException;
import propra.splitter.domain.model.Ausgabe;
import propra.splitter.domain.model.Gruppe;

@WebMvcTest
@ContextConfiguration(classes = {SpringConfiguration.class, RestApiController.class,
    ExceptionHandlers.class})
@DisplayName("Die API ")
public class RestApiTest {

  @Autowired
  MockMvc mvc;

  @MockBean
  RestGruppen restGruppen;


  public static final MediaType APPLICATION_JSON =
      new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype());

  /*
   * Works tests
   */

  /*
   * Get tests
   */

  @Test
  @DisplayName("gibt die Gruppen einer Person zurück")
  void test_01() throws Exception {
    // Arrange
    Gruppe gruppe1 = new Gruppe(1L, "Seafood-Hater", Set.of("Mick", "Taha"), Set.of(), false);
    Gruppe gruppe2 = new Gruppe(2L, "Investment", Set.of("Mick", "Resul"), Set.of(), false);
    when(restGruppen.getGruppen("Mick")).thenReturn(Set.of(gruppe1, gruppe2));

    // Act
    mvc.perform(get("/api/user/Mick/gruppen"))

        // Assert
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Seafood-Hater"))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("Investment"));
  }

  @Test
  @DisplayName("gibt eine leeres Array zurück, wenn eine Person nicht existiert")
  void test_02() throws Exception {
    // Arrange
    when(restGruppen.getGruppen("Mick")).thenReturn(Set.of());

    // Act
    mvc.perform(get("/api/user/Ronnie/gruppen"))

        // Assert
        .andExpect(status().isOk()).andExpect(content().json("[]"));
  }

  @Test
  @DisplayName("gibt alle Gruppeninformationen zurück")
  void test_03() throws Exception {
    // Arrange
    Ausgabe ausgabe = new Ausgabe("Black Paint", 2599, "Keith", Set.of("Mick", "Keith", "Ronnie"),
        LocalDateTime.MAX);
    Gruppe gruppe =
        new Gruppe(1L, "Tour 2023", Set.of("Mick", "Keith", "Ronnie"), Set.of(ausgabe), false);
    when(restGruppen.getGruppe(1L)).thenReturn(gruppe);

    // Act
    mvc.perform(get("/api/gruppen/1"))

        // Assert
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Tour 2023"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.ausgaben[0].grund").value("Black Paint"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.ausgaben[0].schuldner").isNotEmpty());
  }

  @Test
  @DisplayName("berechnet die Transaktionen")
  void test_04() throws Exception {
    // Arrange
    Ausgabe ausgabe =
        new Ausgabe("Black Paint", 2600, "Keith", Set.of("Mick", "Ronnie"), LocalDateTime.MAX);
    Gruppe gruppe =
        new Gruppe(1L, "Tour 2023", Set.of("Mick", "Keith", "Ronnie"), Set.of(ausgabe), false);
    when(restGruppen.getGruppe(1L)).thenReturn(gruppe);

    // Act
    mvc.perform(get("/api/gruppen/1/ausgleich"))

        // Assert
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].von").value("Mick"))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].an").value("Keith"))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].cents").value(1300))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].von").value("Ronnie"))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].an").value("Keith"))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].cents").value(1300));
  }

  /*
   * Post tests
   */

  @Test
  @DisplayName("erzeugt eine neue Gruppe")
  void test_05() throws Exception {
    // Arrange
    when(restGruppen.neueGruppe(anySet(), anyString())).thenReturn(1L);

    // Act
    String bodyContent =
        "{\"name\" : \"Tour 2023\", \"personen\" : [\"Mick\", \"Keith\", \"Ronnie\"] }";

    mvc.perform(post("/api/gruppen").content(bodyContent).contentType(APPLICATION_JSON))

        // Assert
        .andExpect(status().isCreated());
  }

  @Test
  @DisplayName("erzeugt eine neue Ausgabe")
  void test_06() throws Exception {
    // Act
    String bodyContent = "{\"grund\": \"Black Paint\", \"glaeubiger\": \"Keith\", \"cent\" : 2599, "
        + "\"schuldner\" : [\"Mick\", \"Keith\", \"Ronnie\"]}";

    mvc.perform(post("/api/gruppen/1/auslagen").contentType(APPLICATION_JSON).content(bodyContent))

        // Assert
        .andExpect(status().isCreated());
  }

  @Test
  @DisplayName("schließt eine Gruppe")
  void test_07() throws Exception {
    // Act
    mvc.perform(post("/api/gruppen/1/schliessen"))

        // Assert
        .andExpect(status().isOk());
  }

  /*
   * Logic fails tests
   */

  @Test
  @DisplayName("gibt 404 aus, wenn versucht wird die Informationen einer nicht-existenten Gruppe "
      + "zu erlangen")
  void test_08() throws Exception {
    // Arrange
    doThrow(GroupNotFoundException.class).when(restGruppen).getGruppe(2L);

    // Act
    mvc.perform(get("/api/gruppen/2"))

        // Assert
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("gibt 400 aus, wenn versucht wird eine neue Gruppe zu erzeugen, die JSON aber"
      + " fehlerhaft ist")
  void test_09() throws Exception {
    // Act
    String bodyContent =
        "{\"titel\" : \"Tour 2023\", \"personen\" : [\"Mick\", \"Keith\", \"Ronnie\"] }";

    when(restGruppen.neueGruppe(Set.of("Mick", "Keith", "Ronnie"), "Tour 2023")).thenReturn(1L);
    mvc.perform(post("/api/gruppen").contentType(APPLICATION_JSON).content(bodyContent))

        // Assert
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("gibt 400 aus, wenn versucht wird eine neue Ausgabe zu erzeugen, die JSON aber"
      + " fehlerhaft ist")
  void test_10() throws Exception {
    // Act
    String bodyContent = "{\"grund\": \"Black Paint\", \"glaeubiger\": \"Keith\", \"geld\" : 2599, "
        + "\"schuldner\" : [\"Mick\", \"Keith\", \"Ronnie\"]}";

    mvc.perform(post("/api/gruppen/1/auslagen").contentType(APPLICATION_JSON).content(bodyContent))

        // Assert
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("gibt 404 aus, wenn versucht wird eine neue Ausgabe zu erzeugen, die Gruppe aber "
      + "nicht existiert")
  void test_11() throws Exception {
    // Arrange
    doThrow(GroupNotFoundException.class).when(restGruppen)
        .neueAusgabe(1L, "Black Paint", "Keith", 2599, Set.of("Mick", "Keith", "Ronnie"));

    // Act
    String bodyContent = "{\"grund\": \"Black Paint\", \"glaeubiger\": \"Keith\", \"cent\" : 2599, "
        + "\"schuldner\" : [\"Mick\", \"Keith\", \"Ronnie\"]}";

    mvc.perform(post("/api/gruppen/1/auslagen").contentType(APPLICATION_JSON).content(bodyContent))

        // Assert
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("gibt 409 aus, wenn versucht wird eine Auslage zu erzeugen, die Gruppe aber "
      + "bereits geschlossen ist")
  void test_12() throws Exception {
    // Arrange
    doThrow(AlreadyClosedException.class).when(restGruppen)
        .neueAusgabe(1L, "Black Paint", "Keith", 2599, Set.of("Mick", "Keith", "Ronnie"));

    // Act
    String bodyContent = "{\"grund\": \"Black Paint\", \"glaeubiger\": \"Keith\", \"cent\" : 2599, "
        + "\"schuldner\" : [\"Mick\", \"Keith\", \"Ronnie\"]}";

    mvc.perform(post("/api/gruppen/1/auslagen").contentType(APPLICATION_JSON).content(bodyContent))

        // Assert
        .andExpect(status().isConflict());

  }

  @Test
  @DisplayName("gibt 404 aus, wenn versucht wird eine Gruppe zu schließen, die Gruppe aber "
      + "nicht existiert")
  void test_13() throws Exception {
    // Arrange
    doThrow(GroupNotFoundException.class).when(restGruppen).schliessen(1L);

    // Act
    mvc.perform(post("/api/gruppen/1/schliessen"))

        // Assert
        .andExpect(status().isNotFound());
  }
}
