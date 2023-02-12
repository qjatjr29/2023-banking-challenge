package numble.banking.core.token;

import static org.junit.jupiter.api.Assertions.*;

import numble.banking.core.common.error.exception.UnAuthorizedException;
import numble.banking.core.user.command.domain.Role;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class JwtTokenProviderTest {

  Logger log = LoggerFactory.getLogger(JwtTokenProviderTest.class);

  @Autowired
  JwtTokenProvider jwtTokenProvider;

  Long id;
  String email;
  Role role;

  @BeforeEach
  void setup() {
    id = 1L;
    email = "test@gmail.com";
    role = Role.USER;
  }

  @Test
  @DisplayName("토큰 생성 테스트")
  void generateToken() {

    // given
    TokenData tokenData = new TokenData(id, email, role.name());

    // when
    String accessToken = jwtTokenProvider.generateAccessToken(tokenData);
    String refreshToken = jwtTokenProvider.generateRefreshToken(tokenData);

    // then
    Assertions.assertThat(accessToken).isNotNull();
    Assertions.assertThat(refreshToken).isNotNull();
  }

  @Test
  @DisplayName("토큰 정보 추출 테스트")
  void extractToken() {

    // given
    TokenData tokenData = new TokenData(id, email, role.name());

    // when
    String accessToken = jwtTokenProvider.generateAccessToken(tokenData);
    TokenData data = jwtTokenProvider.getTokenData(accessToken);

    // then

    Assertions.assertThat(data).isNotNull();
    Assertions.assertThat(data.getUserId()).isEqualTo(id);
    Assertions.assertThat(data.getEmail()).isEqualTo(email);
    Assertions.assertThat(data.getRole()).isEqualTo(role.name());
  }

  /**
   * accesstoken 의 유효기간이 2000 인 경우
   */
  @Test
  @DisplayName("토큰이 만료된 경우")
  void checkExpiredToken() {
    // given
    TokenData tokenData = new TokenData(id, email, role.name());
    String accessToken = jwtTokenProvider.generateAccessToken(tokenData);

    // when
    sleep(3000);

    // then
    Assertions.assertThatThrownBy(() -> jwtTokenProvider.validateToken(accessToken))
        .isInstanceOf(UnAuthorizedException.class);

  }

  private void sleep(int millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      log.error(e.getMessage(), e);
    }
  }

}