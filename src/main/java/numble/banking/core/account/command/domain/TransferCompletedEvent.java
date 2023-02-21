package numble.banking.core.account.command.domain;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import numble.banking.core.common.event.Event;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransferCompletedEvent extends Event {

  private String toUserName;
  private String accountNumber;
  private Money amount;
  private Money balance;
  private boolean isDeposit;
  private LocalDateTime transferTime;

  public TransferCompletedEvent(String toUserName, String accountNumber,
      Money amount, Money balance, boolean isDeposit, LocalDateTime transferTime) {
    super();
    this.toUserName = toUserName;
    this.accountNumber = accountNumber;
    this.amount = amount;
    this.balance = balance;
    this.isDeposit = isDeposit;
    this.transferTime = transferTime;
  }
}
