package pro.anuj.skunkworks.bandbaja.core.execution;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import pro.anuj.skunkworks.bandbaja.core.domain.Payload;

public interface TaskExecutionContext {

  Payload getPayload();

  TaskExecutionResult getTaskExecutionResult();

  void setTaskExecutionResult(TaskExecutionResult runResult);

  <T> T set(@NonNull String key, @NonNull T value);

  <T> T get(@NonNull String key);

  <T> void multiSet(Map<String, T> values);

  @Getter
  @Setter
  @RequiredArgsConstructor
  class DefaultTaskExecutionContext implements TaskExecutionContext {
    private final Payload payload;
    private final Map<String, Object> map = new HashMap<>();
    private TaskExecutionResult taskExecutionResult;

    public static TaskExecutionContext of(Payload payload) {
      return new DefaultTaskExecutionContext(payload);
    }

    public static TaskExecutionContext of(Payload payload, Map<String, String> map) {
      DefaultTaskExecutionContext context = new DefaultTaskExecutionContext(payload);
      context.getMap().putAll(map);
      return context;
    }

    @Override
    public <T> T set(@NonNull String key, @NonNull T value) {
      //noinspection unchecked
      return (T) map.put(key, value);
    }

    @Override
    public <T> void multiSet(Map<String, T> values) {
      map.putAll(values);
    }

    @Override
    public <T> T get(@NonNull String key) {
      if (map.containsKey(key)) {
        //noinspection unchecked
        return (T) map.get(key);
      } else {
        return null;
      }
    }
  }
}
