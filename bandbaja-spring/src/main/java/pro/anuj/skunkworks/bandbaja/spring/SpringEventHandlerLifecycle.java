package pro.anuj.skunkworks.bandbaja.spring;

import jakarta.annotation.PreDestroy;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import pro.anuj.skunkworks.bandbaja.core.contracts.EventHandlerContainer;

@AllArgsConstructor
public class SpringEventHandlerLifecycle implements ApplicationListener<ApplicationReadyEvent> {

  private final List<EventHandlerContainer> eventHandlers;

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    eventHandlers.forEach(EventHandlerContainer::start);
  }

  @PreDestroy
  public void destroy() {
    eventHandlers.forEach(EventHandlerContainer::stop);
  }
}
