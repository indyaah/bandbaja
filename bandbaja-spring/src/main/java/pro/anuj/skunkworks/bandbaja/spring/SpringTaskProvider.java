package pro.anuj.skunkworks.bandbaja.spring;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import pro.anuj.skunkworks.bandbaja.core.execution.Task;
import pro.anuj.skunkworks.bandbaja.core.execution.TaskProvider;

@AllArgsConstructor
public class SpringTaskProvider implements TaskProvider {

  private final ApplicationContext applicationContext;

  @Override
  public Task get(String taskType) {
    return applicationContext.getBean(taskType, Task.class);
  }
}
