package pro.anuj.skunkworks.bandbaja.core.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Pair<L, R> {

  private final L left;
  private final R right;

  public boolean leftPresent() {
    return left != null;
  }

  public boolean rightPresent() {
    return right != null;
  }
}
