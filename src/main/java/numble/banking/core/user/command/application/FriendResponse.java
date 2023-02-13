package numble.banking.core.user.command.application;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import numble.banking.core.user.command.domain.Friend;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class FriendResponse {

  private Long friendId;
  private String name;

  public static FriendResponse of(final Friend friend) {
    return new FriendResponse(friend.getFriendId(), friend.getName());
  }
}
