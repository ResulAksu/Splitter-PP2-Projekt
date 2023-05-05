package propra.splitter.web.requestentities;

import java.math.BigDecimal;
import java.util.Set;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

public class NeueAusgabeForm {

  @NotBlank(message = "Gib einen Namen ein")
  @Length(max = 50, message = "Darf maximal 50 Zeichen lang sein")
  private String grund;

  @NotNull(message = "Gib einen Betrag an")
  @Positive(message = "Positiven Betrag angeben")
  private BigDecimal geld;

  @NotBlank(message = "Gib einen Schuldner an")
  @Length(max = 39, message = "GitHub-Handles sind maximal 39 Zeichen lang")
  private String glaeubiger = "";

  @NotEmpty(message = "Gib Mitglieder an")
  private Set<String> teilnehmer;

  public String getGrund() {
    return grund;
  }

  public void setGrund(String grund) {
    this.grund = grund;
  }

  public BigDecimal getGeld() {
    return geld == null ? new BigDecimal(0) : geld.add(BigDecimal.ZERO);
  }

  public void setGeld(BigDecimal geld) {
    this.geld = geld;
  }

  public String getGlaeubiger() {
    return glaeubiger;
  }

  public void setGlaeubiger(String glaeubiger) {
    this.glaeubiger = glaeubiger;
  }

  public Set<String> getTeilnehmer() {
    return teilnehmer == null ? Set.of() : Set.copyOf(teilnehmer);
  }

  public void setTeilnehmer(Set<String> teilnehmer) {
    this.teilnehmer = Set.copyOf(teilnehmer);
  }

  public int getCentVal() {
    return geld.multiply(BigDecimal.valueOf(100)).intValue();
  }
}
