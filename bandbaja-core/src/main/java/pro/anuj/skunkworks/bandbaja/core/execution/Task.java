package pro.anuj.skunkworks.bandbaja.core.execution;

public interface Task {

  default boolean preExecute(TaskExecutionContext context) {
    return true;
  }

  TaskExecutionResult execute(TaskExecutionContext context);

  default boolean postExecute(TaskExecutionContext context) {
    return true;
  }

  default boolean retry(TaskExecutionContext context) {
    return true;
  }

  default boolean isRetryable() {
    return false;
  }
}
