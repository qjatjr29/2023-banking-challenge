package numble.banking.core.account.command.application;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountCountResponse {

  private Long count;

  public AccountCountResponse(Long count) {
    this.count = count;
  }
}
