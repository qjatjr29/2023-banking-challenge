package numble.banking.core.account.command.application;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import numble.banking.core.account.command.domain.Money;
import numble.banking.core.account.command.domain.TransferHistory;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransferResponse {

  private String from;
  private String to;

  private Money amount;
  private Money balance;

  private Boolean isDeposit;
  private LocalDateTime transferTime;
  private String content;

  private TransferResponse (String fromName, String toName, TransferHistory history) {
    this.from = fromName;
    this.to = toName;
    this.amount = history.getTransferAmount();
    this.balance = history.getBalance();
    this.isDeposit = history.getIsDeposit();
    this.content = history.getContent();
    this.transferTime = history.getTransferTime();
  }

  public static TransferResponse from(String fromName, String toName, TransferHistory history) {
    return new TransferResponse(fromName, toName, history);
  }
}
