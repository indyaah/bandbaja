package pro.anuj.skunkworks.bandbaja.core.execution;

import java.util.Map;

public interface TaskExecutionResult {

    boolean success();

    Map<String, String> additionalProperties();

    record DefaultTaskExecutionResult(boolean success,
                                      Map<String, String> additionalProperties) implements TaskExecutionResult {

    }
}
