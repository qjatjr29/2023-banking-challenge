package numble.banking.core.common.error.exception;

import java.util.List;
import numble.banking.core.common.error.ErrorField;
import numble.banking.core.common.error.ErrorCode;

public class NotFoundException extends BusinessException{

  public NotFoundException(ErrorCode errorCode) {
    super(errorCode);
  }

  public NotFoundException(List<ErrorField> errors) {
    super(ErrorCode.ENTITY_NOT_FOUND, errors);
  }

  public NotFoundException(String message, ErrorField errorCode) {
    super(message, errorCode);
  }
}
