package numble.banking.core.token;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import numble.banking.core.user.command.domain.User;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TokenData {

  private Long userId;
  private String email;
  private String role;

  public static TokenData of(final User user) {
    return TokenData.builder()
        .userId(user.getId())
        .email(user.getEmail())
        .role(user.getRole().name())
        .build();
  }

}
