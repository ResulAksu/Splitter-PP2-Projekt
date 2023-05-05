package propra.splitter.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import propra.splitter.application.Gruppen;
import propra.splitter.application.exceptions.GroupNotFoundException;
import propra.splitter.application.exceptions.NotAllowedException;
import propra.splitter.domain.model.Ausgabe;
import propra.splitter.domain.model.Gruppe;
import propra.splitter.web.displayentities.DisplayAusgabe;
import propra.splitter.web.displayentities.DisplayGruppe;
import propra.splitter.web.helper.WithMockOAuth2User;

@WebMvcTest(controllers = GruppenController.class)
public class GruppenControllerTest {

  @Autowired
  private MockMvc mvc;

  @MockBean
  Gruppen groups;

  @Test
  @WithMockOAuth2User(login = "Milo")
  @DisplayName("Authorisiert, hat Zugriff")
  void test_01() throws Exception {
    Gruppe gruppe = mock(Gruppe.class);
    when(gruppe.id()).thenReturn(1L);
    when(groups.getGruppe("Milo", 1L)).thenReturn(gruppe);

    mvc.perform(get("/gruppen/1")).andExpect(status().isOk());
  }

  @Test
  @DisplayName("Nicht authorisiert, kein Zugriff")
  void test_02() throws Exception {
    //Arrange & Act
    MvcResult mvcResult = mvc.perform(get("/gruppen/1"))

        //Assert
        .andExpect(status().is3xxRedirection()).andReturn();
    assertThat(mvcResult.getResponse().getRedirectedUrl()).contains("oauth2/authorization/github");
  }

  @Test
  @WithMockOAuth2User(login = "Milo")
  @DisplayName("Gruppe existiert nicht")
  void test_03() throws Exception {
    when(groups.getGruppe("Milo", 400)).thenThrow(GroupNotFoundException.class);
    mvc.perform(get("/gruppen/400")).andExpect(status().isNotFound());
  }

  @Test
  @WithMockOAuth2User(login = "Milad")
  @DisplayName("Alle Mitglieder anzeigen")
  void test_04() throws Exception {
    //Arrange
    Gruppe gruppe = mock(Gruppe.class);
    when(gruppe.id()).thenReturn(1L);
    when(gruppe.teilnehmer()).thenReturn(Set.of("Milad", "MetoAbi", "Jens"));
    when(groups.getGruppe("Milad", 1L)).thenReturn(gruppe);

    //Act
    MvcResult mvcResult = mvc.perform(get("/gruppen/1")).andReturn();

    //Assert
    DisplayGruppe displayGruppe =
        (DisplayGruppe) mvcResult.getModelAndView().getModel().get("gruppe");
    assertThat(displayGruppe.teilnehmer()).isEqualTo(Set.of("Milad", "MetoAbi", "Jens"));
  }

  @Test
  @WithMockOAuth2User(login = "Milad")
  @DisplayName("Neues Mitglied hinzugefuen")
  void test_05() throws Exception {
    Gruppe gruppe = mock(Gruppe.class);
    when(gruppe.id()).thenReturn(1L);
    when(groups.getGruppe("Milad", 1L)).thenReturn(gruppe);

    //Act
    mvc.perform(post("/gruppen/1?newParticipant").param("username", "Resul").with(csrf()))
        //Assert
        .andExpect(status().isCreated());
  }

  @Test
  @DisplayName("Nicht authorisiert, neues Mitglied nicht hinzugefuegen")
  void test_06() throws Exception {
    //Arrange
    Gruppe group = mock(Gruppe.class);
    when(group.id()).thenReturn(1L);

    //Act
    MvcResult mvcResult =
        mvc.perform(post("/gruppen/1?newParticipant").param("username", "Resul").with(csrf()))
            //Assert
            .andExpect(status().is3xxRedirection()).andReturn();
    assertThat(mvcResult.getResponse().getRedirectedUrl()).contains("oauth2/authorization/github");
  }

  @Test
  @WithMockOAuth2User(login = "Taha")
  @DisplayName("Nicht Mitglied der Gruppe, neues Mitglied nicht hinzuzufuegen")
  void test_07() throws Exception {
    //Arrange
    doThrow(NotAllowedException.class).when(groups).neuerTeilnehmer("Taha", 1L, "Resul");
    //Act
    mvc.perform(post("/gruppen/1?newParticipant").param("username", "Resul").with(csrf()))
        //Assert
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockOAuth2User(login = "Milo")
  @DisplayName("Gruppe existiert nicht, neues Mitglied")
  void test_08() throws Exception {

    doThrow(GroupNotFoundException.class).when(groups).neuerTeilnehmer("Milo", 1L, "Resul");
    mvc.perform(post("/gruppen/1?newParticipant").param("username", "Resul").with(csrf()))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockOAuth2User(login = "MetoAbi")
  @DisplayName("Ausgaben anzeigen")
  void test_09() throws Exception {
    //Arrange
    Ausgabe expense = new Ausgabe("Chupa Chups", 2000, "A", Set.of("MetoAbi"),
        LocalDateTime.of(1999, 6, 24, 17, 30));

    Gruppe gruppe = mock(Gruppe.class);
    when(gruppe.id()).thenReturn(1L);
    when(gruppe.ausgaben()).thenReturn(Set.of(expense));
    when(groups.getGruppe("MetoAbi", 1L)).thenReturn(gruppe);

    //Act
    MvcResult mvcResult = mvc.perform(get("/gruppen/1")).andReturn();

    //Assert
    DisplayAusgabe displayAusgabe =
        new DisplayAusgabe("Chupa Chups", "20.00 €", "A", Set.of("MetoAbi"),
            LocalDateTime.of(1999, 6, 24, 17, 30));

    DisplayGruppe displayGruppe =
        (DisplayGruppe) mvcResult.getModelAndView().getModel().get("gruppe");
    assertThat(displayGruppe.ausgaben()).containsExactly(displayAusgabe);
  }

  @Test
  @WithMockOAuth2User(login = "MetoAbi")
  @DisplayName("Neue Ausgabe hinzugefuegen")
  void test_10() throws Exception {

    Gruppe gruppe = mock(Gruppe.class);
    when(gruppe.id()).thenReturn(1L);
    when(groups.getGruppe("MetoAbi", 1L)).thenReturn(gruppe);
    //Act

    mvc.perform(
            post("/gruppen/1?newExpense").param("expenseName", "MeatLover").param("amount", "20")
                .param("debtee", "MetoAbi").param("teilnehmer", "Resul", "Taha", "MetoAbi")
                .with(csrf()))
        //Assert
        .andExpect(status().isCreated());
  }

  @Test
  @DisplayName("Nicht authorisiert, neue Ausgabe nicht hinzugefuegen")
  void test_11() throws Exception {
    //Act
    MvcResult mvcResult = mvc.perform(
            post("/gruppen/1?newExpense").param("expenseName", "MeatLover").param("amount", "20")
                .param("debtee", "MetoAbi").param("teilnehmer", "Resul", "Taha", "MetoAbi")
                .with(csrf()))
        //Assert
        .andExpect(status().is3xxRedirection()).andReturn();
    assertThat(mvcResult.getResponse().getRedirectedUrl()).contains("oauth2/authorization/github");
  }

  @Test
  @WithMockOAuth2User(login = "Taha")
  @DisplayName("Gruppe schliessen")
  void test_14() throws Exception {
    //Arrange
    Gruppe gruppe = mock(Gruppe.class);
    when(gruppe.id()).thenReturn(1L);
    when(groups.getGruppe("Taha", 1L)).thenReturn(gruppe);

    //Act
    mvc.perform(post("/gruppen/1?close").with(csrf()))
        //Assert
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Nicht autorisiert, Gruppe nicht schliessen")
  void test_15() throws Exception {
    //Arrange
    Gruppe gruppe = mock(Gruppe.class);
    when(gruppe.id()).thenReturn(1L);
    when(groups.getGruppe("Taha", 1L)).thenReturn(gruppe);

    doNothing().when(groups).schliessen("Taha", 1L);
    //Act
    MvcResult mvcResult = mvc.perform(post("/gruppen/1?close").with(csrf()))
        //Assert
        .andExpect(status().is3xxRedirection()).andReturn();
    assertThat(mvcResult.getResponse().getRedirectedUrl()).contains("oauth2/authorization/github");
  }

  @Test
  @WithMockOAuth2User(login = "JoeSchmoe")
  @DisplayName("Nicht Mitglied der Gruppe, Gruppe nicht schliessen")
  void test_17() throws Exception {
    //Arrange
    doThrow(NotAllowedException.class).when(groups).schliessen("JoeSchmoe", 1L);
    //Act
    mvc.perform(
            post("/gruppen/1?close").param("caller", "JoeSchmoe").param("gruppenid", "1").with(csrf()))
        //Assert
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockOAuth2User(login = "JoeSchmoe")
  @DisplayName("Gruppe existiert nicht, Gruppe nicht schliessen")
  void test_18() throws Exception {
    //Arrange
    doThrow(GroupNotFoundException.class).when(groups).schliessen("JoeSchmoe", 1L);
    //Act
    mvc.perform(
            post("/gruppen/1?close").param("caller", "JoeSchmoe").param("gruppenid", "1").with(csrf()))
        //Assert
        .andExpect(status().isNotFound());
  }

}
