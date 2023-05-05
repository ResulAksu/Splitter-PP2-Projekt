package propra.splitter.rest;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import propra.splitter.application.exceptions.AlreadyClosedException;
import propra.splitter.application.exceptions.GroupNotFoundException;
import propra.splitter.application.exceptions.NotPossibleException;

@RestControllerAdvice
public class ExceptionHandlers {

  @ResponseStatus(value = CONFLICT, reason = "Gruppe bereits geschlossen.")
  @ExceptionHandler(AlreadyClosedException.class)
  void alreadyClosed() {
  }

  @ResponseStatus(value = NOT_FOUND, reason = "Gruppe nicht gefunden.")
  @ExceptionHandler(GroupNotFoundException.class)
  void groupNotFound() {
  }

  @ResponseStatus(value = BAD_REQUEST, reason = "Manche Teilnehmer sind nicht Teil der Gruppe.")
  @ExceptionHandler(NotPossibleException.class)
  void teilnehmerNichtGruppe() {
  }
}
