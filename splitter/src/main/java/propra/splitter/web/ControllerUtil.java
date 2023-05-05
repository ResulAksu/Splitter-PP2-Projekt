package propra.splitter.web;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(basePackages = "propra.splitter.web")
public class ControllerUtil {

  @ModelAttribute("username")
  String username(OAuth2AuthenticationToken token) {
    return token.getPrincipal().getAttribute("login");
  }
}
