package numble.banking.core.common.error.exception;

import java.util.List;
import numble.banking.core.common.error.ErrorCode;
import numble.banking.core.common.error.ErrorField;

public class UnAuthorizedException extends BusinessException{

  public UnAuthorizedException(ErrorCode errorCode) {
    super(errorCode);
  }

  public UnAuthorizedException(List<ErrorField> errors) {
    super(ErrorCode.UNAUTHORIZED, errors);
  }

  public UnAuthorizedException(ErrorCode code, List<ErrorField> errors) {
    super(code, errors);
  }

}
