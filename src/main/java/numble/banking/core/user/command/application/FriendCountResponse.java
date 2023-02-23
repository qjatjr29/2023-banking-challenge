package numble.banking.core.user.command.application;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FriendCountResponse {

  private Long count;

  public FriendCountResponse(Long count) {
    this.count = count;
  }
}
