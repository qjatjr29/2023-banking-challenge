package numble.banking.core.user.command.application;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TokenResponse {

  private String accessToken;
  private String refreshToken;

  public TokenResponse(String accessToken, String refreshToken) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
  }
}
