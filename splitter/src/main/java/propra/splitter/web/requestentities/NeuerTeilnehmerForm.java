package propra.splitter.web.requestentities;

import javax.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public class NeuerTeilnehmerForm {

  @Length(max = 39, message = "Darf maximal 39 Zeichen lang sein")
  @Pattern(regexp = "[\\S\\d-]+", message = "Kein gültiger GitHub-Handle")
  private String username;

  public void setUsername(String username) {
    this.username = username;
  }

  public String getUsername() {
    return username;
  }

}