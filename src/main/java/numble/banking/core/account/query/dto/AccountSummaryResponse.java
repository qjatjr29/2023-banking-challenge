package numble.banking.core.account.query.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import numble.banking.core.account.command.domain.Account;
import numble.banking.core.account.command.domain.AccountType;
import numble.banking.core.account.command.domain.Bank;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountSummaryResponse {

  private String accountNumber;

  private String accountName;

  private AccountType type;

  private Bank bank;

  protected AccountSummaryResponse(final String accountNumber, final String accountName, final AccountType type, final Bank bank) {
    this.accountNumber = accountNumber;
    this.accountName = accountName;
    this.type = type;
    this.bank = bank;
  }

  public static AccountSummaryResponse of(final Account account) {

    return new AccountSummaryResponse(account.getAccountNumber(), account.getAccountName(), account.getAccountType(), account.getBank());
  }
}
