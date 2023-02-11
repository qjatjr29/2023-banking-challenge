package numble.banking.core.common.error.exception;

import java.util.List;
import numble.banking.core.common.error.ErrorField;
import numble.banking.core.common.error.ErrorCode;

public class BadRequestException extends BusinessException {

  public BadRequestException(ErrorCode errorCode) {
    super(errorCode);
  }

  public BadRequestException(List<ErrorField> errors) {
    super(ErrorCode.BAD_REQUEST, errors);
  }

  public BadRequestException(ErrorCode code, List<ErrorField> errors) {
    super(code, errors);
  }

}
