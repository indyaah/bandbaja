package pro.anuj.skunkworks.bandbaja.core.domain;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Payload {

  private String taskType;
  private JsonNode payload;
  private TaskMetadata metadata;
}
