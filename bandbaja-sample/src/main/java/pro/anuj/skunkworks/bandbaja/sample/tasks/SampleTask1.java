package pro.anuj.skunkworks.bandbaja.sample.tasks;

import java.util.Map;

import org.springframework.stereotype.Component;

import pro.anuj.skunkworks.bandbaja.core.execution.Task;
import pro.anuj.skunkworks.bandbaja.core.execution.TaskExecutionContext;
import pro.anuj.skunkworks.bandbaja.core.execution.TaskExecutionResult;
import pro.anuj.skunkworks.bandbaja.core.execution.TaskExecutionResult.DefaultTaskExecutionResult;

@Component("task1")
public class SampleTask1 implements Task {

    @Override
    public TaskExecutionResult execute(TaskExecutionContext context) {
        return new DefaultTaskExecutionResult(true, Map.of());
    }
}
