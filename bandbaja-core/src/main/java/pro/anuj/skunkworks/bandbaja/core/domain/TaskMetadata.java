package pro.anuj.skunkworks.bandbaja.core.domain;

import static pro.anuj.skunkworks.bandbaja.core.domain.TaskTrigger.APP;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode
public class TaskMetadata {
  private Instant timestamp = Instant.now();
  private int attemptNumber = 1;
  private TaskTrigger triggerType = APP;
  private Map<String, String> additionalProperties = new HashMap<>();
}
