package numble.banking.core.account.command.application;

import java.util.List;
import numble.banking.core.common.error.ErrorCode;
import numble.banking.core.common.error.ErrorField;
import numble.banking.core.common.error.exception.BadRequestException;

public class InsufficientBalanceException extends BadRequestException  {

  public InsufficientBalanceException(ErrorCode errorCode) {
    super(errorCode);
  }

  public InsufficientBalanceException(List<ErrorField> errors) {
    super(ErrorCode.OVER_AMOUNT_CURRENT_VALUE, errors);
  }

  public InsufficientBalanceException(ErrorCode code, List<ErrorField> errors) {
    super(code, errors);
  }
}
