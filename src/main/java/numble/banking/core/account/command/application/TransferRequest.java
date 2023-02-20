package numble.banking.core.account.command.application;

import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import numble.banking.core.account.command.domain.Money;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TransferRequest {

  @NotNull
  private Long fromAccountId;
  @NotNull
  private Long toAccountId;

  @NotNull
  private Money amount;
  private String accountNumber;
  private String content;

}
