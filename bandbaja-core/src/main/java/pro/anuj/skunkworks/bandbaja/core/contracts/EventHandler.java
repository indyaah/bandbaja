package pro.anuj.skunkworks.bandbaja.core.contracts;

import pro.anuj.skunkworks.bandbaja.core.domain.ErrorOrResult;
import pro.anuj.skunkworks.bandbaja.core.execution.TaskExecutionResult;

@FunctionalInterface
public interface EventHandler<T> {

  ErrorOrResult<TaskExecutionResult> handle(T event);
}
