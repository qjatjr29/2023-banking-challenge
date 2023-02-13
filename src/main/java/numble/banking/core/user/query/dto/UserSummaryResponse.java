package numble.banking.core.user.query.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import numble.banking.core.user.command.domain.User;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserSummaryResponse {

  private Long id;
  private String name;
  private String email;
  private String phone;
  private Boolean isFriend;

  protected UserSummaryResponse(final User user, final Boolean isFriend) {
    this.id = user.getId();
    this.name = user.getName();
    this.email = user.getEmail();
    this.phone = user.getPhone();
    this.isFriend = isFriend;
  }

  public static UserSummaryResponse from(final User user, final boolean isFriend) {
    return new UserSummaryResponse(user, isFriend);
  }
}
