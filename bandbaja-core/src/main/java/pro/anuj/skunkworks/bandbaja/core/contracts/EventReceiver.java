package pro.anuj.skunkworks.bandbaja.core.contracts;

import java.util.List;
import pro.anuj.skunkworks.bandbaja.core.domain.ErrorOrResult;

public interface EventReceiver<T> {

  List<T> receive(Integer batchSize);

  ErrorOrResult<Boolean> acknowledge(T message);
}
