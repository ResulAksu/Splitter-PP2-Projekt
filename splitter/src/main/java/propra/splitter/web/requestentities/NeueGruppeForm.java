package propra.splitter.web.requestentities;

import javax.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public class NeueGruppeForm {

  @NotBlank(message = "Bitte Gruppennamen eingeben")
  @Length(max = 50, message = "Darf maximal 50 Zeichen lang sein")
  private String gruppenTitel;

  public void setGruppenTitel(String gruppenTitel) {
    this.gruppenTitel = gruppenTitel;
  }

  public String getGruppenTitel() {
    return gruppenTitel;
  }
}
