package pro.anuj.skunkworks.bandbaja.core.contracts;

@FunctionalInterface
public interface ExceptionHandler<T> {

  enum ErrorHandlerResult {
    ACKNOWLEDGE,
    IGNORE;
  }

  ErrorHandlerResult handle(T message, Exception e);
}
