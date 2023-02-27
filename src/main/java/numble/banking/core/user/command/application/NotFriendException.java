package numble.banking.core.user.command.application;

import java.util.List;
import numble.banking.core.common.error.ErrorCode;
import numble.banking.core.common.error.ErrorField;
import numble.banking.core.common.error.exception.BadRequestException;

public class NotFriendException extends BadRequestException {

  public NotFriendException(ErrorCode errorCode) {
    super(errorCode);
  }

  public NotFriendException(List<ErrorField> errors) {
    super(ErrorCode.INSUFFICIENT_QUALIFICATIONS_FRIEND, errors);
  }

  public NotFriendException(ErrorCode code, List<ErrorField> errors) {
    super(code, errors);
  }

}
