package pro.anuj.skunkworks.bandbaja.sqs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import pro.anuj.skunkworks.bandbaja.core.contracts.EventHandler;
import pro.anuj.skunkworks.bandbaja.core.domain.ErrorOrResult;
import pro.anuj.skunkworks.bandbaja.core.domain.Payload;
import pro.anuj.skunkworks.bandbaja.core.execution.TaskExecutionContext;
import pro.anuj.skunkworks.bandbaja.core.execution.TaskExecutionContext.DefaultTaskExecutionContext;
import pro.anuj.skunkworks.bandbaja.core.execution.TaskExecutionResult;
import pro.anuj.skunkworks.bandbaja.core.execution.TaskExecutor;
import software.amazon.awssdk.services.sqs.model.Message;

@Log4j2
@AllArgsConstructor
public class SqsEventHandler implements EventHandler<Message> {

  private final ObjectMapper objectMapper;
  private final TaskExecutor taskExecutor;

  @Override
  public ErrorOrResult<TaskExecutionResult> handle(Message event) {
    try {
      final Payload payload = objectMapper.readValue(event.body(), Payload.class);
      final TaskExecutionContext executionContext =
          DefaultTaskExecutionContext.of(payload, event.attributesAsStrings());
      return taskExecutor.execute(executionContext);
    } catch (JsonProcessingException e) {
      log.error("Could not convert message to Task payload", e);
      return ErrorOrResult.error(e);
    }
  }
}
