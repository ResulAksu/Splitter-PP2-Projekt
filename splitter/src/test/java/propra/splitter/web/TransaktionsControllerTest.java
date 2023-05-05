package propra.splitter.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import propra.splitter.application.Gruppen;
import propra.splitter.domain.model.Gruppe;
import propra.splitter.domain.model.Transaktion;
import propra.splitter.web.displayentities.DisplayTransaktion;
import propra.splitter.web.helper.WithMockOAuth2User;

@WebMvcTest(controllers = TransaktionsController.class)
@DisplayName("Der Controller ")
public class TransaktionsControllerTest {

  @Autowired
  private MockMvc mvc;

  @MockBean
  Gruppen gruppen;

  @Test
  @WithMockOAuth2User(login = "Milo")
  @DisplayName("gewährt autorisierten Usern Zugriff")
  void test_01() throws Exception {
    // Arrange
    Gruppe group = mock(Gruppe.class);
    when(group.id()).thenReturn(1L);
    when(gruppen.getGruppe("Milo", 1L)).thenReturn(group);

    // Act
    mvc.perform(get("/gruppen/1/transaktionen").with(csrf()))

        // Assert
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("leitet auf GitHub weiter, wenn nicht-autorisierte User versuchen zuzugreifen")
  void test_02() throws Exception {
    // Act
    MvcResult mvcResult = mvc.perform(get("/gruppen/1/transaktionen"))

        //Assert
        .andExpect(status().is3xxRedirection()).andReturn();
    assertThat(mvcResult.getResponse().getRedirectedUrl()).contains("oauth2/authorization/github");
  }

  @Test
  @WithMockOAuth2User(login = "A")
  @DisplayName("zeigt alle Transaktionen an")
  void test_03() throws Exception {
    //Arrange
    Gruppe group = mock(Gruppe.class);
    when(group.id()).thenReturn(1L);
    when(gruppen.getGruppe("A", 1L)).thenReturn(group);

    DisplayTransaktion dt1 = new DisplayTransaktion("B", "A", "300.00 €");
    DisplayTransaktion dt2 = new DisplayTransaktion("C", "A", "300.00 €");
    DisplayTransaktion dt3 = new DisplayTransaktion("E", "D", "50.00 €");

    Transaktion t1 = new Transaktion("B", "A", 30000);
    Transaktion t2 = new Transaktion("C", "A", 30000);
    Transaktion t3 = new Transaktion("E", "D", 5000);

    when(group.berechneTransaktionen()).thenReturn(Set.of(t1, t2, t3));

    //Act
    mvc.perform(get("/gruppen/1/transaktionen"))

        //Assert
        .andExpect(model().attribute("glaeubiger_transaktionen", Set.of(dt1, dt2)))
        .andExpect(model().attribute("andere_transaktionen", Set.of(dt3)));
  }
}
