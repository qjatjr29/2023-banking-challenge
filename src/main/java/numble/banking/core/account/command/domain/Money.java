package numble.banking.core.account.command.domain;

import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Money {

  private Long money;

  public Long getMoney() {
    return money;
  }

  public Money transfer(Long transferredMoney) {
    return new Money(money + transferredMoney);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Money money1 = (Money) o;
    return Objects.equals(money, money1.money);
  }

  @Override
  public int hashCode() {
    return Objects.hash(money);
  }
}
