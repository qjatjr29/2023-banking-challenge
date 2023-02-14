package numble.banking.core.user.command.domain;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Friend {

  @Column(name = "friend_id")
  private Long friendId;

  @Column(name = "friend_name")
  private String name;

  @Column(name = "friend_phone")
  private String phone;

  private Friend(final Long friendId, final String name, final String phone) {
    this.friendId = friendId;
    this.name = name;
    this.phone = phone;
  }

  public static Friend of(final User user) {
    return new Friend(user.getId(), user.getName(), user.getPhone());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Friend friend = (Friend) o;
    return Objects.equals(friendId, friend.friendId) && Objects.equals(name,
        friend.name) && Objects.equals(phone, friend.phone);
  }

  @Override
  public int hashCode() {
    return Objects.hash(friendId, name, phone);
  }
}
