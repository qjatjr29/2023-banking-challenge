package numble.banking.core.user.command.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import numble.banking.support.repository.BaseRepositoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("유저 repository 테스트")
class UserRepositoryTest extends BaseRepositoryTest {

  @Autowired
  UserRepository userRepository;

  String loginId;
  String password;


  @BeforeEach
  void setup() {
    loginId = "beomsic";
    password = "password12!";
  }

  @Test
  @DisplayName("저장 성공 테스트")
  void saveSuccessTest() {
    // given
    User user = User.builder()
        .loginId(loginId)
        .password(password)
        .name("beomsic")
        .email("test@gmail.com")
        .phone("010-0000-0000")
        .build();

    user.encryptPassword();;

    // when
    User savedUser = userRepository.save(user);

    // then
    assertThat(savedUser).isNotNull();
    assertEquals(loginId, savedUser.getLoginId());
    assertTrue(savedUser.verifyPassword(password));
  }

}