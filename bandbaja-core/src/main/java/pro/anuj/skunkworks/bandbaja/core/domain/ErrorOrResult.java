package pro.anuj.skunkworks.bandbaja.core.domain;

public class ErrorOrResult<T> extends Pair<Exception, T> {

  private ErrorOrResult(Exception left, T right) {
    super(left, right);
  }

  public static <T> ErrorOrResult<T> error(Exception e) {
    return new ErrorOrResult<>(e, null);
  }

  public static <T> ErrorOrResult<T> result(T t) {
    return new ErrorOrResult<>(null, t);
  }

  public T getOrThrow() throws Exception {
    if (leftPresent()) {
      throw getLeft();
    } else {
      return getRight();
    }
  }
}
