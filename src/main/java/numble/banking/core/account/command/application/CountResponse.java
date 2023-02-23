package numble.banking.core.account.command.application;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CountResponse {

  private Long count;

  public CountResponse(Long count) {
    this.count = count;
  }
}
