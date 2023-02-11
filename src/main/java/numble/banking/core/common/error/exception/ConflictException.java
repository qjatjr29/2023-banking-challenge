package numble.banking.core.common.error.exception;

import java.util.List;
import numble.banking.core.common.error.ErrorField;
import numble.banking.core.common.error.ErrorCode;

public class ConflictException extends BusinessException {

  public ConflictException(ErrorCode errorCode) {
    super(errorCode);
  }

  public ConflictException(List<ErrorField> errors) {
    super(ErrorCode.DUPLICATE_INPUT_VALUE, errors);
  }

  public ConflictException(ErrorCode code, List<ErrorField> errors) {
    super(code, errors);
  }

}
