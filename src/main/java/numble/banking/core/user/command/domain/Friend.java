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

  private Friend(final Long friendId, final String name) {
    this.friendId = friendId;
    this.name = name;
  }

  public static Friend of(final User user) {
    return new Friend(user.getId(), user.getName());
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
        friend.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(friendId, name);
  }
}
