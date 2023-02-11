package numble.banking.core.user.command.application;


import java.text.MessageFormat;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<PasswordValidation, String> {

  private static final int MIN_SIZE = 8;
  private static final int MAX_SIZE = 15;
  private static final String REGEX_PASSWORD =
      "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&])[A-Za-z[0-9]$@$!%*#?&]{" + MIN_SIZE
          + "," + MAX_SIZE + "}$";

  @Override
  public void initialize(PasswordValidation constraintAnnotation) {
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    boolean isValidPassword = value.matches(REGEX_PASSWORD);

    if(!isValidPassword) {
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate(
              MessageFormat.format(
                  "{0}자 이상의 {1}자 이하의 숫자, 영문자, 특수문자를 포함한 비밀번호를 입력해주세요"
                  , MIN_SIZE
                  , MAX_SIZE))
          .addConstraintViolation();
    }
    return isValidPassword;
  }
}
