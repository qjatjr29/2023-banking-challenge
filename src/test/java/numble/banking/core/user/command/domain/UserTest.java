package numble.banking.core.user.command.domain;

import static org.junit.jupiter.api.Assertions.*;

import numble.banking.core.common.error.exception.BadRequestException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("User Entity 관련 테스트")
class UserTest {

  @Test
  @DisplayName("유저 생성 테스트")
  void builderUser() {

    // given
    String loginId = "beomsic";
    String password = "password123!";

    // when
    User user = User.builder()
        .loginId(loginId)
        .password(password)
        .name("beomseok")
        .email("test@gmail.com")
        .address(Address.from("00000", "서울시", "서울시"))
        .phone("010-0000-0000")
        .build();

    user.encryptPassword();

    // then
    Assertions.assertThat(user).isNotNull();
    Assertions.assertThat(user.verifyPassword(password)).isTrue();
    Assertions.assertThat(user.getLoginId()).isEqualTo(loginId);
  }


  @Test
  @DisplayName("비밀번호 일치 확인 - 비밀번호 제대로 입력한 경우")
  void verifyPassword() {

    // given
    String loginId = "beomsic";
    String password = "password123!";

    // when
    User user = User.builder()
        .loginId(loginId)
        .password(password)
        .name("beomseok")
        .email("test@gmail.com")
        .address(Address.from("00000", "서울시", "서울시"))
        .phone("010-0000-0000")
        .build();

    user.encryptPassword();

    // then
    Assertions.assertThat(user).isNotNull();
    Assertions.assertThat(user.verifyPassword(password)).isTrue();
  }

  @Test
  @DisplayName("비밀번호 일치 확인 - 비밀번호 잘못 입력한 경우")
  void verifyPasswordFail() {
    // given
    String loginId = "beomsic";
    String password = "password123!";
    String otherPassword = "otherPassword12!";

    // when
    User user = User.builder()
        .loginId(loginId)
        .password(password)
        .name("beomseok")
        .email("test@gmail.com")
        .address(Address.from("00000", "서울시", "서울시"))
        .phone("010-0000-0000")
        .build();

    user.encryptPassword();

    // then
    Assertions.assertThat(user).isNotNull();
    Assertions.assertThatThrownBy(() -> user.verifyPassword(otherPassword))
        .isInstanceOf(BadRequestException.class);
  }
}