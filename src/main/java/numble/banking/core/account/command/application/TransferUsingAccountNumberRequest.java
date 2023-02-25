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
public class TransferUsingAccountNumberRequest {
  @NotNull
  private String fromAccountNumber;
  @NotNull
  private String toAccountNumber;

  private String content;

  @NotNull
  private Money amount;
}
