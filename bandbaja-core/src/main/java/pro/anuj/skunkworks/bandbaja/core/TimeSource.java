package pro.anuj.skunkworks.bandbaja.core;

public interface TimeSource {

  TimeSource SYSTEM_TIME_SOURCE = new TimeSource() {};

  default Long getCurrentTime() {
    return System.currentTimeMillis();
  }
}
