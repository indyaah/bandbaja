package pro.anuj.skunkworks.bandbaja.sample.tasks;

import java.util.Map;

import org.springframework.stereotype.Component;

import pro.anuj.skunkworks.bandbaja.core.execution.Task;
import pro.anuj.skunkworks.bandbaja.core.execution.TaskExecutionContext;
import pro.anuj.skunkworks.bandbaja.core.execution.TaskExecutionResult;

@Component("task2")
public class SampleTask2 implements Task {

    @Override
    public TaskExecutionResult execute(TaskExecutionContext context) {
        return new TaskExecutionResult.DefaultTaskExecutionResult(true, Map.of());
    }
}
