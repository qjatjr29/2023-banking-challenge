package numble.banking.core.common.dto;

import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import numble.banking.core.common.error.ErrorField;
import numble.banking.core.common.error.ErrorCode;
import org.springframework.validation.BindingResult;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponseDto {

  private String resultCode;
  private String message;
  private int statusCode;
  private List<ErrorField> errors;

  public ErrorResponseDto(ErrorCode errorCode, List<ErrorField> errors) {
    this.resultCode = errorCode.getResultCode();
    this.message = errorCode.getMessage();
    this.statusCode = errorCode.getStatusCode();
    this.errors = errors;
  }

  public static ErrorResponseDto from(final ErrorCode errorCode, final BindingResult bindingResult) {
    return new ErrorResponseDto(errorCode, ErrorField.of(bindingResult));
  }

  public static ErrorResponseDto of(final ErrorCode errorCode, final List<ErrorField> code) {
    return new ErrorResponseDto(errorCode, code);
  }

}
