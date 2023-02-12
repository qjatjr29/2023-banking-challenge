package numble.banking.core.user.command.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

import java.util.Optional;
import numble.banking.core.common.error.exception.BadRequestException;
import numble.banking.core.common.error.exception.NotFoundException;
import numble.banking.core.token.JwtTokenProvider;
import numble.banking.core.token.TokenData;
import numble.banking.core.user.command.domain.User;
import numble.banking.core.user.command.domain.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("유저 Authentication 관련 테스트")
class UserAuthServiceTest {

  @InjectMocks
  private UserAuthService userAuthService;

  @Mock
  UserRepository userRepository;

  @Mock
  JwtTokenProvider jwtTokenProvider;

  @Mock
  RedisTemplate<String, Object> redisTemplate;

  @Mock
  private ValueOperations valueOperations;

  String loginId;
  String password;
  String accessToken;
  String refreshToken;
  User user;

  @BeforeEach
  void setup() {
    loginId = "beomsic";
    password = "password12!";
    accessToken = "test-accesstoken";
    refreshToken = "test-refreshtoken";

    user = User.builder()
        .loginId(loginId)
        .password(password)
        .name("beomsic")
        .email("test@gmail.com")
        .phone("010-0000-0000")
        .build();
    user.encryptPassword();
  }

  @Nested
  @DisplayName("로그인 테스트")
  class Login {

    @BeforeEach
    void setup() {
      given(userRepository.save(user))
          .willReturn(user);
      given(userRepository.findByLoginId(loginId))
          .willReturn(Optional.of(user));

      given(redisTemplate.opsForValue())
          .willReturn(valueOperations);
      doNothing().when(valueOperations).set(anyString(), any());

      given(jwtTokenProvider.generateAccessToken(any(TokenData.class)))
          .willReturn(accessToken);
      given(jwtTokenProvider.generateRefreshToken(any(TokenData.class)))
          .willReturn(refreshToken);
      given(jwtTokenProvider.getTokenExpiredIn(anyString()))
          .willReturn(1000L);
    }

    @Test
    @DisplayName("로그인 성공")
    void loginSuccess() {
      // given
      LoginRequest loginRequest = new LoginRequest(loginId, password);

      // when
      TokenResponse tokenResponse = userAuthService.login(loginRequest);

      // then
      Assertions.assertThat(tokenResponse).isNotNull();
      Assertions.assertThat(tokenResponse.getAccessToken()).isEqualTo(accessToken);
      Assertions.assertThat(tokenResponse.getRefreshToken()).isEqualTo(refreshToken);
    }

    @Test
    @DisplayName("없는 로그인 아이디 입력")
    void loginWrongLoginId() {
      // given
      LoginRequest loginRequest = new LoginRequest("wrongId", password);

      // when
      // then
      Assertions.assertThatThrownBy(() -> userAuthService.login(loginRequest))
              .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("잘못된 비밀번호 입력")
    void loginWrongPassword() {
      // given
      LoginRequest loginRequest = new LoginRequest(loginId, "wrongpw12!");

      // when
      // then
      Assertions.assertThatThrownBy(() -> userAuthService.login(loginRequest))
          .isInstanceOf(BadRequestException.class);
    }

  }

}