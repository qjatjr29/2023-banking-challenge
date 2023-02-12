package numble.banking.core.user.command.application;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class LoginRequest {

  @NotBlank
  @Size(min = 6, max = 12)
  @Pattern(regexp = "^[a-z\\d]*$", message = "영문 소문자 또는 숫자만 사용 가능합니다.")
  private String loginId;

  @NotNull
  @PasswordValidation
  private String password;

}
