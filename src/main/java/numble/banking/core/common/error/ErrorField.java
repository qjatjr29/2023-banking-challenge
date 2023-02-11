package numble.banking.core.common.error;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorField {

  private String field;
  private String value;
  private String errorMessage;

  private ErrorField(String field, String value, String errorMessage) {
    this.field = field;
    this.value = value;
    this.errorMessage = errorMessage;
  }

  public static ErrorField from(final String field, final String value, final String message) {
    return new ErrorField(field, value, message);
  }

  public static List<ErrorField> of(BindingResult bindingResult) {

    List<FieldError> fieldErrors = bindingResult.getFieldErrors();

    return fieldErrors.stream()
        .map(error -> new ErrorField(
            error.getField(),
            error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
            error.getDefaultMessage()))
        .collect(Collectors.toList());
  }

}
