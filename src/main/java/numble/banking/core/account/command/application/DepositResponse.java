package numble.banking.core.account.command.application;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import numble.banking.core.account.command.domain.Money;
import numble.banking.core.account.command.domain.TransferHistory;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DepositResponse {

  private Money amount;
  private Money balance;

  private Boolean isDeposit;
  private LocalDateTime transferTime;

  private DepositResponse (TransferHistory history) {

    this.amount = history.getTransferAmount();
    this.balance = history.getBalance();
    this.isDeposit = history.getIsDeposit();
    this.transferTime = history.getTransferTime();
  }

  public static DepositResponse of(TransferHistory history) {
    return new DepositResponse(history);
  }

}
