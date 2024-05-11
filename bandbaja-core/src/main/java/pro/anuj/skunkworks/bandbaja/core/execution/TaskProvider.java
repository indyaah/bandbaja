package pro.anuj.skunkworks.bandbaja.core.execution;

@FunctionalInterface
public interface TaskProvider {

  Task get(String taskType);
}
