package pro.anuj.skunkworks.bandbaja.core.execution;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import pro.anuj.skunkworks.bandbaja.core.domain.ErrorOrResult;
import pro.anuj.skunkworks.bandbaja.core.domain.Payload;

@Log4j2
@AllArgsConstructor
public class DefaultTaskExecutor implements TaskExecutor {

  private final TaskProvider taskProvider;

  @Override
  public ErrorOrResult<TaskExecutionResult> execute(TaskExecutionContext taskExecutionContext) {
    try {
      final Payload payload = taskExecutionContext.getPayload();
      final String taskType = payload.getTaskType();
      final Task task = taskProvider.get(taskType);

      boolean preprocess = task.preExecute(taskExecutionContext);

      if (preprocess) {
        TaskExecutionResult executionResult = task.execute(taskExecutionContext);
        taskExecutionContext.setTaskExecutionResult(executionResult);
        if (executionResult.success()) {
          boolean postprocess = task.postExecute(taskExecutionContext);
          if (postprocess) {
            log.debug("Post-execute succeeded.");
          } else {
            log.warn("Post-execute Failed.");
          }
        } else {
          if (task.isRetryable()) {
            boolean retry = task.retry(taskExecutionContext);
            log.debug("Task retry result : {}", retry);
          }
        }
        return ErrorOrResult.result(executionResult);
      } else {
        log.error("Pre-execute failed. Skipping execution");
        return ErrorOrResult.error(
            new IllegalStateException("Pre-execute failed. Skipping execution"));
      }
    } catch (Exception e) {
      return ErrorOrResult.error(e);
    }
  }
}
