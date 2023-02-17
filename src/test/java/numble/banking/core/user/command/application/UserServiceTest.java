package numble.banking.core.user.command.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import java.util.Optional;
import numble.banking.core.common.error.exception.ConflictException;
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

@ExtendWith(MockitoExtension.class)
@DisplayName("유저 서비스 테스트")
class UserServiceTest {

  @InjectMocks
  UserService userService;

  @Mock
  UserRepository userRepository;


  String loginId;
  String password;
  String email;
  String phone;

  @BeforeEach
  void setup() {
    loginId = "beomsic";
    password = "password12!";
    email = "beomsic@gmail.com";
    phone = "010-0000-0000";
  }

  @Nested
  @DisplayName("회원가입 테스트")
  class signup {

    SignupRequest request;

    @BeforeEach
    void setup() {
      request = new SignupRequest(loginId, password, "beomseok", phone, email, "08326", "서울시", "서울시");
    }

    @Test
    @DisplayName("회원가입 성공")
    void signupSuccess() {

      // given
      // when
      UserDetailResponse userDetail = userService.signup(request);

      // then
      Assertions.assertThat(userDetail).isNotNull();
      Assertions.assertThat(userDetail.getName()).isEqualTo("beomseok");
      Assertions.assertThat(userDetail.getEmail()).isEqualTo(email);
      Assertions.assertThat(userDetail.getPhone()).isEqualTo(phone);
    }

    @Test
    @DisplayName("아이디 중복으로 인한 회원가입 실패")
    void signupFailDuplicateLoginId() {
      // given
      given(userRepository.existsByLoginId(loginId))
          .willReturn(true);

      // when
      // then
      Assertions.assertThatThrownBy(() -> userService.signup(request))
          .isInstanceOf(ConflictException.class);
    }

    @Test
    @DisplayName("휴대폰 번호 중복으로 인한 회원가입 실패")
    void signupFailDuplicatePhone() {
      // given
      given(userRepository.existsByPhone(phone))
          .willReturn(true);

      // when
      // then
      Assertions.assertThatThrownBy(() -> userService.signup(request))
          .isInstanceOf(ConflictException.class);
    }

    @Test
    @DisplayName("이메일 중복으로 인한 회원가입 실패")
    void signupFailDuplicateEmail() {
      // given
      given(userRepository.existsByEmail(email))
          .willReturn(true);

      // when
      // then
      Assertions.assertThatThrownBy(() -> userService.signup(request))
          .isInstanceOf(ConflictException.class);
    }
  }

  @Test
  @DisplayName("친구 사이 확인 테스트")
  void areTheyFriend() {

    // given
    given(userRepository.findById(1L))
        .willReturn(Optional.of(User.builder().id(1L).build()));

    given(userRepository.findById(2L))
        .willReturn(Optional.of(User.builder().id(2L).build()));
    // when
    userService.follow(1L, 2L);

    // then
    Assertions.assertThat(userRepository
            .findById(1L).get()
            .areTheyFriend(2L))
        .isTrue();
  }
}