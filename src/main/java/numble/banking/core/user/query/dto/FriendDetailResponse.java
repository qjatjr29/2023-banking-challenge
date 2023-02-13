package numble.banking.core.user.query.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import numble.banking.core.user.command.domain.Friend;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class FriendDetailResponse {

  private String name;
  private String phone;

  private FriendDetailResponse(final Friend friend) {
    this.name = friend.getName();
    this.phone = friend.getPhone();
  }

  public static FriendDetailResponse of(final Friend friend) {
    return new FriendDetailResponse(friend);
  }
}
