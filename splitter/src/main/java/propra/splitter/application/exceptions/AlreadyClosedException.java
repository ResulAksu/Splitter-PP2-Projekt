package propra.splitter.application.exceptions;

public class AlreadyClosedException extends RuntimeException {

  private long id;
  private String user;


  public AlreadyClosedException() {
  }

  public AlreadyClosedException(long id, String user) {
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
