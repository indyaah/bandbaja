package pro.anuj.skunkworks.bandbaja.core.execution;

import pro.anuj.skunkworks.bandbaja.core.domain.ErrorOrResult;

@FunctionalInterface
public interface TaskExecutor {

  ErrorOrResult<TaskExecutionResult> execute(TaskExecutionContext taskExecutionContext);
}
