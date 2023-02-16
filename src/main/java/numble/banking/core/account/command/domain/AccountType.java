package numble.banking.core.account.command.domain;

import lombok.Getter;

@Getter
public enum AccountType {
  DEPOSIT,
  SAVINGS,
  STOCK;

  public static AccountType getAccountType(String type) {
    return AccountType.valueOf(type.toUpperCase());
  }

}
