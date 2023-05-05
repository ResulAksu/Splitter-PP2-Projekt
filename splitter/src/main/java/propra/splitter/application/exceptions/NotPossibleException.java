package propra.splitter.application.exceptions;

public class NotPossibleException extends RuntimeException {

  private long id;
  private String user;

  public NotPossibleException() {
  }

  public NotPossibleException(long id, String user) {
    this.id = id;
    this.user = user;
  }

  public long getId() {
    return id;
  }

  public String getUser() {
    return user;
  }
}
