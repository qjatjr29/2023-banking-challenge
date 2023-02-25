package numble.banking.core.account.query.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import numble.banking.core.account.command.domain.Account;
import numble.banking.core.account.command.domain.AccountType;
import numble.banking.core.account.command.domain.Bank;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountSummaryResponse {

  private Long id;

  private String accountNumber;

  private String accountName;

  private AccountType type;

  private Bank bank;

  private LocalDate createdAt;

  protected AccountSummaryResponse(final Account account) {
    this.id = account.getId();
    this.accountNumber = account.getAccountNumber();
    this.accountName = account.getAccountName();
    this.type = account.getAccountType();
    this.bank = account.getBank();
    this.createdAt = LocalDate.from(account.getCreatedAt());
  }

  public static AccountSummaryResponse of(final Account account) {

    return new AccountSummaryResponse(account);
  }
}
