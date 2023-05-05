package propra.splitter.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import propra.splitter.web.helper.WithMockOAuth2User;

@WebMvcTest(controllers = MainController.class)
public class MainControllerTest {

  @Autowired
  private MockMvc mvc;

  @MockBean
  Gruppen groups;


  @Test
  @WithMockOAuth2User(login = "Milo")
  @DisplayName("Authorisierte User haben Zugriff")
  void test_01() throws Exception {
    //Act & Assert
    mvc.perform(get("/")).andExpect(status().isOk());
  }

  @Test
  @DisplayName("Nicht authorisierte User haben keinen Zugriff")
  void test_02() throws Exception {
    //Arrange & Act
    MvcResult mvcResult = mvc.perform(get("/"))
        //Assert
        .andExpect(status().is3xxRedirection()).andReturn();
    assertThat(mvcResult.getResponse().getRedirectedUrl()).contains("oauth2/authorization/github");
  }

  @Test
  @WithMockOAuth2User(login = "Resul")
  @DisplayName("Offene Gruppen werden angezeigt")
  void test_03() throws Exception {
    //Arrange
    Gruppe group1 = Gruppe.of("Resul", "Party");
    Gruppe group2 = Gruppe.of("Resul", "Pc");
    Gruppe group3 = new Gruppe(null, "SeafoodHater", Set.of("Resul", "Milad"), null, false);
    when(groups.getGruppen("Resul")).thenReturn(Set.of(group1, group2, group3));

    //Act
    mvc.perform(get("/"))
        //Assert
        .andExpect(model().attribute("offene_gruppen", Set.of(group1, group2, group3)));
  }

  @Test
  @WithMockOAuth2User(login = "Meto")
  @DisplayName("Geschlossene Gruppen werden angezeigt")
  void test_04() throws Exception {
    //Arrange
    Gruppe group1 = new Gruppe(null, "SeafoodHater", Set.of("Meto", "Milad"), null, true);
    Gruppe group2 = new Gruppe(null, "Meatlover", Set.of("Resul", "Meto"), null, true);
    when(groups.getGruppen("Meto")).thenReturn(Set.of(group1, group2));

    //Act
    mvc.perform(get("/"))
        //Assert
        .andExpect(model().attribute("geschlossene_gruppen", Set.of(group1, group2)));
  }

  @Test
  @WithMockOAuth2User(login = "Milad")
  @DisplayName("Gruppe kann erstellt werden")
  void test_05() throws Exception {
    //Arrange
    Gruppe group = mock(Gruppe.class);
    when(group.id()).thenReturn(1L);
    when(groups.neueGruppe("Milad", "SeafoodHater")).thenReturn(1L);

    //Act
    mvc.perform(post("/").param("gruppenTitel", "SeafoodHater").with(csrf()))

        //Assert
        .andExpect(status().is3xxRedirection());
  }

  @Test
  @DisplayName("Gruppe wird nicht erstellt, wenn nicht authorisiert")
  void test_06() throws Exception {
    //Act
    MvcResult mvcResult = mvc.perform(post("/").param("gruppenTitel", "SeafoodHater").with(csrf()))
        //Assert
        .andExpect(status().is3xxRedirection()).andReturn();
    assertThat(mvcResult.getResponse().getRedirectedUrl()).contains("oauth2/authorization/github");
  }
}
