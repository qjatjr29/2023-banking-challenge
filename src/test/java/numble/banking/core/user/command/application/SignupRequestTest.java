package numble.banking.core.user.command.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;
import java.util.stream.Stream;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("회원가입 요청 dto 테스트")
class SignupRequestTest {

  Validator validator;

  @BeforeEach
  void setup() {
    ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    validator = validatorFactory.getValidator();
  }

  @ParameterizedTest(name = "Pwd: {0} -> result : {1}")
  @MethodSource("getRegexPasswords")
  @DisplayName("여러가지 패스워드 테스트")
  void validate_password(String password, boolean excepted) {
    // Given
    SignupRequest request = SignupRequest.builder()
        .loginId("beomsic")
        .password(password)
        .name("test")
        .phone("010-0000-0000")
        .email("test@gmail.com")
        .build();

    // When
    Set<ConstraintViolation<SignupRequest>> validate = validator.validate(request);

    // Then
    assertThat(validate.isEmpty()).isEqualTo(excepted);
  }

  @ParameterizedTest(name = "signRequest : {0}--> result : {1} !!")
  @MethodSource("getSignupRequests")
  @DisplayName("여러가지 signRequest 테스트 ")
  void validate_signRequest(SignupRequest request, boolean excepted) {
    // Given

    // When
    Set<ConstraintViolation<SignupRequest>> validate = validator.validate(request);

    // Then
    assertThat(validate.isEmpty()).isEqualTo(excepted);
  }

  static Stream<Arguments> getRegexPasswords() {
    return Stream.of(
        Arguments.of("abc123!", false),
        Arguments.of("abcdefghijk", false),
        Arguments.of("01223456789", false),
        Arguments.of("!@#$%^&*()", false),
        Arguments.of("password1234", false),
        Arguments.of("password!@", false),
        Arguments.of("12345678!@", false),
        Arguments.of("abcdefg0123456789!@#$", false),
        Arguments.of("password12!", true),
        Arguments.of("abcde123!@##", true),
        Arguments.of("3!43ad@4", true)
    );
  }

  static Stream<Arguments> getSignupRequests() {
    return Stream.of(
        Arguments.of(new SignupRequest("testid", "password12!", "beomsic", "010-0000-0000", "test@gmail.com", "08324", "서울시", "서울시"), true),
        Arguments.of(new SignupRequest("", "password12!", "beomsic", "010-0000-0000", "test@gmail.com", "08324", "서울시", "서울시"), false),
        Arguments.of(new SignupRequest("testid", "password12!", "", "010-0000-0000", "test@gmail.com", "08324", "서울시", "서울시"), false),
        Arguments.of(new SignupRequest("testid", "password12!", "beomsic", "", "test@gmail.com", "08324", "서울시", "서울시"), false),
        Arguments.of(new SignupRequest("testid", "password12!", "beomsic", "010-0000-0000", "", "08324", "서울시", "서울시"), false),
        Arguments.of(new SignupRequest("testid", "", "beomsic", "010-0000-0000", "test@gmail.com", "08324", "서울시", "서울시"), false),
        Arguments.of(new SignupRequest("testid", "password12!", "beomsic", "010-0000-0000", "test@gmail.com", "", "", ""), true)
    );
  }


}