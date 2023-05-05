package propra.splitter.web;


import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import propra.splitter.application.exceptions.GroupNotFoundException;
import propra.splitter.application.exceptions.NotAllowedException;

@ControllerAdvice
public class GlobalExceptionHandlers {

  @ExceptionHandler(GroupNotFoundException.class)
  @ResponseStatus(NOT_FOUND)
  public String handleExceptionGroupNotFound() {
    return "groupNotFound";
  }

  @ExceptionHandler(NotAllowedException.class)
  @ResponseStatus(FORBIDDEN)
  public String handleExceptionNotAllowed() {
    return "notAllowed";
  }
}
