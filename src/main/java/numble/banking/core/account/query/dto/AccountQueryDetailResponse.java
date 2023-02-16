package numble.banking.core.account.query.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import numble.banking.core.account.command.domain.Account;
import numble.banking.core.account.command.domain.AccountType;
import numble.banking.core.account.command.domain.Bank;
import numble.banking.core.account.command.domain.TransferHistory;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountQueryDetailResponse {

  private Long accountId;
  private String accountName;
  private String accountNumber;
  private AccountType accountType;
  private Bank bank;
  private LocalDate openDate;
  private List<TransferHistory> transferHistories;

  private AccountQueryDetailResponse(final Account account) {
    this.accountId = account.getId();
    this.accountName = account.getAccountName();
    this.accountNumber = account.getAccountNumber();
    this.accountType = account.getAccountType();
    this.bank = account.getBank();
    this.openDate = LocalDate.from(account.getCreatedAt());
    this.transferHistories = account.getTransferHistories();
  }

  public static AccountQueryDetailResponse of(final Account account) {
    return new AccountQueryDetailResponse(account);
  }
}
