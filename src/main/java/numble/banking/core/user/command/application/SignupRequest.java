package numble.banking.core.user.command.application;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SignupRequest {

  @NotBlank
  @Size(min = 6, max = 12)
  @Pattern(regexp = "^[a-z\\d]*$", message = "영문 소문자 또는 숫자만 사용 가능합니다.")
  private String loginId;

  @NotNull
  @PasswordValidation
  private String password;

  @NotBlank
  private String name;

  @NotBlank
  @Pattern(regexp = "^01([0|1|6|7|8|9])-?([0-9]{3,4})-?([0-9]{4})$")
  private String phone;

  @NotBlank
  @Pattern(regexp = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "이메일 형식에 맞지 안습니다")
  private String email;

  private String zipCode;

  private String address;

  private String roadAddress;

}
